
var UserError = exports.UserError = function(message) {
	if(!(this instanceof UserError)) return new UserError(message);

	this.message = message;
    this.stack = null;
    Error.captureStackTrace(this, UserError);
}

UserError.prototype = Object.create(Error.prototype);
UserError.prototype.name = "UserError";
UserError.prototype.constructor = UserError;


exports.UserErrorPromise = function(message) {
	return Promise.reject(new UserError(message));
}