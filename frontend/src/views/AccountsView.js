import React from 'react'
import { connect } from 'react-redux'
import { actions as accountActions } from '../redux/modules/accounts'
import { actions as sessionActions } from '../redux/modules/session'
import { actions as navActions } from '../redux/modules/navigation'
import { pushPath } from 'redux-simple-router'
import GridList from 'material-ui/lib/grid-list/grid-list'
import GridTile from 'material-ui/lib/grid-list/grid-tile'
import md5 from 'md5'

const mapStateToProps = (state) => ({
  accounts: state.accounts
})
export class AccountsView extends React.Component {
  static propTypes = {
    accounts: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired
  }

  constructor (props) {
    super(props)
    this.componentWillMount = this.componentWillMount.bind(this)
  }

  componentWillMount () {
    this.props.dispatch(navActions.showCurrentAccount(false))
    this.props.dispatch(accountActions.fetchAll())
  }

  onAccountClick = (account) => {
    this.props.dispatch(sessionActions.currentAccount(account))
    this.props.dispatch(pushPath('/donate'))
  }

  render () {
    return (
      <div>
        <GridList
          cols={3}
          cellHeight={160}>
          {
            this.props.accounts.accounts.map(account =>
              <GridTile
                key={account.id}
                title={account.name}
                onClick={() => this.onAccountClick(account)}>
                <img src={`http://www.gravatar.com/avatar/${md5(account.id)}?s=200&d=identicon&r=PG`}/>
              </GridTile>)
          }
        </GridList>
      </div>
    )
  }
}

export default connect(mapStateToProps)(AccountsView)
