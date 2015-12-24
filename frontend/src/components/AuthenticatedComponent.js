import React from 'react'
import {connect} from 'react-redux'
import { pushPath } from 'redux-simple-router'
import { actions as sessionActions } from '../redux/modules/session'

export function requireAuthentication (Component) {
  const mapStateToProps = (state) => ({
    isAuthenticated: state.session.isAuthenticated
  })

  class AuthenticatedComponent extends React.Component {
    static propTypes = {
      isAuthenticated: React.PropTypes.bool.isRequired,
      location: React.PropTypes.object.isRequired,
      dispatch: React.PropTypes.func.isRequired
    }

    componentWillMount () {
      this.checkAuth()
    }

    componentWillReceiveProps (nextProps) {
      this.checkAuth()
    }

    checkAuth () {
      if (!this.props.isAuthenticated) {
        this.props.dispatch(sessionActions.tryToAuthenticate())
        let redirectAfterLogin = this.props.location.pathname
        this.props.dispatch(pushPath(`/login?next=${redirectAfterLogin}`))
      }
    }

    render () {
      return (
        <div>
          {this.props.isAuthenticated === true
            ? <Component {...this.props}/>
            : null
          }
        </div>
      )
    }
  }

  return connect(mapStateToProps)(AuthenticatedComponent)
}
