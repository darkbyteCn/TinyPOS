var gUtils = require('./Utils');

exports.getDocs = (req, res, next, from, query, projection, sortBy) => {
	var skip = parseInt(req.query.skip) || 0;
	var limit = Math.min(Math.max(parseInt(req.query.limit) || 50, 0), 100);
	if(sortBy == null)
		sortBy = {_id: parseInt(req.query.sortDirection) || 1};

	var cursor = req.app.locals.db.collection(from).find(query || {})
		.sort(sortBy)
		.skip(skip)
		.limit(limit);

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
		{ upsert: true, returnOriginal: false }
	).then(function(r) {
		return r.value.val;
	});
};


exports.search = search = (db, docName, keywords, skip, limit, opts) => {
	opts = opts || {};

	var regexList = [],
		strcmpList = [];
	for(var keyword of keywords) {
		regexList.push({"keywords.name": {$regex: '^' + keyword}});
		strcmpList.push({$eq: [{$substr: ["$$keyword.name", 0, keyword.length]}, keyword]});
	}

	var keywordFilter = {
		$filter: {input: "$keywords", as: "keyword", cond: {$or: strcmpList}}
	};
	var weightMapper = {
		input: "$keywords",
		as: "keyword",
		in: {
			$multiply: [
				"$$keyword.weight",
				{
					$cond: [
						{$setIsSubset: [["$$keyword.name"], keywords]},
						opts.matchedFactor || 3,
						1
					]
				}
			]
		}
	};

	var stages = [
		{$match: {$and: regexList}},
		{$project: {keywords: keywordFilter}},
		{$project: {weights: {$map: weightMapper}}},
		{$unwind: '$weights'},
		{$group: {_id: "$_id", weight: {$sum: "$weights"}}},
		{$sort: {weight:-1, _id:-1}},
		{$skip: skip},
		{$limit: limit}
	];

	if(!opts.idOnly)
		stages.push({
			$lookup: {
				from: docName,
				localField: "_id",
				foreignField: "_id",
				as: "doc"
	        }
   		});

	return db.collection(docName).aggregate(stages).toArray();
};


exports.getSearchResult = (req, res, next, collection) => {
	var skip = parseInt(req.query.skip) || 0;
	var limit = parseInt(req.query.limit) || 50;
	
	new Promise((resolve, reject) => {
		var keywords = gUtils.parseTerms(req.query.terms || "");
		if(keywords == null || keywords.length == 0)
			resolve([]);
		else
			resolve(search(req.app.locals.db, collection, keywords, skip, limit));

	}).then((results) => {
		var docs = [];
		for(var doc of results) {
			if(doc.doc.length != 0)
				delete doc.doc[0].keywords;
			docs.push(doc.doc[0])
		}
		res.json({total: -1, docs: docs});

	}).catch((err) => {
		next(err);

	});
}