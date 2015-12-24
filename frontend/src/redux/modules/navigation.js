import { createAction, handleActions } from 'redux-actions'

// ------------------------------------
// Constants
// ------------------------------------
export const NAV_TOGGLE = 'NAV_TOGGLE'
export const NAV_OPEN = 'NAV_OPEN'

// ------------------------------------
// Actions
// ------------------------------------
export const toggle = createAction(NAV_TOGGLE)
export const open = createAction(NAV_OPEN, value => value)

export const actions = {
  toggle,
  open
}

// ------------------------------------
// Reducer
// ------------------------------------
export default handleActions({
  [NAV_TOGGLE]: (state) => {
    return {isOpen: !state.isOpen}
  },
  [NAV_OPEN]: (state, { isOpen }) => {
    return {isOpen: isOpen || false}
  }
}, {isOpen: false})
