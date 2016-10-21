var gDoc = require('./Doc');
var gDocEvent = require('./DocEvent');

exports.newSyncableDoc = (app, name, doc) => {
	var db = app.locals.db;
	var curTs = new Date().getTime();

	return (doc._id == null ? gDoc.getNewID(db, name) : Promise.resolve(doc._id))
	.then((id) => {
		doc = Object.assign({}, doc, {_id: id, dbModifiedTime: curTs, dbChanged: 1, dbDeleted: 0})
		return db.collection(name).insertOne(doc);
	}).then((r) => {
		if(r.insertedCount > 0) {
			gDocEvent.recordDocEvent(app, name, doc._id, 0);
			return doc._id;
		}
	});
};

function prepareUpdateDoc(doc) {
	var curTs = new Date().getTime();
	doc = Object.assign({}, doc);
	doc.$set = Object.assign({}, doc.$set || {}, {dbModifiedTime: curTs});
	doc.$inc = Object.assign({}, doc.$inc || {}, {dbChanged: 1});
	return doc;
}

exports.updateSyncableDoc = (app, name, query, doc, opts) => {
	var db = app.locals.db;
	query = Object.assign({}, query, {dbDeleted: {$ne: 1}});

	return db.collection(name).updateOne(query, prepareUpdateDoc(doc), opts)
	.then((result) => {
		if(result.modifiedCount > 0) {
			gDocEvent.recordDocEvent(app, name, query._id, 0);
			return true;
		}
	});
};

exports.findAndUpdateSyncableDoc = (app, name, query, doc, opts) => {
	var db = app.locals.db;
	query = Object.assign({}, query, {dbDeleted: {$ne: 1}});

	return db.collection(name).findOneAndUpdate(query, prepareUpdateDoc(doc), opts)
	.then((result) => {
		if(result.lastErrorObject.n > 0)
			gDocEvent.recordDocEvent(app, name, query._id, 0);
		return result.value;
	});
};

exports.deleteSyncableDoc = (app, name, id, opts) => {
	var db = app.locals.db;

	return db.collection(name).updateOne(
		{_id: id, dbDeleted: {$ne: 1}},
		prepareUpdateDoc({dbDeleted: 1}),
		opts
	).then((result) => {
		if(result.modifiedCount > 0) {
			gDocEvent.recordDocEvent(app, name, id, 1);
			return true;
		}
	});
};

