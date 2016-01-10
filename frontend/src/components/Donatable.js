import React from 'react';
import BaseAccount from 'components/BaseAccount';
import { actions as donatablesActions } from '../redux/modules/donatables';
import { actions as sessionActions } from '../redux/modules/session';
import { connect } from 'react-redux';

const mapStateToProps = (state) => ({
  session: state.session
});
class Donatable extends React.Component {
  static propTypes = {
    donatable: React.PropTypes.object.isRequired,
    session: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired,
    currentAccount: React.PropTypes.object
  };

  onDonatableClick = () => {
    if (this.props.session.editMode) {
      this.props.dispatch(sessionActions.currentAccount(this.props.donatable));
      this.props.dispatch(donatablesActions.toggleCreateDialog());
    } else {
      this.props.dispatch(donatablesActions.donate(this.props.currentAccount, this.props.donatable));
    }
  };

  render () {
    const { donatable } = this.props;
    const amount = donatable.amount < 0 ? '' : `${donatable.amount} €`;
    const title = <p>{donatable.name}<br/>{amount}</p>;
    return <BaseAccount
      key={donatable.id}
      account={donatable}
      title={title}
      onClick={this.onDonatableClick} />;
  }
}

export default connect(mapStateToProps)(Donatable);
