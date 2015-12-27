import React from 'react'
import { connect } from 'react-redux'
import { actions as donatablesActions } from '../redux/modules/donatables'
import { actions as navActions } from '../redux/modules/navigation'
import { pushPath } from 'redux-simple-router'
import GridList from 'material-ui/lib/grid-list/grid-list'
import GridTile from 'material-ui/lib/grid-list/grid-tile'
import _ from 'underscore'
import md5 from 'md5'

const mapStateToProps = (state) => ({
  donatables: state.donatables,
  currentAccount: _.findWhere(state.accounts.accounts, {id: state.session.currentAccount.id})
})
export class DonateView extends React.Component {
  static propTypes = {
    donatables: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired,
    currentAccount: React.PropTypes.object
  }

  constructor (props) {
    super(props)
    this.componentWillMount = this.componentWillMount.bind(this)
  }

  componentWillMount () {
    if (!this.props.currentAccount) this.props.dispatch(pushPath('/'))
    else this.props.dispatch(donatablesActions.fetchAll())
    this.props.dispatch(navActions.showCurrentAccount(true))
  }

  donate = (donatable) => {
    this.props.dispatch(donatablesActions.donate(this.props.currentAccount, donatable))
  }

  render () {
    return (
      <div>
        <GridList
          cols={3}
          cellHeight={160}>
          {
            this.props.donatables.donatables.map(donatable =>
              <GridTile
                key={donatable.id}
                title={`${donatable.name} ${donatable.amount} €`}
                onClick={() => this.donate(donatable)}>
                <img src={`http://www.gravatar.com/avatar/${md5(donatable.id)}?s=200&d=identicon&r=PG`} />
              </GridTile>)
          }
        </GridList>
      </div>
    )
  }
}

export default connect(mapStateToProps)(DonateView)
