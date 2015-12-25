import React from 'react'
import { connect } from 'react-redux'
import AppBar from 'material-ui/lib/app-bar'
import Avatar from 'material-ui/lib/avatar'
import { actions as navActions } from '../redux/modules/navigation'
import _ from 'underscore'

const trollface = require('assets/trollface.svg')

const mapStateToProps = (state) => ({
  accounts: state.accounts,
  currentAccount: _.findWhere(state.accounts.accounts, {id: state.session.currentAccount.id})
})
class AppTopBar extends React.Component {
  static propTypes = {
    accounts: React.PropTypes.object.isRequired,
    currentAccount: React.PropTypes.object,
    dispatch: React.PropTypes.func.isRequired
  }

  render () {
    const { dispatch, currentAccount } = this.props
    const topAccountInfo = currentAccount
      ? <span>
          <Avatar src={trollface}/>
          <span
            style={{ paddingLeft: 5, position: 'relative', top: -15 }}>{currentAccount.name} {currentAccount.balance}
            €</span>
        </span>
      : null
    return <AppBar
      style={{ position: 'fixed' }}
      onLeftIconButtonTouchTap={() => dispatch(navActions.toggle())}
      iconElementRight={topAccountInfo}
      title='donatr'/>
  }
}

export default connect(mapStateToProps)(AppTopBar)
