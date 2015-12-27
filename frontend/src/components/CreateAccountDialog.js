import React from 'react'
import { connect } from 'react-redux'
import Dialog from 'material-ui/lib/dialog'
import FlatButton from 'material-ui/lib/flat-button'
import TextField from 'material-ui/lib/text-field'
import { actions as accountActions } from '../redux/modules/accounts'

const mapStateToProps = (state) => ({
  isOpen: state.accounts.createDialogOpen || false
})
class CreateAccountDialog extends React.Component {
  static propTypes = {
    isOpen: React.PropTypes.bool.isRequired,
    dispatch: React.PropTypes.func.isRequired,
    defaultName: React.PropTypes.string,
    defaultEmail: React.PropTypes.string
  }

  createAccount = () => {
    const name = this.refs.nameInput.getValue()
    const email = this.refs.emailInput.getValue()
    this.props.dispatch(accountActions.create(name, email))
    this.props.dispatch(accountActions.closeCreateDialog())
  }

  getActions = () => {
    const { dispatch } = this.props
    return [
      <FlatButton label='Cancel'
                  onClick={() => dispatch(accountActions.toggleCreateDialog())}
                  secondary/>,
      <FlatButton label='Submit'
                  onClick={this.createAccount}
                  primary/>
    ]
  }

  render () {
    const { dispatch, isOpen } = this.props
    return <Dialog autoScrollBodyContent
      modal={false}
      title='Create new account'
      actions={this.getActions()}
      onRequestClose={() => dispatch(accountActions.closeCreateDialog())}
      open={isOpen}>
      <div><TextField defaultValue={this.props.defaultName} floatingLabelText='Name' ref='nameInput'/></div>
      <div><TextField defaultValue={this.props.defaultEmail} floatingLabelText='E-Mail' ref='emailInput'/></div>
    </Dialog>
  }
}

export default connect(mapStateToProps)(CreateAccountDialog)
