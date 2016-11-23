import React  from 'react'
import ReactDOM from 'react-dom'
import { Router, Route, browserHistory, IndexRoute, Redirect } from 'react-router'
import App from './App'
import Home from './Home'
import Orders from './Orders'
import OrderDemo from './OrderDemo'

$(function() {


ReactDOM.render((
<Router history={browserHistory}>
  <Route path="/web/" component={App}>
    <IndexRoute component={Home}/>
    <Route path="home" component={Home}/>
    <Route path="orders" component={Orders}/>
    <Route path="order-demo" component={OrderDemo}/>
  </Route>
  <Redirect from="/" to="/web/" />
  <Redirect from="/web" to="/web/" />
</Router>

), document.getElementById('app'));


});
