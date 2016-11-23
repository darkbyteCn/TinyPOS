import React  from 'react'
import { Link, browserHistory } from 'react-router'

export default React.createClass({


  render() {
    return (
<div className="container">
  <ul className="nav nav-tabs">
    <li><Link activeClassName="active" to="/web/home">Home</Link></li>
    <li><Link activeClassName="active" to="/web/orders">Orders</Link></li>
    <li><Link activeClassName="active" to="/web/order-demo">Order Demo</Link></li>
  </ul>
</div>
)
  }

});