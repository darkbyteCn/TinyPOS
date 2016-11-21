var gExpress = require('express');
var gBodyParser = require('body-parser');
var gDoc = require('../lib/Doc');

var gJsonParser = gBodyParser.json();

var gRouter = gExpress.Router();
module.exports = {path: "/Auth", route: gRouter, authNotRequired: true};


gRouter.post('/getEmployeeInfo', getEmployeeInfo);
gRouter.post('/getAuth', getAuth);
gRouter.get('/checkAuth', checkAuth);


function getEmployeeInfo(req, res, next) {
	res.json({success: true, name: "nobody"});
}

function getAuth(req, res, next) {
	res.cookie("serverAuth", "1112233444444");
	console.log("getAuth");
	res.json({success: true});
}

function checkAuth(req, res, next) {
	res.json({success: true});
}
