export const FUNDABLES_FETCH_SUCCEEDED = 'FUNDABLES_FETCH_SUCCEEDED';
export const FUNDABLES_FETCH_FAILED = "FUNDABLES_FETCH_FAILED";
export const FUNDABLES_FETCH_REQUESTED = "FUNDABLES_FETCH_REQUESTED";
export const FUNDABLE_CREATED = "FundableCreated";
export const FUNDABLE_UPDATED = "FundableUpdated";

export default function fundableReducer(state = [], action) {
    switch (action.type) {
        case FUNDABLES_FETCH_SUCCEEDED:
            return action.payload;
        case FUNDABLE_CREATED:
            return [...state, action.payload.fundable];
        case FUNDABLE_UPDATED:
            return state.map(d => action.payload.fundable.id === d.id
                ? {...action.payload.fundable, balance: action.payload.fundable.balance.toFixed(2)}
                : d);
        default:
            return state;
    }
};