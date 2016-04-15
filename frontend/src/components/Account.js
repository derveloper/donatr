import React from 'react';
import BaseAccount from 'components/BaseAccount';
import { actions as sessionActions } from '../redux/modules/session';
import { connect } from 'react-redux';
import { pushPath } from 'redux-simple-router';
import { actions as accountActions } from '../redux/modules/accounts';

const mapStateToProps = (state) => ({
  session: state.session
});
class Account extends React.Component {
  static propTypes = {
    account: React.PropTypes.object.isRequired,
    session: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired
  };

  onAccountClick = () => {
    this.props.dispatch(sessionActions.currentAccount(this.props.account));
    if (this.props.session.editMode) {
      this.props.dispatch(accountActions.toggleCreateDialog());
    } else {
      this.props.dispatch(pushPath('/donate'));
    }
  };

  render () {
    const { account } = this.props;
    const title = <p>{account.name}</p>;
    return <span>
      <BaseAccount
        key={account.id}
        title={title}
        account={account}
        handleClick={this.onAccountClick} />
    </span>;
  }
}

export default connect(mapStateToProps)(Account);
