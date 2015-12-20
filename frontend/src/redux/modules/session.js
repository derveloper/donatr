import { createAction, handleActions } from 'redux-actions'
import request from 'superagent'
import cookie from 'cookie'
import config from 'config'

// ------------------------------------
// Constants
// ------------------------------------
export const SESSION_CREATED = 'SESSION_CREATED'
export const SESSION_CREATE_FAILED = 'SESSION_CREATE_FAILED'
export const SESSION_DESTROYED = 'SESSION_DESTROYED'
export const SESSION_LOGGED_OUT = 'SESSION_LOGGED_OUT'

// ------------------------------------
// Actions
// ------------------------------------
export const created = createAction(SESSION_CREATED, (value = {}) => value)
export const failed = createAction(SESSION_CREATE_FAILED, (value = {}) => value)
export const destroyed = createAction(SESSION_DESTROYED, (reload = true) => reload)
export const loggedOut = createAction(SESSION_LOGGED_OUT)

export const create = (username, password) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/login')
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
      .get(config.api.url + '/logout')
      .withCredentials()
      .end((err, res) => {
        if (err) dispatch(failed(false))
        else dispatch(destroyed())
      })
  }
}

export function tryToAuthenticate () {
  return (dispatch) => {
    if (!hasAuthCookie()) {
      request
        .get(config.api.url + '/')
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
  tryToAuthenticate
}

function hasAuthCookie () {
  const cookies = cookie.parse(document.cookie)
  return !!cookies['auth']
}

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [SESSION_CREATED]: (state, { payload }) => {
    sessionStorage.setItem('isAuthenticated', true)
    return {isAuthenticated: payload, loginFailed: false}
  },
  [SESSION_CREATE_FAILED]: (state, { payload }) => {
    sessionStorage.setItem('isAuthenticated', false)
    return {isAuthenticated: payload, loginFailed: true}
  },
  [SESSION_DESTROYED]: (reload) => {
    if (reload) window.location.reload()
    return {isAuthenticated: false, loginFailed: false}
  },
  [SESSION_LOGGED_OUT]: () => {
    return {isAuthenticated: false, loginFailed: false}
  }
}, {isAuthenticated: false, loginFailed: false})
