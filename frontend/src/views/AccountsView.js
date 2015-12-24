import React from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { actions as accountActions } from '../redux/modules/accounts'
import GridList from 'material-ui/lib/grid-list/grid-list'
import GridTile from 'material-ui/lib/grid-list/grid-tile'

const trollface = require('assets/trollface.svg')

// We define mapStateToProps where we'd normally use
// the @connect decorator so the data requirements are clear upfront, but then
// export the decorated component after the main class definition so
// the component can be tested w/ and w/o being connected.
// See: http://rackt.github.io/redux/docs/recipes/WritingTests.html
const mapStateToProps = (state) => ({
  accounts: state.accounts
})
export class HomeView extends React.Component {
  static propTypes = {
    accounts: React.PropTypes.object.isRequired,
    dispatch: React.PropTypes.func.isRequired
  }

  constructor(props) {
    super(props)
    this.componentWillMount = this.componentWillMount.bind(this)
  }

  componentWillMount() {
    this.props.dispatch(accountActions.fetchAll())
  }

  render () {
    return (
      <div>
        <GridList
          cols={3}
          cellHeight={160}>
          {
            this.props.accounts.accounts.map(account => <GridTile
              title={account.name}
            ><img src={trollface}/></GridTile>)
          }
        </GridList>
        <hr />
        <Link to='/about'>Go To About View</Link>
      </div>
    )
  }
}

export default connect(mapStateToProps)(HomeView)
