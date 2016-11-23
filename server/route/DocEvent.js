var gExpress = require('express');
var gDoc = require('../lib/Doc');
var gDocEvent = require('../lib/DocEvent');
var gLogger = require('../lib/Logger');
var gError= require('../lib/Error');


var gRouter = gExpress.Router();
module.exports = {path: "/DocEvent", route: gRouter};

gRouter.get('/getDocs', getDocs);
gRouter.get('/getLastId', getLastId);


function getLastId(req, res, next) {
	gDocEvent.getDocEventLastId(req.app).then((docEventLastId) => {
		res.json({lastId: docEventLastId});
	});
}

function getDocs(req, res, next) {
	var fromId = parseInt(req.query.fromId) || 0;
	var syncColls = new Set(String(req.query.sync).split(','));
	var db = req.app.locals.db;
	var collection = db.collection("DocEvent");
	var ret = {};
	var docsByColl = {};

	return gDocEvent.getDocEventLastId(req.app)
	.then((docEventLastId) => {
		var lastId = docEventLastId || 0;
		var toId = Math.min(fromId + 100, lastId);

		ret.toId = toId;
		ret.lastId = lastId;
		if(toId <= fromId) {
			return [];
		} else {
			return collection.aggregate([
				{ $match: {_id: {$gt: fromId, $lte: toId}} },
				{ $group: {_id: {docId: '$docId', docName: '$docName'}, event: {$sum: "$event"}} }
			]).toArray();
		}

	}).then((docs) => {
		//console.log(docs);
		for(var doc of docs) {

			if(!Object.prototype.hasOwnProperty.call(docsByColl, doc._id.docName))
				docsByColl[doc._id.docName] = {deleted: [], updated: []};

			if(doc.event)
				docsByColl[doc._id.docName].deleted.push(doc._id.docId);
			else
				docsByColl[doc._id.docName].updated.push(doc._id.docId);
		}

		var promises = [];
		for(var name in docsByColl) {
			var recs = docsByColl[name];

			promises.push(
				recs.updated.length && syncColls.has(name)
				? db.collection(name).find({_id: {$in: recs.updated}}).toArray()
				: recs.updated
			);
		}

		return Promise.all(promises);

	}).then((results) => {
		var names = Object.keys(docsByColl);
		for(var i = 0; i < names.length; i++) {
			var recs = docsByColl[names[i]];
			recs.updated = results[i] || [];
		}

		ret.docsByColl = docsByColl;
		res.json(ret);

	}).catch((err) => {
		next(err);
	});
}