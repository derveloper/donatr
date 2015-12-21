import { createAction, handleActions } from 'redux-actions'
import request from 'superagent'

// ------------------------------------
// Constants
// ------------------------------------
export const COUNTER_INCREMENT = 'COUNTER_INCREMENT'

// ------------------------------------
// Actions
// ------------------------------------
export const increment = createAction(COUNTER_INCREMENT, (value = 1) => value)

// This is a thunk, meaning it is a function that immediately
// returns a function for lazy evaluation. It is incredibly useful for
// creating async actions, especially when combined with redux-thunk!
// NOTE: This is solely for demonstration purposes. In a real application,
// you'd probably want to dispatch an action of COUNTER_DOUBLE and let the
// reducer take care of this logic.
export const doubleAsync = () => {
  return (dispatch, getState) => {
    request.get('http://localhost:8080/api/session')
      .withCredentials()
      .end(() => dispatch(increment(getState().counter)))
  }
}

export const actions = {
  increment,
  doubleAsync
}

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [COUNTER_INCREMENT]: (state, { payload }) => state + payload
}, 1)
