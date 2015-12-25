import { combineReducers } from 'redux'
import { routeReducer } from 'redux-simple-router'
import session from './session'
import accounts from './accounts'
import navigation from './navigation'
import donatables from './donatables'

export default combineReducers({
  accounts,
  session,
  navigation,
  donatables,
  router: routeReducer
})
