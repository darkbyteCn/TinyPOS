var gExpress = require('express');
var gCookieParser = require('cookie-parser');
var gBodyParser = require('body-parser');
var gDoc = require('../lib/Doc');
var gUtils = require('../lib/Utils');
var gError= require('../lib/Error');
var gCustomer= require('../lib/Customer');
var gDataModel = require('../lib/DataModel');

var gJsonParser = gBodyParser.json();


var gRouter = gExpress.Router();
module.exports = {path: "/Customer", route: gRouter};

gRouter.get('/getTickets', getTickets);
gRouter.get('/getDocs', getDocs);
gRouter.get('/getDoc', getDoc);
gRouter.get('/search', search);
gRouter.post('/newDoc', gJsonParser, newDoc);
gRouter.post('/updateDoc', gJsonParser, updateDoc);

function getDocs(req, res, next) {
	gDoc.getDocs(req, res, next, 'Customer', null, {keywords: 0});
}

function getDoc(req, res, next) {
	gDoc.getDoc(req, res, next, 'Customer', {_id: parseInt(req.query._id)}, {keywords: 0});
}

function getTickets(req, res, next) {
	gDoc.getDocs(req, res, next,
		'Ticket',
		{'customer._id': parseInt(req.query._id)},
		{keywords: 0}
	);
}

function newDoc(req, res, next) {
	var newDoc = req.body;

	return new Promise((resolve, reject) => {
		if(typeof newDoc != 'object')
			resolve(gError.UserErrorPromise("invalid input"));
		else
			resolve(gDataModel.filterCustomer(newDoc));

	}).then((doc) => {
		newDoc = doc;
		newDoc._id = null;
		return gCustomer.newCustomer(req.app, newDoc);

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
			resolve(gError.UserErrorPromise("invalid input"));
		else
			resolve(gDataModel.filterCustomer(newDoc));

	}).then((doc) => {
		newDoc = doc;
		return gCustomer.updateCustomer(req.app, newDoc);

	}).then(() => {
		res.json({_id: newDoc._id});

	}).catch(function(err) {
		next(err);

	});

}

function search(req, res, next) {
	return gDoc.getSearchResult(req, res, next, "Customer");
}

