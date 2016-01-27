import React from 'react';
import { connect } from 'react-redux';
import MenuItem from 'material-ui/lib/menus/menu-item';
import LeftNav from 'material-ui/lib/left-nav';
import CreateAccountDialog from 'components/CreateAccountDialog';
import CreateDonatableDialog from 'components/CreateDonatableDialog';
import TransferMoneyDialog from 'components/TransferMoneyDialog';
import _ from 'underscore';
import { pushPath } from 'redux-simple-router';
import { actions as navActions } from '../redux/modules/navigation';
import { actions as accountActions } from '../redux/modules/accounts';
import { actions as donatableActions } from '../redux/modules/donatables';
import { actions as sessionActions } from '../redux/modules/session';
import { actions as transactionActions } from '../redux/modules/transactions';

const mapStateToProps = (state) => ({
  isOpen: state.navigation.isOpen || false,
  session: state.session,
  currentAccount: _.findWhere(state.accounts.accounts, {id: state.session.currentAccount.id})
});
class Navigation extends React.Component {
  static propTypes = {
    isOpen: React.PropTypes.bool.isRequired,
    session: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired,
    currentAccount: React.PropTypes.object
  };

  toggleCreateAccountDialog = () => {
    this.props.dispatch(navActions.toggle());
    this.props.dispatch(accountActions.toggleCreateDialog());
  };

  toggleCreateDonatableDialog = () => {
    this.props.dispatch(navActions.toggle());
    this.props.dispatch(donatableActions.toggleCreateDialog());
  };

  toggleTransferMoneyDialog = () => {
    this.props.dispatch(navActions.toggle());
    this.props.dispatch(transactionActions.toggleCreateDialog());
  };

  goToAccounts = () => {
    this.props.dispatch(navActions.toggle());
    this.props.dispatch(pushPath('/'));
  };

  goToCredit = () => {
    this.props.dispatch(navActions.toggle());
    this.props.dispatch(pushPath('/credit'));
  };

  toggleEditMode = () => {
    this.props.dispatch(sessionActions.toggleEditMode());
    this.props.dispatch(navActions.toggle());
  };

  render () {
    if (!this.props.session.isAuthenticated) return null;
    const { dispatch, session, currentAccount } = this.props;
    return <span>
      <CreateAccountDialog account={session.editMode ? (session.currentAccount || {}) : {}} />
      <CreateDonatableDialog donatable={session.editMode ? (session.currentAccount || {}) : {}} />
      <TransferMoneyDialog />
      <LeftNav
        docked={false}
        open={this.props.isOpen}
        onRequestChange={open => dispatch(navActions.open(open))}
        ref='leftNav'>
        <MenuItem onClick={this.goToAccounts} primaryText='Accounts'/>
        <MenuItem onClick={this.toggleCreateAccountDialog} primaryText='Add account'/>
        <MenuItem onClick={this.toggleTransferMoneyDialog} primaryText='Transfer money'/>
        <MenuItem onClick={this.toggleCreateDonatableDialog} primaryText='Add donatable'/>
        { currentAccount ? <MenuItem onClick={this.goToCredit} primaryText='Add credit'/> : null }
        <MenuItem onClick={this.toggleEditMode} primaryText={`Toggle edit mode ${session.editMode ? 'off' : 'on'}`}/>
        <MenuItem onClick={() => dispatch(sessionActions.destroy())} primaryText='Logout'/>
      </LeftNav>
    </span>;
  }
}

export default connect(mapStateToProps)(Navigation);
