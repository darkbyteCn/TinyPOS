
function Auth(app) {
	if(!(this instanceof Auth)) return new Auth(app);

	this.app = app;
	this.boundRouteCallback = this.routeCallback.bind(this);
}
module.exports = Auth;

Auth.prototype.getBoundRouteCallback = function() {
	return this.boundRouteCallback;
}

Auth.prototype.routeCallback = function(req, res, next) {

	next();
}


