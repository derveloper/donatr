import { combineReducers } from 'redux'
import { routeReducer } from 'redux-simple-router'
import session from './session'
import accounts from './accounts'

export default combineReducers({
  accounts,
  session,
  router: routeReducer
})
