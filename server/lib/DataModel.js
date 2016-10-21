
var filterTicket = exports.filterTicket = (js) => {
	var newJs = {};
	newJs._id = parseInt(js._id) || 0;
	newJs.state = parseInt(js.state) || 0;
	newJs.tableId = parseInt(js.tableId) || 0;
	newJs.tableName = js.tableName == null ? null : String(js.tableName);
	newJs.employeeId = parseInt(js.employeeId) || 0;
	newJs.employeeName = js.employeeName == null ? null : String(js.employeeName);
	newJs.customerId = parseInt(js.customerId) || 0;
	newJs.customerName = js.customerName == null ? null : String(js.customerName);
	newJs.numFoodFullfilled = parseInt(js.numFoodFullfilled) || 0;
	newJs.numFood = parseInt(js.numFood) || 0;
	newJs.numGuest = parseInt(js.numGuest) || 0;
	newJs.curItemId = parseInt(js.curItemId) || 0;
	newJs.foodItems = js.foodItems == null ? null : js.foodItems.map(filterTicketFood);
	newJs.subtotal = parseFloat(js.subtotal) || 0.0;
	newJs.tips = parseFloat(js.tips) || 0.0;
	newJs.fee = parseFloat(js.fee) || 0.0;
	newJs.tax = parseFloat(js.tax) || 0.0;
	newJs.total = parseFloat(js.total) || 0.0;
	newJs.createdTime = parseInt(js.createdTime) || 0;
	newJs.dbRev = parseInt(js.dbRev) || 0;
	return newJs;
};

var filterTicketFoodAttr = exports.filterTicketFoodAttr = (js) => {
	var newJs = {};
	newJs.name = js.name == null ? null : String(js.name);
	newJs.value = js.value == null ? null : String(js.value);
	return newJs;
};

var filterTicketFood = exports.filterTicketFood = (js) => {
	var newJs = {};
	newJs._id = parseInt(js._id) || 0;
	newJs.itemId = parseInt(js.itemId) || 0;
	newJs.foodName = js.foodName == null ? null : String(js.foodName);
	newJs.quantity = parseInt(js.quantity) || 0;
	newJs.price = parseFloat(js.price) || 0.0;
	newJs.exPrice = parseFloat(js.exPrice) || 0.0;
	newJs.attr = js.attr == null ? null : js.attr.map(filterTicketFoodAttr);
	newJs.taxable = parseInt(js.taxable) || 0;
	return newJs;
};

var filterFoodAttr = exports.filterFoodAttr = (js) => {
	var newJs = {};
	newJs.name = js.name == null ? null : String(js.name);
	newJs.priceDiff = parseFloat(js.priceDiff) || 0.0;
	return newJs;
};

var filterFoodAttrGroup = exports.filterFoodAttrGroup = (js) => {
	var newJs = {};
	newJs.name = js.name == null ? null : String(js.name);
	newJs.attr = js.attr == null ? null : js.attr.map(filterFoodAttr);
	return newJs;
};

var filterFood = exports.filterFood = (js) => {
	var newJs = {};
	newJs._id = parseInt(js._id) || 0;
	newJs.foodName = js.foodName == null ? null : String(js.foodName);
	newJs.taxable = parseInt(js.taxable) || 0;
	newJs.price = parseFloat(js.price) || 0.0;
	newJs.dbRev = parseInt(js.dbRev) || 0;
	newJs.attrGroup = js.attrGroup == null ? null : js.attrGroup.map(filterFoodAttrGroup);
	return newJs;
};

var filterMenu = exports.filterMenu = (js) => {
	var newJs = {};
	newJs._id = parseInt(js._id) || 0;
	newJs.categoryId = parseInt(js.categoryId) || 0;
	newJs.foodId = parseInt(js.foodId) || 0;
	newJs.menuName = js.menuName == null ? null : String(js.menuName);
	newJs.dbRev = parseInt(js.dbRev) || 0;
	return newJs;
};

var filterDineTable = exports.filterDineTable = (js) => {
	var newJs = {};
	newJs._id = parseInt(js._id) || 0;
	newJs.name = js.name == null ? null : String(js.name);
	newJs.ticketId = parseInt(js.ticketId) || 0;
	newJs.maxGuest = parseInt(js.maxGuest) || 0;
	newJs.dbRev = parseInt(js.dbRev) || 0;
	return newJs;
};

var filterConfig = exports.filterConfig = (js) => {
	var newJs = {};
	newJs._id = parseInt(js._id) || 0;
	newJs.key = js.key == null ? null : String(js.key);
	newJs.val = js.val == null ? null : String(js.val);
	return newJs;
};

