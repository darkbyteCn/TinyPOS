var gSyncableDoc = require('./SyncableDoc');

var openDineTable = exports.openDineTable = (app, dineTableId, ticketId) => {
	return gSyncableDoc.updateSyncableDoc(app, "DineTable",
		{_id: dineTableId, ticketId: 0},
		{$set: {ticketId: ticketId}}
	);
};

var closeDineTable = exports.closeDineTable = (app, dineTableId, ticketId) => {
	return gSyncableDoc.updateSyncableDoc(app, "DineTable",
		{_id: dineTableId, ticketId: ticketId},
		{$set: {ticketId: 0}}
	);
};

var isDineTableAvailable = exports.isDineTableAvailable = (app, dineTableId) => {
	var db = app.locals.db;
	return db.collection("DineTable").count(
		{_id: dineTableId, ticketId: 0, dbDeleted: {$ne: 1}}
	).then((count) => {
		return count > 0 ? true : false;
	});
};
