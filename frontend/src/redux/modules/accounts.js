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
export const destroyed = createAction(ACCOUNT_DESTROYED, (id) => id)
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

export const updateName = (id, name) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/account/name')
      .withCredentials()
      .send({id, name})
      .end((err) => {
        if (err) dispatch(failed(false))
        else dispatch(fetchAll())
      })
  }
}

export const updateEmail = (id, email) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/account/email')
      .withCredentials()
      .send({id, email})
      .end((err) => {
        if (err) dispatch(failed(false))
        else dispatch(fetchAll())
      })
  }
}

export const updateImageUrl = (id, imageUrl) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/account/image')
      .withCredentials()
      .send({id, imageUrl})
      .end((err) => {
        if (err) dispatch(failed(false))
        else dispatch(fetchAll())
      })
  }
}

export const destroy = (id) => {
  return (dispatch) => {
    request
      .del(config.api.url + '/account')
      .withCredentials()
      .send({id})
      .end((err) => {
        if (err) dispatch(failed(false))
        else dispatch(destroyed(id))
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
  closeCreateDialog,
  updateName,
  updateEmail,
  updateImageUrl
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
    let fromAccountIndex = _.findIndex(accounts, {id: payload.accountFrom})
    let toAccountIndex = _.findIndex(accounts, {id: payload.accountTo})
    const amount = parseFloat(payload.amount)
    if (fromAccountIndex !== -1) {
      accounts[fromAccountIndex].balance =
        (parseFloat(accounts[fromAccountIndex].balance) - amount).toFixed(2)
    }
    if (toAccountIndex !== -1) {
      accounts[toAccountIndex].balance =
        (parseFloat(accounts[toAccountIndex].balance) + amount).toFixed(2)
    }
    return Object.assign({}, state, {accounts})
  },
  [ACCOUNT_CREATE_FAILED]: (state) => state,
  [ACCOUNT_DESTROYED]: (state, { payload }) => {
    let accountId = _.findIndex(state.accounts, {id: payload})
    let accounts = state.accounts
    if (accountId > -1) {
      accounts.splice(accountId, 1);
    }
    return Object.assign({}, state, {accounts: accounts})
  },
  [ACCOUNT_FETCHED]: (state, { payload }) =>
    Object.assign({}, state, {accounts: payload.accounts}),
  [ACCOUNT_TOGGLE_CREATE_DIALOG]: (state) =>
    Object.assign({}, state, {createDialogOpen: !state.createDialogOpen}),
  [ACCOUNT_CLOSE_CREATE_DIALOG]: (state) =>
    Object.assign({}, state, {createDialogOpen: false})
}, {accounts: [], createDialogOpen: false})
