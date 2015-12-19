import React from 'react'
import { connect } from 'react-redux'
import { actions as sessionActions } from '../redux/modules/session'
import { pushPath } from 'redux-simple-router'

// We define mapStateToProps where we'd normally use
// the @connect decorator so the data requirements are clear upfront, but then
// export the decorated component after the main class definition so
// the component can be tested w/ and w/o being connected.
// See: http://rackt.github.io/redux/docs/recipes/WritingTests.html
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
    this.checkAuth(nextProps.session.isAuthenticated)
  }

  checkAuth (isAuthenticated) {
    if (isAuthenticated || this.props.session.isAuthenticated) {
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
    return (
      <div className='container-fluid text-xs-center'>
        <h1>Welcome to the React Redux Starter Kit</h1>
        { this.props.session.loginFailed
          ? <div className='alert-danger'>Login failed!</div> : '' }
        <form onSubmit={this.onSubmit} action='/login' method='post'>
          <div><input type='text' name='username' id='user-name-label'/></div>
          <div><input type='password' name='password' id='password-name-label'/></div>
          <button type='submit'>Login</button>
        </form>
      </div>
    )
  }
}

export default connect(mapStateToProps)(LoginView)
