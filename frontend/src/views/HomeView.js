import React from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { actions as counterActions } from '../redux/modules/counter'
import { actions as sessionActions } from '../redux/modules/session'
import styles from './HomeView.scss'

// We define mapStateToProps where we'd normally use
// the @connect decorator so the data requirements are clear upfront, but then
// export the decorated component after the main class definition so
// the component can be tested w/ and w/o being connected.
// See: http://rackt.github.io/redux/docs/recipes/WritingTests.html
const mapStateToProps = (state) => ({
  counter: state.counter
})
export class HomeView extends React.Component {
  static propTypes = {
    counter: React.PropTypes.number.isRequired,
    dispatch: React.PropTypes.func.isRequired
  }

  render () {
    return (
      <div className='container-fluid text-xs-center'>
        <h1>Welcome to the React Redux Starter Kit</h1>
        <h2>
          Sample Counter:&nbsp;
          <span className={styles['counter--green']}>{this.props.counter}</span>
        </h2>
        <button type='button' className='btn btn-primary'
                onClick={() => {
                  this.props.dispatch(sessionActions.destroy())
                }}>
          Increment
        </button>
        <button type='button' className='btn btn-primary'
                onClick={() => this.props.dispatch(counterActions.doubleAsync())}>
          Double (Async)
        </button>
        <hr />
        <Link to='/about'>Go To About View</Link>
      </div>
    )
  }
}

export default connect(mapStateToProps)(HomeView)
