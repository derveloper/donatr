import { createAction, handleActions } from 'redux-actions'
import request from 'superagent'
import config from 'config'

// ------------------------------------
// Constants
// ------------------------------------
export const SESSION_CREATED = 'SESSION_CREATED'
export const SESSION_CREATE_FAILED = 'SESSION_CREATE_FAILED'
export const SESSION_DESTROYED = 'SESSION_DESTROYED'
export const SESSION_LOGGED_OUT = 'SESSION_LOGGED_OUT'
export const SESSION_SET_CURRENT_ACCOUNT = 'SESSION_SET_CURRENT_ACCOUNT'

// ------------------------------------
// Actions
// ------------------------------------
export const created = createAction(SESSION_CREATED, (value = {}) => value)
export const failed = createAction(SESSION_CREATE_FAILED, (value = {}) => value)
export const destroyed = createAction(SESSION_DESTROYED, (reload = true) => reload)
export const loggedOut = createAction(SESSION_LOGGED_OUT)
export const currentAccount = createAction(SESSION_SET_CURRENT_ACCOUNT, (accountId) => accountId)

export const create = (username, password) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/session')
      .type('form')
      .withCredentials()
      .send({username, password})
      .end((err, res) => {
        if (err) dispatch(failed(false))
        else dispatch(created(true))
      })
  }
}

export const destroy = () => {
  return (dispatch) => {
    request
      .del(config.api.url + '/session')
      .withCredentials()
      .end((err, res) => {
        if (err) dispatch(failed(false))
        else dispatch(destroyed())
      })
  }
}

export function tryToAuthenticate () {
  return (dispatch, getState) => {
    if (!getState().isAuthenticated) {
      request
        .get(config.api.url + '/session')
        .withCredentials()
        .end((err, res) => {
          if (err || res.statusCode === 401) dispatch(loggedOut())
          else dispatch(created(true))
        })
    }
    else dispatch(created(true))
  }
}

export const actions = {
  create,
  created,
  failed,
  destroy,
  destroyed,
  tryToAuthenticate,
  currentAccount
}

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [SESSION_CREATED]: (state, { payload }) => {
    return {isAuthenticated: payload, loginFailed: false, triedToAuthenticate: true, currentAccount: false}
  },
  [SESSION_CREATE_FAILED]: (state, { payload }) => {
    return {isAuthenticated: payload, loginFailed: true, triedToAuthenticate: true, currentAccount: false}
  },
  [SESSION_DESTROYED]: (reload) => {
    if (reload) window.location.reload()
    return {isAuthenticated: false, loginFailed: false, triedToAuthenticate: true, currentAccount: false}
  },
  [SESSION_LOGGED_OUT]: () => {
    return {isAuthenticated: false, loginFailed: false, triedToAuthenticate: true, currentAccount: false}
  },
  [SESSION_SET_CURRENT_ACCOUNT]: (state, { payload }) => {
    return Object.assign({}, state, {currentAccount: payload})
  }
}, {isAuthenticated: false, loginFailed: false, triedToAuthenticate: false, currentAccount: false})
