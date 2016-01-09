import React from 'react';
import { connect } from 'react-redux';
import Dialog from 'material-ui/lib/dialog';
import FlatButton from 'material-ui/lib/flat-button';
import TextField from 'material-ui/lib/text-field';
import { actions as donatablesActions } from '../redux/modules/donatables';
import { actions as sessionActions } from '../redux/modules/session';
import { actions as accountActions } from '../redux/modules/accounts';

const mapStateToProps = (state) => ({
  session: state.session,
  isOpen: state.donatables.createDialogOpen || false
});
class CreateDonatableDialog extends React.Component {
  static propTypes = {
    session: React.PropTypes.object.isRequired,
    isOpen: React.PropTypes.bool.isRequired,
    dispatch: React.PropTypes.func.isRequired,
    donatable: React.PropTypes.object
  };

  createDonatable = () => {
    const { donatable, session, dispatch } = this.props;
    const name = this.refs.nameInput.getValue();
    const amount = this.refs.amountInput.getValue();
    const imageUrl = this.refs.imageUrlInput.getValue();
    const id = this.props.donatable.id;

    if (session.editMode) {
      if (donatable.name !== name) {
        dispatch(accountActions.updateName(id, name));
      }
      if (donatable.amount !== parseFloat(amount)) {
        dispatch(donatablesActions.updateAmount(id, amount));
      }
      if (donatable.imageUrl !== imageUrl) {
        dispatch(accountActions.updateImageUrl(id, imageUrl));
      }
    } else {
      dispatch(donatablesActions.create(name, amount, imageUrl));
    }

    dispatch(donatablesActions.closeCreateDialog());
    dispatch(sessionActions.currentAccount(false));
  };

  destroy = () => {
    this.props.dispatch(donatablesActions.destroy(this.props.donatable.id));
    this.props.dispatch(donatablesActions.toggleCreateDialog());
  };

  getActions = () => {
    const { dispatch, session } = this.props;
    return [
      <FlatButton label='Cancel'
                  onClick={() => dispatch(donatablesActions.toggleCreateDialog())}
                  secondary/>,
      <FlatButton label='Submit'
                  onClick={this.createDonatable}
                  primary/>,
      session.editMode ? <FlatButton label='Delete'
                                     onClick={this.destroy}
                                     secondary /> : null
    ];
  };

  render () {
    const { dispatch, isOpen, donatable } = this.props;
    return <Dialog autoScrollBodyContent
      modal={false}
      title='Create donatable'
      actions={this.getActions()}
      onRequestClose={() => dispatch(donatablesActions.closeCreateDialog())}
      open={isOpen}>
      <div><TextField defaultValue={donatable.name} floatingLabelText='Name' ref='nameInput'/></div>
      <div><TextField defaultValue={donatable.amount} floatingLabelText='Amount' ref='amountInput'/></div>
      <div><TextField defaultValue={donatable.imageUrl} floatingLabelText='Image URL' ref='imageUrlInput'/></div>
    </Dialog>;
  }
}

export default connect(mapStateToProps)(CreateDonatableDialog);
