var gExpress = require('express');
var gCookieParser = require('cookie-parser');
var gBodyParser = require('body-parser');
var gDoc = require('../lib/Doc');
var gDocEvent = require('../lib/DocEvent');
var gDataModel = require('../lib/DataModel');
var gDineTable = require('../lib/DineTable');
var gTicket = require('../lib/Ticket');
var gError = require('../lib/Error');

var gJsonParser = gBodyParser.json();

var gRouter = gExpress.Router();
module.exports = {path: "/Ticket", route: gRouter};

gRouter.get('/getDocs', getDocs);
gRouter.get('/getDoc', getDoc);
gRouter.get('/getSyncDocs', getSyncDocs);
gRouter.post('/newDoc', gJsonParser, newDoc);
gRouter.post('/updateDoc', gJsonParser, updateDoc);
gRouter.post('/checkout', gJsonParser, checkout);
gRouter.post('/fulfill', gJsonParser, fulfill);
gRouter.post('/deleteDoc', gJsonParser, deleteDoc);
gRouter.get('/search', search);
gRouter.get('/getTicketsByPage', getTicketsByPage);

function getDocs(req, res, next) {
	gDoc.getDocs(req, res, next, 'Ticket', null, {keywords: 0});
}

function getDoc(req, res, next) {
	gDoc.getDoc(req, res, next, 'Ticket', {_id: parseInt(req.query._id)}, {keywords: 0});
}

function getSyncDocs(req, res, next) {
	var fromId = Math.max(0, parseInt(req.query.fromId) || 0);
	var filter = {
		_id: {$gt: fromId},
		state: {$bitsAllClear: gDataModel.Ticket.STATE_COMPLETED},
		dbDeleted: {$ne: 1}
	};
	gDoc.getDocs(req, res, next, 'Ticket', filter, {keywords: 0}, {_id: 1});
}

function validate(doc) {
	//simple validator, need more.... 

	if(doc.tableId != -1 && doc.tableId != -2 && doc.tableId < 1)
		return gError.UserErrorPromise("invalid tableId");
	else if((doc.tableId == -1 || doc.tableId == -2) && (doc.customer || {})._id == 0)
		return gError.UserErrorPromise("invalid customer");

	return doc;
}

function newDoc(req, res, next) {
	var newDoc = req.body;

	return new Promise((resolve, reject) => {
		resolve(validate(gDataModel.filterTicket(newDoc)));

	}).then((doc) => {
		newDoc = doc;
		return gTicket.newTicket(req.app, newDoc, parseInt(req.query.payMode));

	}).then((result) => {
		res.json(result);

	}).catch(function(err) {
		next(err);

	});
}

function updateDoc(req, res, next) {
	var newDoc = req.body;

	return new Promise((resolve, reject) => {
		resolve(validate(gDataModel.filterTicket(newDoc)));

	}).then((doc) => {
		newDoc = doc;
		return gTicket.updateTicket(req.app, newDoc, parseInt(req.query.payMode));

	}).then((result) => {
		res.json(result);

	}).catch(function(err) {
		next(err);

	});

}

function checkout(req, res, next) {
	var doc = req.body || {};
	var ticketId = parseInt(doc._id) || 0;

	return gTicket.closeTicketTable(req.app, ticketId)
	.then((success) => {
		res.json({success: success});

	}).catch(function(err) {
		next(err);

	});
}

function fulfill(req, res, next) {
	var ticketMap = req.body || {};
	var ticketList = [];

	return new Promise((resolve, reject) => {
		var promises = [];
		for(var ticketId in ticketMap) {
			var foodMap = ticketMap[ticketId];
			ticketId = parseInt(ticketId);
			if(!ticketId) continue;
  
			var vfoodMap = {};
			for(var pos in foodMap)
				vfoodMap[parseInt(pos)] = parseInt(foodMap[pos]) || 0;
			promises.push(gTicket.fulfill(req.app, ticketId, vfoodMap));
			ticketList.push({_id: ticketId});
		}
		resolve(Promise.all(promises));

	}).then((resultList) => {
		res.json({success: true, resultList: resultList});

	}).catch(function(err) {
		next(err);

	});
	
}

function deleteDoc(req, res, next) {
	var ticket = req.body || {};
	var stateMask = gDataModel.Ticket.STATE_PAID;
	var ticketId = parseInt(ticket._id) || 0;

	return gTicket.deleteDoc(
		req.app,
		{_id: ticketId, state: {$bitsAllClear: stateMask}, payments: {$size: 0}}
	).then((success) => {
		res.json({success : success});

	}).catch(function(err) {
		next(err);

	});
}

function search(req, res, next) {
	return gDoc.getSearchResult(req, res, next, "Ticket");
}

function getTicketsByPage(req, res, next) {
	var pageIndex = parseInt(req.query.pageIndex) || 0;
	var pageSize = Math.min(Math.max(parseInt(req.query.pageSize) || 50, 0), 100);

	var skip = pageIndex * pageSize;
	var limit = pageSize;
	var ret = {total: 0, pages: {}};

	var cursor = req.app.locals.db.collection("Ticket").find({})
	.sort({_id: -1})
	.skip(skip)
	.limit(limit)
	.project({keywords: 0});

	return cursor.count()
	.then((count) => {
		ret.total = count;
		return cursor.toArray();

	}).then((docs) => {
		ret.pages[pageIndex] = docs || [];
		ret.success = true;
		res.json(ret);

	}).catch(function(err) {
		next(err);

	});
}