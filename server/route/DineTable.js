var gExpress = require('express');
var gCookieParser = require('cookie-parser');
var gBodyParser = require('body-parser');
var gDoc = require('../lib/Doc');

var gJsonParser = gBodyParser.json();



var gRouter = gExpress.Router();
module.exports = {path: "/DineTable", route: gRouter};

gRouter.get('/getDocs', getDocs);
gRouter.get('/getDoc', getDoc);
gRouter.get('/getSyncDocs', getSyncDocs);


function getDocs(req, res, next) {
	gDoc.getDocs(req, res, next, 'DineTable', null, null);
}

function getDoc(req, res, next) {
	gDoc.getDocs(req, res, next, 'DineTable', {_id: parseInt(req.query._id)}, null);
}

function getSyncDocs(req, res, next) {
	var fromId = Math.max(0, parseInt(req.query.fromId) || 0);
	var filter = {
		_id: {$gt: fromId},
		dbDeleted: {$ne: 1}
	};
	gDoc.getDocs(req, res, next, 'DineTable', filter, null, {_id: 1});
}
