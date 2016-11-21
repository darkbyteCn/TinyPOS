var gExpress = require('express');
var gCookieParser = require('cookie-parser');
var gBodyParser = require('body-parser');
var gDoc = require('../lib/Doc');
var gDocEvent = require('../lib/DocEvent');
var gDataModel = require('../lib/DataModel');
var gDineTable = require('../lib/DineTable');
var gTicket = require('../lib/Ticket');
var gError = require('../lib/Error');

var gJsonParser = gBodyParser.json();

var gRouter = gExpress.Router();
module.exports = {path: "/Report", route: gRouter};

gRouter.get('/getTicketOverAll', getTicketOverAll);


function getTicketOverAll(req, res, next) {
	var date = String(req.query.date || "");

	var year = parseInt(date.substr(0, date.length - 4));
	var month = parseInt(date.substr(date.length - 4, 2));
	var day = parseInt(date.substr(date.length - 2, 2));

	var frmTs = new Date(year, month , day).getTime();
	var toTs = new Date(year, month + 1, day).getTime();

	var db = req.app.locals.db
	var cursor = db.collection("Ticket")
	.find({createdTime: {$gte: frmTs, $lt: toTs}})
	.project({foodItems: 1})
	.toArray()
	.then((docs) => {
		var ret = {success: true, ticketCount: docs.length, foodItemCount: 0};

		for(var doc of docs)
			ret.foodItemCount += doc.foodItems.length;

		res.json(ret);

	}).catch(function(err) {
		next(err);

	});
}

