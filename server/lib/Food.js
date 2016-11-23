var gDoc = require('./Doc');
var gDocEvent = require('./DocEvent');
var gSyncableDoc = require('./SyncableDoc');
var gError= require('./Error');
var gLogger = require('./Logger');
var gUtils = require('./Utils');

var SearchWeights = [1, 5];
function generateKeywords(categoryName, foodName) {
	var indexes = [[], []];

	if(categoryName) indexes[0].push(categoryName);
	if(foodName) indexes[1].push(foodName);

	var keywordMap = {};
	for(var i = 0; i < indexes.length; i++) {
		var rawKeywords = gUtils.parseTerms(indexes[i].join(' '));
		if(rawKeywords == null) continue;

		for(var kw of rawKeywords) {
			if(Object.prototype.hasOwnProperty.call(keywordMap, kw))
				keywordMap[kw] += SearchWeights[i];
			else
				keywordMap[kw] = SearchWeights[i];
		}
	}
	
	var keywords = [];
	for(var kw in keywordMap)
		keywords.push({name: kw, weight: keywordMap[kw]});

	return keywords;
}

exports.reindexAll = (app) => {
	var db = app.locals.db;
	return db.collection("Menu").find()
	.toArray()
	.then((docs) => {
		var map = {};
		for(var doc of docs)
			map[doc._id] = doc

		var promises = [];
		for(var doc of docs) {
			if(doc.foodId == 0) continue;

			var keywords = generateKeywords((map[doc.categoryId] || {}).menuName, doc.menuName);
			promises.push(
				db.collection("Food").update({_id: doc.foodId}, {$set: {keywords: keywords}})
			);
		}

		return Promise.all(promises);
	});
}