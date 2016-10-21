var gCfg = require('../config');
var gWinston = require('winston');

var logger = module.exports = new gWinston.Logger({
	transports: [
		new gWinston.transports.Console(),
		//new (winston.transports.File)({ filename: 'somefile.log' })
	]
});
