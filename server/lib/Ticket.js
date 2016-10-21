var gDoc = require('./Doc');
var gDocEvent = require('./DocEvent');
var gDineTable = require('./DineTable');
var gSyncableDoc = require('./SyncableDoc');
var gError= require('./Error');
var gLogger = require('./Logger');

var newTicket = exports.newTicket = (app, doc) => {
	var db = app.locals.db;
	var curTs = new Date().getTime();
	doc = Object.assign({}, doc);

	if(doc.createdTime == null || doc.createdTime == 0) doc.createdTime = curTs;
	doc.dbCreatedTime = curTs;
	doc.dbRev = 1;
	doc.state = 1;

	if(doc.tableId > 0)
		p = gDineTable.isDineTableAvailable(app, doc.tableId)
		.then((success) => {
			if(!success)
				return gError.UserErrorPromise(`Dine Table Id#${doc.tableId} is not available`);
		});
	else
		p = Promise.resolve();

	return p.then(() => {
		return gDoc.getNewID(db, "Ticket");

	}).then((id) => {
		doc._id = id;
		if(doc.tableId > 0)
			return gDineTable.openDineTable(app, doc.tableId, doc._id)
			.then((success) => {
				if(!success)
					return gError.UserErrorPromise(`Can't Open Dine Table #${doc.tableId}`);
			});

	}).then(() => {
		return gSyncableDoc.newSyncableDoc(app, "Ticket", doc);

	}).then((id) => {
		return id;

	});
};

var updateTicket = exports.updateTicket = (app, newDoc) => {
	var db = app.locals.db;
	var curDoc,
		updDoc = {$set: {}};

	return db.collection("Ticket").findOne({_id: newDoc._id, state: {$ne: 9}})
	.then((doc) => {
		if(doc == null)
			return gError.UserErrorPromise(`Can't find Ticket #${newDoc._id}`);

		curDoc = doc;
		if(curDoc.tableId != newDoc.tableId && newDoc.tableId > 0)
			return gDineTable.openDineTable(app, newDoc.tableId, newDoc._id)
			.then((success) => {
				if(!success)
					return gError.UserErrorPromise(`Can't Open DineTable #${newDoc.tableId}`);
			});

	}).then(() => {
		updDoc.$set = Object.assign({}, newDoc, {dbRev: newDoc.dbRev + 1});
		delete updDoc.$set._id;
		delete updDoc.$set.dbCreatedTime;

		return gSyncableDoc.updateSyncableDoc(app, "Ticket", {
			_id: newDoc._id,
			dbRev: newDoc.dbRev,
			state: {$ne: 9}
		}, updDoc);

	}).then((success) => {
		if(!success) return false;
		if(curDoc.tableId != newDoc.tableId && curDoc.tableId > 0)
			return gDineTable.closeDineTable(app, curDoc.tableId, newDoc._id)
			.then((success) => {
				if(!success)
					return gError.UserErrorPromise(`Can't Close DineTable #${curDoc.tableId}`);
				return true;
			});

		return true;
	});
};

var checkTicket = exports.checkTicket = (app, ticketId) => {
	var db = app.locals.db;

	return gSyncableDoc.findAndUpdateSyncableDoc(app, "Ticket",
		{_id: ticketId, state: {$ne: 9}},
		{$inc: {dbRev: 1}, $set: {state: 9}},
		{projection: {tableId: 1}}
	).then((doc) => {
		if(doc != null && doc.tableId > 0)
			return gDineTable.closeDineTable(app, doc.tableId, ticketId)
			.then((success) => {
				if(!success)
					return gError.UserErrorPromise(`Can't Close DineTable #${doc.tableId}`);
				return true;
			});
		return doc != null;
	});
}
