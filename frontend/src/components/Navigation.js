import React from 'react'
import { connect } from 'react-redux'
import MenuItem from 'material-ui/lib/menus/menu-item'
import LeftNav from 'material-ui/lib/left-nav'
import CreateAccountDialog from 'components/CreateAccountDialog'
import { pushPath } from 'redux-simple-router'
import { actions as navActions } from '../redux/modules/navigation'
import { actions as accountActions } from '../redux/modules/accounts'

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

  goToAccounts = () => {
    this.props.dispatch(navActions.toggle())
    this.props.dispatch(pushPath('/'))
  }

  render() {
    const { dispatch } = this.props
    return <span>
      <CreateAccountDialog />
      <LeftNav
        docked={false}
        open={this.props.isOpen}
        onRequestChange={open => dispatch(navActions.open(open))}
        ref='leftNav'>
        <MenuItem onClick={this.goToAccounts} primaryText='Accounts'/>
        <MenuItem onClick={this.toggleCreateAccountDialog} primaryText='Add account'/>
        <MenuItem primaryText='Add donatable'/>
      </LeftNav>
    </span>
  }
}

export default connect(mapStateToProps)(Navigation)
