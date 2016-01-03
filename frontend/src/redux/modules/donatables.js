import { createAction, handleActions } from 'redux-actions'
import request from 'superagent'
import config from 'config'
import _ from 'underscore'

// ------------------------------------
// Constants
// ------------------------------------
export const DONATABLE_CREATED = 'DONATABLE_CREATED'
export const DONATABLE_DONATED = 'DONATABLE_DONATED'
export const DONATABLE_CREATE_FAILED = 'DONATABLE_CREATE_FAILED'
export const DONATABLE_DESTROYED = 'DONATABLE_DESTROYED'
export const DONATABLE_FETCHED = 'DONATABLE_FETCHED'
export const DONATABLE_TOGGLE_CREATE_DIALOG = 'DONATABLE_TOGGLE_CREATE_DIALOG'
export const DONATABLE_CLOSE_CREATE_DIALOG = 'DONATABLE_CLOSE_CREATE_DIALOG'

// ------------------------------------
// Actions
// ------------------------------------
export const created = createAction(DONATABLE_CREATED, (createdEvent) => createdEvent)
export const donated = createAction(DONATABLE_DONATED, (donation) => donation)
export const failed = createAction(DONATABLE_CREATE_FAILED)
export const destroyed = createAction(DONATABLE_DESTROYED, (id) => id)
export const fetched = createAction(DONATABLE_FETCHED, (result) => result)
export const toggleCreateDialog = createAction(DONATABLE_TOGGLE_CREATE_DIALOG)
export const closeCreateDialog = createAction(DONATABLE_CLOSE_CREATE_DIALOG)

export const fetchAll = () => {
  return (dispatch) => {
    request
      .get(config.api.url + '/aggregate/donatable')
      .set('Content-Type', 'application/json')
      .withCredentials()
      .end((err, res) => {
        if (err) dispatch(failed())
        else dispatch(fetched(res.body))
      })
  }
}

export const create = (name, amount, imageUrl) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/donatable')
      .withCredentials()
      .send({name, amount, imageUrl})
      .end((err, res) => {
        if (err) dispatch(failed(false))
        else dispatch(created(res.body))
      })
  }
}

export const donate = (accountFrom, accountTo) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/transaction')
      .withCredentials()
      .send({accountFrom: accountFrom.id, accountTo: accountTo.id})
      .end((err, res) => {
        if (err) dispatch(failed(false))
        else dispatch(donated(res.body))
      })
  }
}

export const updateAmount = (id, amount) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/donatable/amount')
      .withCredentials()
      .send({id, amount})
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
  donate,
  donated,
  failed,
  destroy,
  toggleCreateDialog,
  closeCreateDialog,
  updateAmount
}

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [DONATABLE_CREATED]: (state, { payload }) => {
    let donatables = state.donatables
    donatables.push({
      id: payload.id,
      name: payload.name,
      amount: payload.amount,
      imageUrl: payload.imageUrl
    })
    return Object.assign({}, state, {donatables})
  },
  [DONATABLE_DONATED]: (state) => state,
  [DONATABLE_CREATE_FAILED]: (state) => state,
  [DONATABLE_DESTROYED]: (state, { payload }) => {
    let donatableId = _.findIndex(state.donatables, {id: payload})
    let donatables = state.donatables
    if (donatableId > -1) {
      donatables.splice(donatableId, 1)
    }
    return Object.assign({}, state, {donatables: donatables})
  },
  [DONATABLE_FETCHED]: (state, { payload }) =>
    Object.assign({}, state, {donatables: payload.donatables}),
  [DONATABLE_TOGGLE_CREATE_DIALOG]: (state) =>
    Object.assign({}, state, {createDialogOpen: !state.createDialogOpen}),
  [DONATABLE_CLOSE_CREATE_DIALOG]: (state) =>
    Object.assign({}, state, {createDialogOpen: false})
}, {donatables: [], createDialogOpen: false})
