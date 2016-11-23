import React  from 'react'
import { Link } from 'react-router'


export default class Home extends React.Component {
	constructor(props) {
		super(props);
		this.state = {counts: {}};
		this.req = null;
	}

	componentDidMount() {
		this.req = $.get("/Api/Sys/getSysInfo", (result) => {
			this.req = null;
			if(result != null && result.success == true) {
				this.setState({counts: result.counts});
			}
		}, "json");
	}

	componentWillUnmount() {
		if(this.req != null) this.req.abort();
	}

	render() {
    	return (
<div className="container">
	<div className="panel panel-primary">

	  <div className="panel-heading">Server Info</div>
	  <div className="panel-body">
	  	<h3>TinyPOS V1.0</h3>
	  	<h4>http://tinypos.tinyappsdev.com:8998</h4>
	  </div>
	  <ul className="list-group">
	    <li className="list-group-item">Orders: {this.state.counts.Ticket || 0}</li>
	    <li className="list-group-item">Customers: {this.state.counts.Customer || 0}</li>
	    <li className="list-group-item">Food Items: {this.state.counts.Food || 0}</li>
	  </ul>
	</div>
</div>
        );
    }



};
