
exports.getDocs = (req, res, next, from, query, projection, sortBy) => {
	var pageIndex = parseInt(req.query.pageIndex) || 0;
	var pageSize = Math.max(parseInt(req.query.pageSize) || 50, 100);
	if(sortBy == null)
		sortBy = {_id: parseInt(req.query.sortDirection) || 1};

	cursor = req.app.locals.db.collection(from).find(query || {})
		.sort(sortBy)
		.skip(pageIndex * pageSize)
		.limit(pageSize);

	if(projection != null) cursor.project(projection);

	var ret = {total: 0, docs: []};
	return cursor.count().then(function(count) {
		ret.total = count;
		return cursor.toArray();

	}).then(function(docs) {
		ret.docs = docs;
		res.json(ret);
		
	}).catch(function(err) {
		next(err);

	});
}

exports.getDoc = (req, res, next, from, query, projection) => {
	var cursor;
	return new Promise(function(resolve, reject) {
		if(typeof query == 'function')
			query = query();

		if(query == null) {
			reject("No key " + byCol);
			return;
		}

		cursor = req.app.locals.db.collection(from).find(query).limit(1);
		if(projection != null) cursor.project(projection);

		resolve(cursor.toArray());

	}).then(function(docs) {
		res.json(docs.length > 0 ? docs[0] : null);

	}).catch(function(err) {
		next(err);

	});

}

exports.getNewID = (db, docName, numOfIds) => {
	return db.collection("DocId").findOneAndUpdate(
		{ name: docName },
		{ $inc: {val: numOfIds || 1}},
		{ upsert: true, returnNewDocument: true }
	).then(function(r) {
		return r.value.val;
	});
};
