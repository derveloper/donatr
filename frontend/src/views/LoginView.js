import React from 'react'
import { connect } from 'react-redux'
import { actions as sessionActions } from '../redux/modules/session'
import { pushPath } from 'redux-simple-router'

const mapStateToProps = (state) => ({
  session: state.session
})
export class LoginView extends React.Component {
  static propTypes = {
    session: React.PropTypes.object.isRequired,
    location: React.PropTypes.object.isRequired,
    params: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired
  }

  constructor (props) {
    super(props)
    this.onSubmit = this.onSubmit.bind(this)
  }

  componentWillMount () {
    this.checkAuth()
  }

  componentWillReceiveProps (nextProps) {
    this.checkAuth(
      nextProps.session.isAuthenticated,
      nextProps.session.triedToAuthenticate
    )
  }

  checkAuth (isAuthenticated, triedToAuthenticate) {
    triedToAuthenticate = triedToAuthenticate || this.props.session.triedToAuthenticate
    isAuthenticated = isAuthenticated || this.props.session.isAuthenticated
    if (!triedToAuthenticate) this.props.dispatch(sessionActions.tryToAuthenticate())
    if (triedToAuthenticate && isAuthenticated) {
      let redirectAfterLogin = this.props.location.query.next || '/'
      this.props.dispatch(pushPath(redirectAfterLogin))
    }
  }

  onSubmit (e) {
    e.preventDefault()
    let username = e.target.elements['username'].value
    let password = e.target.elements['password'].value
    this.props.dispatch(sessionActions.create(username, password))
  }

  render () {
    const { triedToAuthenticate, isAuthenticated } = this.props.session
    return (triedToAuthenticate && !isAuthenticated)
      ? (
        <div className='container-fluid text-xs-center'>
          <h1>Welcome to the React Redux Starter Kit</h1>
          { this.props.session.loginFailed
            ? <div className='alert-danger'>Login failed!</div> : '' }
          <form onSubmit={this.onSubmit} action='/login' method='post'>
            <div><input type='text' name='username' id='user-name-label'/></div>
            <div><input type='password' name='password' id='password-name-label'/></div>
            <button type='submit'>Login</button>
          </form>
        </div>)
      : null
  }
}

export default connect(mapStateToProps)(LoginView)
