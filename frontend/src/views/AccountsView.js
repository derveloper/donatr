import React from 'react'
import { connect } from 'react-redux'
import { actions as accountActions } from '../redux/modules/accounts'
import { actions as navActions } from '../redux/modules/navigation'
import GridList from 'material-ui/lib/grid-list/grid-list'
import Account from 'components/Account'

const mapStateToProps = (state) => ({
  accounts: state.accounts
})
export class AccountsView extends React.Component {
  static propTypes = {
    accounts: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired
  };

  constructor (props) {
    super(props)
    this.componentWillMount = this.componentWillMount.bind(this)
  }

  componentWillMount () {
    this.props.dispatch(navActions.showCurrentAccount(false))
    this.props.dispatch(accountActions.fetchAll())
  }

  render () {
    return (
      <div>
        <GridList
          cols={3}
          cellHeight={160}>
          {
            this.props.accounts.accounts.map(account =>
              <Account
                key={account.id}
                account={account} />)
          }
        </GridList>
      </div>
    )
  }
}

export default connect(mapStateToProps)(AccountsView)
