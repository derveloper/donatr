import { combineReducers } from 'redux';
import { routeReducer as router } from 'redux-simple-router';
import session from './modules/session';
import accounts from './modules/accounts';
import navigation from './modules/navigation';
import donatables from './modules/donatables';
import transactions from './modules/transactions';

export default combineReducers({
  accounts,
  session,
  navigation,
  donatables,
  transactions,
  router
});
