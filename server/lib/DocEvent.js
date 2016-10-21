var gDoc = require('./Doc');
var gDataObserver = require('./DataObserver');
var gLogger = require('./Logger');

var gDocEventObserver = gDataObserver();
exports.registerDocEventObserver = gDocEventObserver.register.bind(gDocEventObserver);
exports.unregisterDocEventObserver = gDocEventObserver.unregister.bind(gDocEventObserver);

exports.getDocEventLastId = (app) => {
	if(app.locals.docEventLastId != null) {
		return Promise.resolve(app.locals.docEventLastId);
	} else {
		return app.locals.db.collection("DocEvent")
		.find()
		.project({_id: 1})
		.sort({_id: -1})
		.limit(1)
		.toArray()
		.then((docs) => {
			var id = docs.length > 0 ? docs[0]._id : 0;
			if(app.locals.docEventLastId == null || id > app.locals.docEventLastId)
				app.locals.docEventLastId = id;
			
			return app.locals.docEventLastId;
		});
	}
}

var gDocEvent = {
	isProgressing: false,
	list: []
};
exports.recordDocEvent = (app, docName, docId, eventType) => {
	var event = {
		docName: docName,
		docId: docId,
		event: eventType,
		ts: parseInt((new Date()).getTime() / 1000)
	};
	gDocEvent.list.push(event);
	gLogger.info("recordDocEvent ->", event);

	if(!gDocEvent.isProgressing) _recordDocEvent(app);
}

function _recordDocEvent(app) {
	if(!gDocEvent.list.length) {
		gDocEvent.isProgressing = false;
		return;
	}

	gDocEvent.isProgressing = true;
	var list = gDocEvent.list.slice(0, 100);
	var id;
	
	return gDoc.getNewID(app.locals.db, "DocEvent", list.length)
	.then((_id) => {
		id = _id - list.length;
		for(var item of list)
			item._id = ++id;

		return app.locals.db.collection("DocEvent").insertMany(list);

	}).then(() => {
		if(app.locals.docEventLastId == null || id > app.locals.docEventLastId)
			app.locals.docEventLastId = id;
			
		gDocEventObserver.notifyChanged();

		for(var item of list) {
			app.locals.db.collection(item.docName)
			.update(
				{_id: item.docID, dbChanged: {$gt: 0}},
				{$inc: {dbChanged: -1}}
			);
		}

		gDocEvent.list.splice(0, list.length);

	}).catch((err) => {
		gLogger.error(err);
		return new Promise((resolve, reject) => {
			setTimeout(resolve, 3000);
		});

	}).then(() => {
		_recordDocEvent(app);

	});
}
