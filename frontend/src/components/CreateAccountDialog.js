import React from 'react'
import { connect } from 'react-redux'
import Dialog from 'material-ui/lib/dialog'
import FlatButton from 'material-ui/lib/flat-button'
import TextField from 'material-ui/lib/text-field'
import { actions as accountActions } from '../redux/modules/accounts'

const mapStateToProps = (state) => ({
  session: state.session,
  isOpen: state.accounts.createDialogOpen || false
})
class CreateAccountDialog extends React.Component {
  static propTypes = {
    session: React.PropTypes.object.isRequired,
    isOpen: React.PropTypes.bool.isRequired,
    dispatch: React.PropTypes.func.isRequired,
    account: React.PropTypes.object
  }

  onSubmit = () => {
    const name = this.refs.nameInput.getValue()
    const email = this.refs.emailInput.getValue()
    const imageUrl = this.refs.imageUrlInput.getValue()

    if (this.props.session.editMode) {
      if (this.props.account.name !== name) {
        this.props.dispatch(accountActions.updateName(this.props.account.id, name))
      }
      if (this.props.account.email !== email) {
        this.props.dispatch(accountActions.updateEmail(this.props.account.id, email))
      }
      if (this.props.account.imageUrl !== imageUrl) {
        this.props.dispatch(accountActions.updateImageUrl(this.props.account.id, imageUrl))
      }
    } else {
      this.props.dispatch(accountActions.create(name, email))
    }

    this.props.dispatch(accountActions.closeCreateDialog())
  }

  getActions = () => {
    const { dispatch } = this.props
    return [
      <FlatButton label='Cancel'
                  onClick={() => dispatch(accountActions.toggleCreateDialog())}
                  secondary/>,
      <FlatButton label='Submit'
                  onClick={this.onSubmit}
                  primary/>
    ]
  }

  render () {
    const { dispatch, isOpen } = this.props
    return <Dialog autoScrollBodyContent
      modal={false}
      title='Create/Edit account'
      actions={this.getActions()}
      onRequestClose={() => dispatch(accountActions.closeCreateDialog())}
      open={isOpen}>
      <div><TextField defaultValue={this.props.account.name} floatingLabelText='Name' ref='nameInput'/></div>
      <div><TextField defaultValue={this.props.account.email} floatingLabelText='E-Mail' ref='emailInput'/></div>
      <div><TextField defaultValue={this.props.account.imageUrl} floatingLabelText='Image URL' ref='imageUrlInput'/></div>
    </Dialog>
  }
}

export default connect(mapStateToProps)(CreateAccountDialog)
