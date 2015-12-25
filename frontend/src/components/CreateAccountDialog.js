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
    dispatch: React.PropTypes.func.isRequired
  }

  createAccount = () => {
    const name = this.refs.nameInput.getValue()
    this.props.dispatch(accountActions.create(name))
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
    return <Dialog
      modal={false}
      title='Dialog With Standard Actions'
      actions={this.getActions()}
      onRequestClose={() => dispatch(accountActions.closeCreateDialog())}
      open={isOpen}>
      <TextField floatingLabelText='Name' ref='nameInput'/>
    </Dialog>
  }
}

export default connect(mapStateToProps)(CreateAccountDialog)
