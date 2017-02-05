export const DONATERS_FETCH_SUCCEEDED = 'DONATERS_FETCH_SUCCEEDED';
export const DONATERS_FETCH_FAILED = "DONATERS_FETCH_FAILED";
export const DONATERS_FETCH_REQUESTED = "DONATERS_FETCH_REQUESTED";
export const DONATER_CREATED = "DonaterCreated";
export const DONATER_UPDATED = "DonaterUpdated";

export default function donaterReducer(state = [], action) {
    switch (action.type) {
        case DONATERS_FETCH_SUCCEEDED:
            console.log("fetched", action);
            return action.payload.map(d => ({...d, balance: d.balance.toFixed(2)}));
        case DONATER_CREATED:
            return [...state, {...action.payload.donater, balance: action.payload.donater.balance.toFixed(2)}];
        case DONATER_UPDATED:
            return state.map(d => action.payload.donater.id === d.id
                    ? {...action.payload.donater, balance: action.payload.donater.balance.toFixed(2)}
                    : d);
        default:
            return state;
    }
};