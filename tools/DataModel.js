
<%
var defaultValueFunc = (colName, colMeta) => {
	if(colMeta.type == 'int' || colMeta.type == 'long')
		return `parseInt(js.${colName}) || 0`;
	else if(colMeta.type == 'double')
		return `parseFloat(js.${colName}) || 0.0`;
	else if(colMeta.type == 'String')
		return `js.${colName} == null ? null : String(js.${colName})`;
	else {
		var className = /^List<([a-z]+)>$/gi.exec(colMeta.type);
		if(className && this.schemas[className[1]])
			return `js.${colName} == null ? null : js.${colName}.map(filter${className[1]})`
	}


	return `js.${colName}`;
}
%>
%for(var tableName in this.schemas) {
var filter${tableName} = exports.filter${tableName} = (js) => {
	var newJs = {};
%	for(var colName in this.schemas[tableName]) {
%		var colMeta = this.schemas[tableName][colName];
%		if(colMeta.system) continue;
	newJs.${colName} = ${defaultValueFunc(colName, colMeta)};
%	}
	return newJs;
};

%}