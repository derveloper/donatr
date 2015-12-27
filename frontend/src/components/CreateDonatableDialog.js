import React from 'react'
import { connect } from 'react-redux'
import Dialog from 'material-ui/lib/dialog'
import FlatButton from 'material-ui/lib/flat-button'
import TextField from 'material-ui/lib/text-field'
import { actions as donatablesActions } from '../redux/modules/donatables'

const mapStateToProps = (state) => ({
  isOpen: state.donatables.createDialogOpen || false
})
class CreateDonatableDialog extends React.Component {
  static propTypes = {
    isOpen: React.PropTypes.bool.isRequired,
    dispatch: React.PropTypes.func.isRequired
  }

  createDonatable = () => {
    const name = this.refs.nameInput.getValue()
    const amount = this.refs.amountInput.getValue()
    const imageUrl = this.refs.imageUrlInput.getValue()
    this.props.dispatch(donatablesActions.create(name, amount, imageUrl))
    this.props.dispatch(donatablesActions.closeCreateDialog())
  }

  getActions = () => {
    const { dispatch } = this.props
    return [
      <FlatButton label='Cancel'
                  onClick={() => dispatch(donatablesActions.toggleCreateDialog())}
                  secondary/>,
      <FlatButton label='Submit'
                  onClick={this.createDonatable}
                  primary/>
    ]
  }

  render () {
    const { dispatch, isOpen } = this.props
    return <Dialog
      modal={false}
      title='Create donatable'
      actions={this.getActions()}
      onRequestClose={() => dispatch(donatablesActions.closeCreateDialog())}
      open={isOpen}>
      <div><TextField floatingLabelText='Name' ref='nameInput'/></div>
      <div><TextField floatingLabelText='Amount' ref='amountInput'/></div>
      <div><TextField floatingLabelText='Image URL' ref='imageUrlInput'/></div>
    </Dialog>
  }
}

export default connect(mapStateToProps)(CreateDonatableDialog)
