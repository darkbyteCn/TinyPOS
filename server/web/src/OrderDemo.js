import React  from 'react'
import { Link } from 'react-router'


var gFoodList = [];

export default class OrderDemo extends React.Component {
	constructor(props) {
		super(props);
		this.state = {searchResult: []};
		this.req = null;
		this.delay = null;
		this.ticketId = 0;

		this.onSearch = this.onSearch.bind(this);
		this.onSaveOrder = this.onSaveOrder.bind(this);
	}

	onSearch(event) {
		var terms = event.target.value;
		this.clearPending();
		this.delay = setTimeout(() => {
			this.delay = null;
			this.search(terms);
		}, 300);
	}

	clearPending() {
		if(this.delay != null) {
			clearTimeout(this.delay);
			this.delay = null;
		}
		if(this.req != null) {
			this.req.abort();
			this.req = null;
		}
	}

	onClickSearchResult(food, event) {
		food.quantity = 1;
		food.attr = [];
		delete food.attrGroup;
		food.itemId = gFoodList.length ? gFoodList[gFoodList.length - 1].itemId + 1 : 1;
		gFoodList.push(food);
		this.clearPending();
		this.refs.searchInput.value = "";
		this.setState({searchResult: []});
	}

	onClickDeleteItem(index, event) {
		gFoodList.splice(index, 1);
		this.setState();
	}

	onSaveOrder(event) {
		if(gFoodList.length == 0) return;
		
		//demo
		var order = {
			employeeId: -1,
			employeeName: "Web Server",
			tableId: -1,
			curItemId: gFoodList[gFoodList.length - 1].itemId + 1,
			foodItems: gFoodList,
			customer: {_id: -1, name: "Online User"}
		}

		$.ajax({
		    type: "POST",
		    url: "/Api/Ticket/newDoc",
		    data: JSON.stringify(order),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
		    success: (result) => {
		    	if(result == null || result.success != true) return;

		    	gFoodList = [];
		    	this.ticketId = result._id;
		    	$(this.refs.dialog).modal('show');
		    	this.setState();
		    },
		});

	}

	search(terms) {
		this.clearPending();
		if(!terms) {
			this.setState({searchResult: []});
			return;
		}

		this.req = $.get("/Api/Food/search", {terms: terms, limit:3}, (result) => {
			this.req = null;
			this.setState({searchResult: result && result.docs || []})
		}, "json");
	}

	render() {

		var orderListView = [];
		var index = -1;
		for(var food of gFoodList) {
			index++;
			orderListView.push(
				<li className="list-group-item orderListItem">
					<div className="row">
						<div className="col-xs-6 textSL">{food.foodName}</div>
						<div className="col-xs-3"><b>${food.price}</b></div>
						<div className="col-xs-3">
							<button type="button" className="btn btn-danger" onClick={this.onClickDeleteItem.bind(this, index)}>
								<span className="glyphicon glyphicon-remove" aria-hidden="true"></span>
							</button>
						</div>
					</div>
				</li>
			);
		}
		if(orderListView.length == 0)
			orderListView.push(<li className="list-group-item">Empty</li>);

		var searchResultView = [];
		for(var food of this.state.searchResult) {
			searchResultView.push(
				<div className="resultListItem" onClick={this.onClickSearchResult.bind(this, food)}>
					<div className="row">
						<div className="col-xs-9 textSL">{food.foodName}</div>
						<div className="col-xs-3"><b>${food.price}</b></div>
					</div>
				</div>
			);
		}

    	return (
<div className="container" id="container_order_demo">
	<div>
		<div className="input-group">
			<input type="text" ref="searchInput" className="form-control" placeholder="Food Name.." onChange={this.onSearch}/>
			<span className="input-group-btn">
				<button className="btn btn-default" type="button" onClick={this.onSaveOrder}>Save Order</button>
			</span>
		</div>
		<div>{searchResultView}</div>
		<br/>
		<div className="panel panel-default">
			<div className="panel-heading">Order List</div>
			<ul className="list-group">{orderListView}</ul>
		</div>
	</div>

	<div ref="dialog" className="modal fade" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel">
	  <div className="modal-dialog modal-sm" role="document">
	    <div className="modal-content">
			<div className="modal-header">
				<button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 className="modal-title">Modal title</h4>
			</div>
			<div className="modal-body">
				<div className="alert alert-success" role="alert">
					ticket #{this.ticketId} has been placed
				</div>
			</div>
	    </div>
	  </div>
	</div>

</div>
        );
    }

};
