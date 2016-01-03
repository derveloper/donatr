import React from 'react'
import { connect } from 'react-redux'
import AppBar from 'material-ui/lib/app-bar'
import Avatar from 'material-ui/lib/avatar'
import { actions as navActions } from '../redux/modules/navigation'
import _ from 'underscore'
import md5 from 'md5'

const mapStateToProps = (state) => ({
  accounts: state.accounts,
  navigation: state.navigation,
  session: state.session,
  currentAccount: _.findWhere(state.accounts.accounts, {id: state.session.currentAccount.id})
})
class AppTopBar extends React.Component {
  static propTypes = {
    accounts: React.PropTypes.object.isRequired,
    session: React.PropTypes.object,
    navigation: React.PropTypes.object,
    currentAccount: React.PropTypes.object,
    dispatch: React.PropTypes.func.isRequired
  }

  render () {
    if (!this.props.session.isAuthenticated) return null
    const { dispatch, currentAccount } = this.props
    const topAccountInfo = currentAccount && this.props.navigation.currentAccountVisible
      ? <span>
          <Avatar src={`http://www.gravatar.com/avatar/${md5(currentAccount.email || currentAccount.id)}?s=200&d=identicon&r=PG`} />
          <span style={{ paddingLeft: 5, position: 'relative', top: -15 }}>
            { currentAccount.name} {currentAccount.balance} €
          </span>
        </span>
      : null
    return <AppBar
      style={{ position: 'fixed' }}
      onLeftIconButtonTouchTap={() => dispatch(navActions.toggle())}
      iconElementRight={topAccountInfo}
      title='donatr' />
  }
}

export default connect(mapStateToProps)(AppTopBar)
