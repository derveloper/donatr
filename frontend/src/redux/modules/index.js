import { combineReducers } from 'redux';
import { routeReducer } from 'redux-simple-router';
import session from './session';
import accounts from './accounts';
import navigation from './navigation';
import donatables from './donatables';
import transactions from './transactions';

export default combineReducers({
  accounts,
  session,
  navigation,
  donatables,
  transactions,
  router: routeReducer
});
