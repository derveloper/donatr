import React from 'react'
import { Provider } from 'react-redux'
import { Router } from 'react-router'
import AppBar from 'material-ui/lib/app-bar'
import Navigation from 'components/Navigation'
import { actions as navActions } from '../redux/modules/navigation'

export default class Root extends React.Component {
  static propTypes = {
    history: React.PropTypes.object.isRequired,
    routes: React.PropTypes.element.isRequired,
    store: React.PropTypes.object.isRequired
  }

  get content () {
    return (
      <Router history={this.props.history}>
        {this.props.routes}
      </Router>
    )
  }

  constructor(props) {
    super(props)
  }

  get devTools () {
    if (__DEBUG__) {
      if (__DEBUG_NEW_WINDOW__) {
        require('../redux/utils/createDevToolsWindow')(this.props.store)
      } else {
        const DevTools = require('containers/DevTools')
        return <DevTools />
      }
    }
  }

  render () {
    return (
      <Provider store={this.props.store}>
        <div style={{ height: '100%' }}>
          <Navigation ref='leftNav'/>
          <AppBar
            onLeftIconButtonTouchTap={() => this.props.store.dispatch(navActions.toggle())}
            title='donatr'/>
          {this.content}
          {this.devTools}
        </div>
      </Provider>
    )
  }
}
