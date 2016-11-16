

var schemas = {

Ticket: {
	_id: {index: "primary", type: 'long'},
	state: {
		type: 'int',
		index: "index",
		value: {
			COMPLETED: '1 << 30',
			PAID: '1 << 3',
			FULFILLED: '1 << 2',
		}
	},

	tableId: {index: "index", type: 'long'},
	tableName: {type: 'String'},

	employeeId: {type: 'long'},
	employeeName: {type: 'String'},

	customer: {type: 'Customer', isJson: true},

	numFoodFullfilled: {type: 'int'},
	numFood: {type: 'int'},

	numGuest: {type: 'int'},

	curItemId: {type: 'int'},
	foodItems: {type: 'List<TicketFood>', isJson: true},

	subtotal: {type: 'double'},
	tips: {type: 'double'},
	fee: {type: 'double'},
	tax: {type: 'double'},
	total: {type: 'double'},
	balance: {type: 'double'},

	payments: {type: 'List<TicketPayment>', isJson: true},

	createdTime: {type: 'long'},

	notes: {type: 'String'},

	dbRev: {type: 'int'},
	dbCreatedTime: {type: 'long', system: true},
	dbModifiedTime: {type: 'long', system: true},
},

TicketPayment: {
	_id: {index: "primary", type: 'long'},
	type: {type: 'int'},
	amount: {type: 'double'},
	tender: {type: 'double'},
	createdTime: {type: 'long'}
},

TicketFoodAttr: {
	name: {type: 'String'},
	value: {type: 'String'}
},

TicketFood: {
	_id: {type: 'long'},
	ticketId: {index: "index", type: 'long'},
	itemId: {type: 'int'},
	foodName: {type: 'String'},
	quantity: {type: 'int'},
	fulfilled: {type: 'int'},
	price: {type: 'double'},
	exPrice: {type: 'double'},
	taxRate: {type: 'double'},
	attr: {type: 'List<TicketFoodAttr>', isJson: true},
	createdTime: {index: "index", type: 'long'}
},

FoodAttr: {
	name: {type: 'String'},
	priceDiff: {type: 'double'}
},

FoodAttrGroup: {
	name: {type: 'String'},
	attr: {type: 'List<FoodAttr>', isJson: true}
},

Food: {
	_id: {index: "primary", type: 'long'},
	foodName: {type: 'String'},
	taxable: {type: 'int'},
	price: {type: 'double'},
	dbRev: {type: 'int'},
	attrGroup: {type: 'List<FoodAttrGroup>', isJson: true}
},

Menu: {
	_id: {index: "primary", type: 'long'},
	categoryId: {index: "index", type: 'long'},
	foodId: {index: "index", type: 'long'},
	menuName: {type: 'String'},
	dbRev: {type: 'int'}
},

DineTable: {
	_id: {index: "primary", type: 'long'},
	name: {type: 'String'},

	ticketId: {type: 'long'},
	maxGuest: {type: 'int'},

	dbRev: {type: 'int'}
},

Config: {
	_id: {index: "primary", type: 'long', range: 1},
	key: {index: "index", type: 'String'},
	val: {type: 'String'}
},

Customer: {
	_id: {index: "primary", type: 'long'},
	name: {type: 'String'},
	address: {type: 'String'},
	address2: {type: 'String'},
	city: {type: 'String'},
	state: {type: 'String'},
	zipCode: {type: 'String'},
	phone: {type: 'String'},
	dbRev: {type: 'int'}
}

};

var tableJoins = [
	["DineTable", "Ticket"],
	["Menu", "Food"]
];

var noSyncTables = {
	TicketFood: true,
	FoodAttrGroup: true,
	FoodAttr: true,
	TicketFoodAttr: true,
	TicketPayment: true,
	Customer: true
};


function capitalize(s) {
	return s
	.replace(/(?:^|[^a-z0-9]+)([a-z0-9])/gi, (a, p1) => { return p1.toUpperCase() });
}

function _capitalize(s) {
	s = s.replace(/[^0-9a-z]/gi, '');
	return s.substr(0, 1).toUpperCase() + s.substr(1);
}

var gFs = require('fs');

var dstDir = "../app/src/main/java/com/tinyappsdev/tinypos/data";
var gTmpl = require("./tiny_template")(null, null, {
	capitalize: capitalize,
	console: console
});

for(var name in schemas) {
	var res = gTmpl.render("Schema.java", {cols: schemas[name], name: name});
	gFs.writeFileSync(dstDir + "/" + capitalize(name) + ".java", res);
}

var res = gTmpl.render("DatabaseOpenHelper.java", {schemas: schemas, version: 21});
gFs.writeFileSync(dstDir + "/DatabaseOpenHelper.java", res);

//var res = gTmpl.render("provider_base.java", {});
//gFs.writeFileSync(dstDir + "/ProviderBase.java", res);
/*
for(var name in schemas) {
	var res = gTmpl.render("Provider.java", {tableName: name});
	gFs.writeFileSync(dstDir + "/" + capitalize(name) + "Provider.java", res);
}
*/

var res = gTmpl.render("ContentProviderEx.java", {schemas: schemas, tableJoins: tableJoins});
gFs.writeFileSync(dstDir + "/ContentProviderEx.java", res);

var res = gTmpl.render("ModelHelper.java", {schemas: schemas, noSyncTables: noSyncTables});
gFs.writeFileSync(dstDir + "/ModelHelper.java", res);


//var res = gTmpl.render("SyncHelper.java", {schemas: schemas, noSyncTables: noSyncTables});
//gFs.writeFileSync(dstDir + "/../service/SyncHelper.java", res);


var res = gTmpl.render("DataModel.js", {schemas: schemas});
gFs.writeFileSync("../server/lib/DataModel.js", res);

