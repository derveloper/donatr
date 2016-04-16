import { createAction, handleActions } from 'redux-actions';
import request from 'superagent';
import config from 'config';
import { donated } from './donatables';

// ------------------------------------
// Constants
// ------------------------------------
export const TRANSACTION_CREATED = 'TRANSACTION_CREATED';
export const TRANSACTION_CREATE_FAILED = 'TRANSACTION_CREATE_FAILED';
export const TRANSACTION_TOGGLE_CREATE_DIALOG = 'TRANSACTION_TOGGLE_CREATE_DIALOG';
export const TRANSACTION_CLOSE_CREATE_DIALOG = 'TRANSACTION_CLOSE_CREATE_DIALOG';

// ------------------------------------
// Actions
// ------------------------------------
export const created = createAction(TRANSACTION_CREATED, (createdEvent) => createdEvent);
export const failed = createAction(TRANSACTION_CREATE_FAILED);
export const toggleCreateDialog = createAction(TRANSACTION_TOGGLE_CREATE_DIALOG);
export const closeCreateDialog = createAction(TRANSACTION_CLOSE_CREATE_DIALOG);

export const create = (accountFrom, accountTo, amount) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/transaction')
      .withCredentials()
      .send({accountFrom: accountFrom, accountTo: accountTo, amount: amount})
      .end((err, res) => {
        if (err) dispatch(failed(false));
        else dispatch(donated(res.body));
      });
  };
};

export const actions = {
  create,
  failed,
  toggleCreateDialog,
  closeCreateDialog
};

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [TRANSACTION_CREATE_FAILED]: (state) => state,
  [TRANSACTION_TOGGLE_CREATE_DIALOG]: (state) =>
    Object.assign({}, state, {createDialogOpen: !state.createDialogOpen}),
  [TRANSACTION_CLOSE_CREATE_DIALOG]: (state) =>
    Object.assign({}, state, {createDialogOpen: false})
}, {createDialogOpen: false});
