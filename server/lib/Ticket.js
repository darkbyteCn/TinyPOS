var gDoc = require('./Doc');
var gDocEvent = require('./DocEvent');
var gDineTable = require('./DineTable');
var gSyncableDoc = require('./SyncableDoc');
var gError= require('./Error');
var gLogger = require('./Logger');
var gDataModel = require('./DataModel');
var gUtils = require('./Utils');

var newTicket = exports.newTicket = (app, doc, shouldFinalize) => {
	var db = app.locals.db;
	var curTs = new Date().getTime();
	doc = Object.assign({}, doc);
	var changeGiven = 0.0;

	if(doc.createdTime == null || doc.createdTime == 0) doc.createdTime = curTs;
	doc.dbCreatedTime = curTs;
	doc.dbRev = 1;
	doc.state = 0;

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

		sanitizeTicket(doc);
		if(shouldFinalize) changeGiven = doc.balance;
		setTicketState(doc, shouldFinalize);

		if(doc.tableId > 0)
			return gDineTable.openDineTable(app, doc.tableId, doc._id)
			.then((success) => {
				if(!success)
					return gError.UserErrorPromise(`Can't Open Dine Table #${doc.tableId}`);
			});

	}).then(() => {
		return gSyncableDoc.newSyncableDoc(app, "Ticket", doc);

	}).then((_id) => {
		return {success: _id != null, _id: _id, changeGiven: changeGiven};
	});
};


function sanitizeTicket(doc) {
	var totalFullfilled = 0;
	var totalNumFood = 0;
	var subtotal = 0;
	var tax = 0;
	var lastItemId = 0;

	if(doc.foodItems == null) doc.foodItems = [];
	for(var foodItem of doc.foodItems) {
		if(foodItem.itemId <= lastItemId)
			throw gError.UserError(`Ticket #${doc._id} - ItemId is Invalid`);
		lastItemId = foodItem.itemId;

		foodItem.fulfilled = Math.max(0, Math.min(foodItem.fulfilled, foodItem.quantity));
		totalFullfilled += foodItem.fulfilled;
		if(foodItem.quantity > 0) totalNumFood += foodItem.quantity;

		foodItem.price = gUtils.round(foodItem.price, 2);
		foodItem.exPrice = gUtils.round(foodItem.price * foodItem.quantity, 2);
		subtotal += foodItem.exPrice;
		tax += gUtils.round(foodItem.exPrice * foodItem.taxRate, 2);
	}

	doc.numFoodFullfilled = totalFullfilled;
	doc.numFood = totalNumFood;
	doc.subtotal = gUtils.round(subtotal, 2);
	doc.tax = gUtils.round(tax, 2);
	doc.fee = gUtils.round(doc.fee, 2);
	doc.tips = gUtils.round(doc.tips, 2);
	doc.total = gUtils.round(doc.subtotal + doc.tax + doc.fee, 2);

	var totalPaidAmount = 0.0;
	if(doc.payments == null) doc.payments = [];
	for(var payment of doc.payments) {
		payment.amount = gUtils.round(payment.amount, 2);
		payment.tender = gUtils.round(payment.tender, 2);
		totalPaidAmount += payment.amount;
	}
	totalPaidAmount = gUtils.round(totalPaidAmount, 2);
	doc.balance = gUtils.round(doc.total - totalPaidAmount, 2);
}

function setTicketState(doc, shouldFinalize) {
	if(doc.numFoodFullfilled == doc.numFood)
		doc.state |= gDataModel.Ticket.STATE_FULFILLED;
	else
		doc.state &= ~gDataModel.Ticket.STATE_FULFILLED;

	doc.state &= ~gDataModel.Ticket.STATE_COMPLETED;
	doc.state &= ~gDataModel.Ticket.STATE_PAID;
	if(shouldFinalize) {
		if(doc.balance > 0)
			throw gError.UserError(`Ticket #${doc._id} - Payment is due`);

		if(doc.balance < 0) {
			doc.payments.push({name: 'cash', amount: doc.balance, tender: doc.balance});
			doc.balance = 0;
		}

		doc.state |= gDataModel.Ticket.STATE_PAID;
		if(doc.numFoodFullfilled == doc.numFood && doc.tableId < 0)
			doc.state |= gDataModel.Ticket.STATE_COMPLETED;
	}
}

var updateTicket = exports.updateTicket = (app, newDoc, shouldFinalize) => {
	var db = app.locals.db;
	var curDoc, _newDoc, dbRev;
	var changeGiven = 0.0;

	return db.collection("Ticket").findOne({_id: newDoc._id})
	.then((doc) => {
		if(doc == null)
			return gError.UserErrorPromise(`Can't find Ticket #${newDoc._id}`);
		else if(doc.state & gDataModel.Ticket.STATE_PAID)
			return gError.UserErrorPromise(`Ticket #${newDoc._id} has been finalised`);
		else if((doc.dbRev & 0xFFFF) != (newDoc.dbRev & 0xFFFF))
			return gError.UserErrorPromise(`Ticket #${newDoc._id} has changed`);

		curDoc = doc;

		var curFoodItemsMap = {};
		for(var foodItem of curDoc.foodItems)
			curFoodItemsMap[foodItem.itemId] = foodItem;
		for(var foodItem of newDoc.foodItems) {
			var curFoodItem = curFoodItemsMap[foodItem.itemId];
			if(curFoodItem == null) continue;
			foodItem.fulfilled = curFoodItem.fulfilled;
		}

		dbRev = (newDoc.dbRev & 0xFFFF) | (curDoc.dbRev & 0xFFFF0000);
		_newDoc = Object.assign({}, newDoc, {dbRev: dbRev + 1});
		sanitizeTicket(_newDoc);
		if(shouldFinalize) changeGiven = _newDoc.balance;
		delete _newDoc._id;
		delete _newDoc.dbCreatedTime;
		setTicketState(_newDoc, shouldFinalize);

		if(curDoc.tableId != newDoc.tableId && newDoc.tableId > 0)
			return gDineTable.openDineTable(app, newDoc.tableId, newDoc._id)
			.then((success) => {
				if(!success)
					return gError.UserErrorPromise(`Can't Open DineTable #${newDoc.tableId}`);
			});

	}).then(() => {
		return gSyncableDoc.updateSyncableDoc(
			app,
			"Ticket",
			{_id: newDoc._id, dbRev: dbRev},
			{$set: _newDoc}
		);

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
	}).then((success) => {
		return {success: success, _id: newDoc._id, changeGiven: changeGiven};
	});
};

var closeTicketTable = exports.closeTicketTable = (app, ticketId) => {
	var db = app.locals.db;
	var stateMask = gDataModel.Ticket.STATE_PAID | gDataModel.Ticket.STATE_FULFILLED;

	return gSyncableDoc.findAndUpdateSyncableDoc(app, "Ticket",
		{_id: ticketId, state: {$bitsAllSet: stateMask}, ticketId: {$gt: 0}},
		{$inc: {dbRev: 1}, $bit: {state: {or: gDataModel.Ticket.STATE_COMPLETED}}},
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

var fulfill = exports.fulfill = (app, ticketId, foodList) => {
	var db = app.locals.db;
	var stateMask = gDataModel.Ticket.STATE_COMPLETED | gDataModel.Ticket.STATE_FULFILLED;

	return db.collection("Ticket").findOne({_id: ticketId, state: {$bitsAllClear: stateMask}})
	.then((doc) => {
		if(doc == null) return;

		var totalFullfilled = 0;
		var count = 0;
		for(var foodItem of doc.foodItems) {
			var qty = foodList[foodItem.itemId];
			if(qty != null) {
				count++;
				foodItem.fulfilled += qty;
				foodItem.fulfilled = Math.max(0, Math.min(foodItem.fulfilled, foodItem.quantity));
			}
			totalFullfilled += foodItem.fulfilled;
		}
		if(!count) return;

		var dbRev = doc.dbRev;
		var updDoc = {
			state: doc.state,
			numFoodFullfilled: totalFullfilled,
			foodItems: doc.foodItems,
			dbRev: dbRev
		};
		updDoc.dbRev += 0x10000;

		if(totalFullfilled == doc.numFood) {
			updDoc.dbRev += 1;
			updDoc.state |= gDataModel.Ticket.STATE_FULFILLED;
			if(doc.tableId < 0 && (doc.state & gDataModel.Ticket.STATE_PAID))
				updDoc.state |= gDataModel.Ticket.STATE_COMPLETED;
		}
		
		return gSyncableDoc.updateSyncableDoc(
			app,
			"Ticket",
			{_id: doc._id, dbRev: dbRev},
			{$set: updDoc}
		);

	});
}

var deleteDoc = exports.deleteDoc = (app, query) => {
	return gSyncableDoc.findAndDeleteSyncableDoc(app, "Ticket", query)
	.then((doc) => {
		if(doc == null) return false;
		if(doc.tableId <= 0) return true;
		return gDineTable.closeDineTable(app, doc.tableId, doc._id);
	});
}