import { createAction, handleActions } from 'redux-actions'
import request from 'superagent'
import config from 'config'

// ------------------------------------
// Constants
// ------------------------------------
export const ACCOUNT_CREATED = 'ACCOUNT_CREATED'
export const ACCOUNT_CREATE_FAILED = 'ACCOUNT_CREATE_FAILED'
export const ACCOUNT_DESTROYED = 'ACCOUNT_DESTROYED'
export const ACCOUNT_FETCHED = 'ACCOUNT_FETCHED'

// ------------------------------------
// Actions
// ------------------------------------
export const created = createAction(ACCOUNT_CREATED)
export const failed = createAction(ACCOUNT_CREATE_FAILED)
export const destroyed = createAction(ACCOUNT_DESTROYED)
export const fetched = createAction(ACCOUNT_FETCHED, (result = {accounts: []}) => result)

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

export const create = (username) => {
  return (dispatch) => {
    request
      .post(config.api.url + '/account')
      .withCredentials()
      .send({username})
      .end((err) => {
        if (err) dispatch(failed(false))
        else dispatch(created(true))
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
  destroy
}

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [ACCOUNT_CREATED]: (state) => state,
  [ACCOUNT_CREATE_FAILED]: (state) => state,
  [ACCOUNT_DESTROYED]: (state) => state,
  [ACCOUNT_FETCHED]: (state, { payload }) => payload
}, {accounts: []})
