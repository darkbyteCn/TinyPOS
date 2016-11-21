var gDoc = require('./Doc');
var gDocEvent = require('./DocEvent');
var gSyncableDoc = require('./SyncableDoc');
var gError= require('./Error');
var gLogger = require('./Logger');
var gUtils = require('./Utils');

var newCustomer = exports.newCustomer = (app, doc) => {
	var db = app.locals.db;
	var curTs = new Date().getTime();
	doc = Object.assign({}, doc);

	doc.dbCreatedTime = curTs;
	doc.dbRev = 1;

	doc.keywords = generateKeywords(doc);
	return gSyncableDoc.newSyncableDoc(app, "Customer", doc);
};

var SearchWeights = [1, 5, 7];
var StreetRegex = /\b( st| ave)\b/gim;
function generateKeywords(doc) {
	var indexes = [[], [], []];

	if(doc.zipCode) indexes[0].push(doc.zipCode);
	if(doc.city) indexes[0].push(doc.city);
	if(doc.state) indexes[0].push(doc.state);

	if(doc.phone) indexes[2].push(doc.phone);
	if(doc.name) indexes[2].push(doc.name);

	var address = (doc.address || "") + " " + (doc.address2 || "");
	address = address.replace(StreetRegex, (s) => {
		indexes[0].push(s);
		return ' ';
	});
	indexes[1].push(address);

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

var updateCustomer = exports.updateCustomer = (app, newDoc) => {
	var db = app.locals.db;
	var curDoc,
		updDoc;

	updDoc = Object.assign({}, newDoc, {dbRev: newDoc.dbRev + 1});
	delete updDoc._id;
	delete updDoc.dbCreatedTime;
	updDoc.keywords = generateKeywords(updDoc);

	return gSyncableDoc.updateSyncableDoc(
		app,
		"Customer",
		{_id: newDoc._id, dbRev: newDoc.dbRev},
		{$set: updDoc}
	);

}