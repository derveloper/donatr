export const DONATERS_FETCH_SUCCEEDED = 'DONATERS_FETCH_SUCCEEDED';
export const DONATERS_FETCH_FAILED = "DONATERS_FETCH_FAILED";
export const DONATERS_FETCH_REQUESTED = "DONATERS_FETCH_REQUESTED";
export const DONATER_CREATED = "DonaterCreated";

export default function donaterReducer(state = [], action) {
    switch (action.type) {
        case DONATERS_FETCH_SUCCEEDED:
            console.log("fetched", action);
            return action.payload;
        case DONATER_CREATED:
            console.log("fetcheddfsdfsd", action);
            return [...state, action.payload.donater];
        default:
            return state;
    }
};