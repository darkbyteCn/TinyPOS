var gNet = require("net");
var gDgram = require('dgram');
var gDoc = require('../lib/Doc');
var gBuffer = require('buffer').Buffer;
var gDocEvent = require('../lib/DocEvent');
var gLogger = require('../lib/Logger');

function createMessageServer() {
	var conns = new Map();
	var server = gDgram.createSocket('udp4');

	server.on('message', (msg, rinfo) => {
		//console.log(`> UDP > ${rinfo.address}:${rinfo.port}`, rinfo);
		var connStr = `${rinfo.address}:${rinfo.port}`;
		if(!msg || msg.length != 4) return;

		try	{
			var msgId = msg.toString();
			if(msgId == 'REQU') {
				requestUpdate(server, rinfo.address, rinfo.port);
			} else if(msgId == 'WTCH') {

			} else
				return;

		} catch(e) {
			gLogger.error(e);
			return;
		}

		conns.set(connStr, {
			lastTs: (new Date()).getTime(),
			address: rinfo.address,
			port: rinfo.port
		});
	});

	server.on('error', (err) => {
		gLogger.error("MessageServer ->", err);
	});

	server.bind(8889);

	return {server: server, conns: conns};
}

function requestUpdate(server, address, port) {
	if(gApp.locals.docEventLastId == null) return;

	var buf = gBuffer.alloc(6);
	buf.writeUIntLE(gApp.locals.docEventLastId, 0, 6);
	server.send(buf, port, address);
}

function sendUpdateToAll() {
	var buf = gBuffer.alloc(6);
	buf.writeUIntLE(gApp.locals.docEventLastId, 0, 6);
	var cts = (new Date()).getTime();
	
	gMessageServer.conns.forEach((conn, key, map) => {
		if(cts - conn.lastTs > 15000)
			map.delete(key);
		else
			gMessageServer.server.send(buf, conn.port, conn.address);
	});
}

function updatePeriodly() {
	gDocEvent.getDocEventLastId(gApp)
	.then(() => {
		sendUpdateToAll();
		return 5000;
	}, (err) => {
		gLogger.error(err);
		return 1000;
	}).catch((err) => {
		gLogger.error(err);
		return 3000;
	}).then((ms) => {
		setTimeout(updatePeriodly, ms);
	});
}

var gApp;
var gMessageServer;
function startService(app) {
	gApp = app;
	gMessageServer = createMessageServer();
	gDocEvent.registerDocEventObserver(sendUpdateToAll);

	updatePeriodly();
}

function stopService(app) {


}

module.exports = {
	type: 'background',
	start: startService,
	stop: stopService,
};