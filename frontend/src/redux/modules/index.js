import { combineReducers } from 'redux'
import { routeReducer } from 'redux-simple-router'
import counter from './counter'
import session from './session'

export default combineReducers({
  counter,
  session,
  router: routeReducer
})
