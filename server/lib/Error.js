
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


exports.DefaultErrorHandler = (err, req, res, next) => {
	if(err instanceof UserError) {
		res.json({success: false, error: err.message})
	} else {
		next(err);
	}
}
