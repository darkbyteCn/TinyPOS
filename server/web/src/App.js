import React  from 'react'
import { formatPattern, Link } from 'react-router'
import jsCookie from 'js-cookie'

import Header from './Header'
import {Footer} from './Footer'
import ApplicationContext from './ApplicationContext'


var App = React.createClass({

	render() {
		return (
			<div>
				<div id="header">
					<Header />
				</div>
				<div id="app_body">{this.props.children}</div>
				<div id="footer">
					<Footer />
				</div>
			</div>
		)
	}

});
App.title = "Home"

export default App;
