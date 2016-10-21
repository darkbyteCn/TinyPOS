var gExpress = require('express');
var gCookieParser = require('cookie-parser');
var gBodyParser = require('body-parser');
var gDoc = require('../lib/Doc');
var gDocEvent = require('../lib/DocEvent');
var gDataModel = require('../lib/DataModel');
var gDineTable = require('../lib/DineTable');
var gTicket = require('../lib/Ticket');
var gError= require('../lib/Error');

var gJsonParser = gBodyParser.json();

var gRouter = gExpress.Router();
module.exports = {path: "/Ticket", route: gRouter};

gRouter.get('/getDocs', getDocs);
gRouter.get('/getDoc', getDoc);
gRouter.get('/getSyncDocs', getSyncDocs);
gRouter.post('/newDoc', gJsonParser, newDoc);
gRouter.post('/updateDoc', gJsonParser, updateDoc);
gRouter.post('/checkout', gJsonParser, checkout);


function getDocs(req, res, next) {
	gDoc.getDocs(req, res, next, 'Ticket', null, null);
}

function getDoc(req, res, next) {
	gDoc.getDocs(req, res, next, 'Ticket', {_id: parseInt(req.query._id)}, null);
}

function getSyncDocs(req, res, next) {
	var fromId = Math.max(0, parseInt(req.query.fromId) || 0);
	gDoc.getDocs(req, res, next, 'Ticket', {state: 1, _id: {$gt: fromId}}, null, {_id: 1});
}

function newDoc(req, res, next) {
	var newDoc = req.body;

	return new Promise((resolve, reject) => {
		if(typeof newDoc != 'object')
			reject(gError.UserErrorPromise("invalid input"));
		else
			resolve(gDataModel.filterTicket(newDoc));

	}).then((doc) => {
		newDoc = doc;
		return gTicket.newTicket(req.app, newDoc);

	}).then((id) => {
		res.json({_id: id});

	}).catch(function(err) {
		next(err);

	});
}

function updateDoc(req, res, next) {
	var newDoc = req.body;

	return new Promise((resolve, reject) => {
		if(typeof newDoc != 'object')
			reject(gError.UserErrorPromise("invalid input"));
		else
			resolve(gDataModel.filterTicket(newDoc));

	}).then((doc) => {
		newDoc = doc;
		return gTicket.updateTicket(req.app, newDoc);

	}).then(() => {
		res.json({_id: newDoc._id});

	}).catch(function(err) {
		next(err);

	});

}

function checkout(req, res, next) {
	var doc = req.body || {};
	var ticketId = parseInt(doc.ticketId) || 0;

	return gTicket.checkTicket(req.app, ticketId)
	.then((success) => {
		res.json({success: success});

	}).catch(function(err) {
		next(err);

	});
}
