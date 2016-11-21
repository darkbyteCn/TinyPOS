var gCfg = require('./config');
var gExpress = require('express');
var gMongoClient = require('mongodb').MongoClient;
var gAssert = require('assert');
var gFs = require('fs');
var gCookieParser = require('cookie-parser');
var gConfig = require('./lib/Config');
var gLogger = require('./lib/Logger');
var gAuth = require('./lib/Auth');

var gApp = gExpress();

gApp.use(setup_end_handler);
gApp.use(gCookieParser());
startApp();

//----------------------------

function startListening() {
	gApp.listen(gCfg.listen_path, function () {
		gLogger.info('Server Started');
	});
}

function startApp() {
	gMongoClient.connect(gCfg.db_url, function(err, db) {
		gAssert.equal(null, err)

		gApp.disable("x-powered-by");
		gApp.locals.db = db;
		gApp.locals.numReqs = 0;
		gApp.locals.cfg = gCfg;
		gApp.locals.config = gConfig(gApp);
		gApp.locals.logger = gLogger;
		gApp.locals.auth = gAuth(gApp);

		initServices();
		initRoutes();

		if(typeof gCfg.listen_path == "string") {
			gFs.unlink(gCfg.listen_path, function() {
				startListening();
			});
		} else {
			startListening();
		}
	});
}

function end_hook(chunk, encoding, callback) {
	this.set('ms', new Date().getTime() - this.startTs);
	this.__proto__.end.call(this, chunk, encoding, callback);
}

function setup_end_handler(req, res, next) {
	res.startTs = new Date().getTime();
	if(!res.hasOwnProperty('end')) {
		res.end = end_hook;
	}

	gApp.locals.numReqs++;
	res.on('close', function() {
		gApp.locals.numReqs--;
	}).on('finish', function() {
		gApp.locals.numReqs--;
	});

	next();
}

function initRoutes() {
	var DIR_PATH = './route';
	var nzs = gFs.readdirSync(DIR_PATH);
	for(var i = 0; i < nzs.length; i++) {
		var nz = nzs[i];
		if(nz.substr(nz.length - 3).toLowerCase() != '.js') continue;

		var module = require(DIR_PATH  + "/" + nz);
		if(!module.path) continue;
		if(!module.authNotRequired)
			gApp.use(module.path, gApp.locals.auth.getBoundRouteCallback(), module.route);
		else
			gApp.use(module.path, module.route);
	}
}

function initServices() {
	var DIR_PATH = './service';
	var nzs = gFs.readdirSync(DIR_PATH);
	for(var i = 0; i < nzs.length; i++) {
		var nz = nzs[i];
		if(nz.substr(nz.length - 3).toLowerCase() != '.js') continue;

		var module = require(DIR_PATH  + "/" + nz);
		if(module.type == 'background' && module.start)
			module.start(gApp);
	}
}
