import { createAction, handleActions } from 'redux-actions';

// ------------------------------------
// Constants
// ------------------------------------
export const NAV_TOGGLE = 'NAV_TOGGLE';
export const NAV_OPEN = 'NAV_OPEN';
export const NAV_SHOW_CURRENT_ACCOUNT = 'NAV_SHOW_CURRENT_ACCOUNT';

// ------------------------------------
// Actions
// ------------------------------------
export const toggle = createAction(NAV_TOGGLE);
export const open = createAction(NAV_OPEN, value => value);
export const showCurrentAccount = createAction(NAV_SHOW_CURRENT_ACCOUNT);

export const actions = {
  toggle,
  open,
  showCurrentAccount
};

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [NAV_TOGGLE]: (state) => {
    return Object.assign({}, state, {isOpen: !state.isOpen});
  },
  [NAV_OPEN]: (state, { payload }) => {
    return Object.assign({}, state, {isOpen: payload || false});
  },
  [NAV_SHOW_CURRENT_ACCOUNT]: (state, { payload }) => {
    return Object.assign({}, state, {currentAccountVisible: payload});
  }
}, {isOpen: false, currentAccountVisible: false});
