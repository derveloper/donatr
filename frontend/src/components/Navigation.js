import React from 'react'
import { connect } from 'react-redux'
import MenuItem from 'material-ui/lib/menus/menu-item'
import LeftNav from 'material-ui/lib/left-nav'
import CreateAccountDialog from 'components/CreateAccountDialog'
import CreateDonatableDialog from 'components/CreateDonatableDialog'
import { pushPath } from 'redux-simple-router'
import { actions as navActions } from '../redux/modules/navigation'
import { actions as accountActions } from '../redux/modules/accounts'
import { actions as donatableActions } from '../redux/modules/donatables'
import { actions as sessionActions } from '../redux/modules/session'

const mapStateToProps = (state) => ({
  isOpen: state.navigation.isOpen || false
})
class Navigation extends React.Component {
  static propTypes = {
    isOpen: React.PropTypes.bool.isRequired,
    dispatch: React.PropTypes.func.isRequired
  }

  toggleCreateAccountDialog = () => {
    this.props.dispatch(navActions.toggle())
    this.props.dispatch(accountActions.toggleCreateDialog())
  }

  toggleCreateDonatableDialog = () => {
    this.props.dispatch(navActions.toggle())
    this.props.dispatch(donatableActions.toggleCreateDialog())
  }

  goToAccounts = () => {
    this.props.dispatch(navActions.toggle())
    this.props.dispatch(pushPath('/'))
  }

  render () {
    const { dispatch } = this.props
    return <span>
      <CreateAccountDialog />
      <CreateDonatableDialog />
      <LeftNav
        docked={false}
        open={this.props.isOpen}
        onRequestChange={open => dispatch(navActions.open(open))}
        ref='leftNav'>
        <MenuItem onClick={this.goToAccounts} primaryText='Accounts'/>
        <MenuItem onClick={this.toggleCreateAccountDialog} primaryText='Add account'/>
        <MenuItem onClick={this.toggleCreateDonatableDialog} primaryText='Add donatable'/>
        <MenuItem onClick={() => dispatch(sessionActions.destroy())} primaryText='Logout'/>
      </LeftNav>
    </span>
  }
}

export default connect(mapStateToProps)(Navigation)
