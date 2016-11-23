import React  from 'react'
import { Link } from 'react-router'
import {ListView, AjaxAdapter} from './TinyListView'

var adapter = new AjaxAdapter({
	url: '/Api/Ticket/getTicketsByPage'
});

adapter.getView = function(position) {
	var item = this.getItem(position);

	if(item == null) {
		return (<div></div>);
	} else {
		var date = new Date();
		date.setTime(item.createdTime);
		return (
			<div>
				<div className="row">
					<div className="col-xs-2"><b>#{item._id}</b></div>
					<div className="col-xs-2">{item.numFoodFullfilled}/{item.numFood}</div>
					<div className="col-xs-2">${item.total}</div>
					<div className="col-xs-6">{item.customer && item.customer.name || ""}</div>
				</div>
				<div className="row">
					<div className="col-xs-12">{date.toLocaleString()} by {item.employeeName}</div>
				</div>
			</div>
		);
	}

};

export default class Orders extends React.Component {
	constructor(props) {
		super(props);

	}

	componentDidMount() {

	}

	componentWillUnmount() {
		adapter.refresh();
	}

	render() {
    	return (
<div className="container" id="container_orders">
	<ListView
		ref="listView"
		adapter={adapter}
		outterScroll={true}
	/>
</div>
        );
    }

};
