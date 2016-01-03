import React from 'react'
import { connect } from 'react-redux'
import { actions as donatablesActions } from '../redux/modules/donatables'
import { actions as navActions } from '../redux/modules/navigation'
import { pushPath } from 'redux-simple-router'
import GridList from 'material-ui/lib/grid-list/grid-list'
import _ from 'underscore'
import Donatable from 'components/Donatable'

const mapStateToProps = (state) => ({
  donatables: state.donatables,
  session: state.session,
  currentAccount: _.findWhere(state.accounts.accounts, {id: state.session.currentAccount.id})
})
export class DonateView extends React.Component {
  static propTypes = {
    donatables: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired,
    session: React.PropTypes.object,
    currentAccount: React.PropTypes.object
  }

  constructor (props) {
    super(props)
    this.componentWillMount = this.componentWillMount.bind(this)
    this.componentWillReceiveProps = this.componentWillReceiveProps.bind(this)
  }

  componentWillMount () {
    if (!this.props.currentAccount) this.props.dispatch(pushPath('/'))
    else this.props.dispatch(donatablesActions.fetchAll())
    this.props.dispatch(navActions.showCurrentAccount(true))
  }

  componentWillReceiveProps (props) {
    if (!props.currentAccount && !props.session.editMode) this.props.dispatch(pushPath('/'))
  }

  render () {
    return (
      <div>
        <GridList
          cols={3}
          cellHeight={160}>
          { this.props.donatables.donatables.map(donatable =>
            <Donatable key={donatable.id}
                       donatable={donatable}
                       currentAccount={this.props.currentAccount} />)
          }
        </GridList>
      </div>
    )
  }
}

export default connect(mapStateToProps)(DonateView)
