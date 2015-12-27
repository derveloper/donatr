import React from 'react'
import { connect } from 'react-redux'
import Dialog from 'material-ui/lib/dialog'
import FlatButton from 'material-ui/lib/flat-button'
import TextField from 'material-ui/lib/text-field'
import { MenuItem } from 'material-ui'
import SelectField from 'material-ui/lib/select-field'
import { actions as transactionActions } from '../redux/modules/transactions'

const mapStateToProps = (state) => ({
  isOpen: state.transactions.createDialogOpen || false,
  accounts: state.accounts
})
class TransferMoneyDialog extends React.Component {
  static propTypes = {
    isOpen: React.PropTypes.bool.isRequired,
    accounts: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired
  }

  constructor (props) {
    super(props)
    this.state = {fromValue: undefined, toValue: undefined}
  }

  createTransaction = () => {
    const accountFrom = this.state.fromValue
    const accountTo = this.state.toValue
    const amount = this.refs.amountInput.getValue()
    this.props.dispatch(transactionActions.create(accountFrom, accountTo, amount))
    this.props.dispatch(transactionActions.closeCreateDialog())
  }

  getActions = () => {
    const { dispatch } = this.props
    return [
      <FlatButton label='Cancel'
                  onClick={() => dispatch(transactionActions.toggleCreateDialog())}
                  secondary/>,
      <FlatButton label='Submit'
                  onClick={this.createTransaction}
                  primary/>
    ]
  }

  _handleSelectValueChange = (name, e, index, value) => {
    this.setState({[name]: value})
  }

  render () {
    const items = this.props.accounts.accounts.map(account =>
      <MenuItem key={account.id} value={account.id} primaryText={account.name} />)
    const { dispatch, isOpen } = this.props
    return <Dialog
      modal={false}
      title='Create new account'
      actions={this.getActions()}
      onRequestClose={() => dispatch(transactionActions.closeCreateDialog())}
      open={isOpen}>
      <div><SelectField
        value={this.state.fromValue}
        onChange={this._handleSelectValueChange.bind(null, 'fromValue')}
        ref='fromInput' hintText='From'>{items}</SelectField></div>
      <div><SelectField
        value={this.state.toValue}
        onChange={this._handleSelectValueChange.bind(null, 'toValue')}
        ref='toInput' hintText='To'>{items}</SelectField></div>
      <div><TextField floatingLabelText='Amount' ref='amountInput'/></div>
    </Dialog>
  }
}

export default connect(mapStateToProps)(TransferMoneyDialog)
