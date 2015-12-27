import { createAction, handleActions } from 'redux-actions'
import { DONATABLE_DONATED } from './donatables'
import _ from 'underscore'
import request from 'superagent'
import config from 'config'

// ------------------------------------
// Constants
// ------------------------------------
export const ACCOUNT_CREATED = 'ACCOUNT_CREATED'
export const ACCOUNT_CREATE_FAILED = 'ACCOUNT_CREATE_FAILED'
export const ACCOUNT_DESTROYED = 'ACCOUNT_DESTROYED'
export const ACCOUNT_FETCHED = 'ACCOUNT_FETCHED'
export const ACCOUNT_TOGGLE_CREATE_DIALOG = 'ACCOUNT_TOGGLE_CREATE_DIALOG'
export const ACCOUNT_CLOSE_CREATE_DIALOG = 'ACCOUNT_CLOSE_CREATE_DIALOG'

// ------------------------------------
// Actions
// ------------------------------------
export const created = createAction(ACCOUNT_CREATED, (createdEvent) => createdEvent)
export const failed = createAction(ACCOUNT_CREATE_FAILED)
export const destroyed = createAction(ACCOUNT_DESTROYED)
export const fetched = createAction(ACCOUNT_FETCHED, (result) => result)
export const toggleCreateDialog = createAction(ACCOUNT_TOGGLE_CREATE_DIALOG)
export const closeCreateDialog = createAction(ACCOUNT_CLOSE_CREATE_DIALOG)

export const fetchAll = () => {
  return (dispatch) => {
    request
      .get(config.api.url + '/aggregate/account')
      .set('Content-Type', 'application/json')
      .withCredentials()
      .end((err, res) => {
        if (err) dispatch(failed())
        else dispatch(fetched(res.body))
      })
  }
}

export const create = (name, email) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/account')
      .withCredentials()
      .send({name, email})
      .end((err, res) => {
        if (err) dispatch(failed(false))
        else dispatch(created(res.body))
      })
  }
}

export const destroy = () => {
  return (dispatch) => {
    request
      .del(config.api.url + '/account')
      .withCredentials()
      .end((err) => {
        if (err) dispatch(failed(false))
        else dispatch(destroyed())
      })
  }
}

export const actions = {
  fetchAll,
  create,
  created,
  failed,
  destroy,
  toggleCreateDialog,
  closeCreateDialog
}

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [ACCOUNT_CREATED]: (state, { payload }) => {
    let accounts = state.accounts
    accounts.push({
      id: payload.id,
      name: payload.name,
      email: payload.email,
      balance: 0
    })
    return Object.assign({}, state, {accounts})
  },
  [DONATABLE_DONATED]: (state, { payload }) => {
    let accounts = state.accounts
    let currentAccountIndex = _.findIndex(accounts, {id: payload.accountFrom})
    accounts[currentAccountIndex].balance = (accounts[currentAccountIndex].balance - payload.amount).toFixed(2)
    return Object.assign({}, state, {accounts})
  },
  [ACCOUNT_CREATE_FAILED]: (state) => state,
  [ACCOUNT_DESTROYED]: (state) => state,
  [ACCOUNT_FETCHED]: (state, { payload }) =>
    Object.assign({}, state, {accounts: payload.accounts}),
  [ACCOUNT_TOGGLE_CREATE_DIALOG]: (state) =>
    Object.assign({}, state, {createDialogOpen: !state.createDialogOpen}),
  [ACCOUNT_CLOSE_CREATE_DIALOG]: (state) =>
    Object.assign({}, state, {createDialogOpen: false})
}, {accounts: [], createDialogOpen: false})
