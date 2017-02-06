export const DONATABLES_FETCH_SUCCEEDED = 'DONATABLES_FETCH_SUCCEEDED';
export const DONATABLES_FETCH_FAILED = "DONATABLES_FETCH_FAILED";
export const DONATABLES_FETCH_REQUESTED = "DONATABLES_FETCH_REQUESTED";
export const DONATABLE_CREATED = "DonatableCreated";

export default function donatableReducer(state = [], action) {
    switch (action.type) {
        case DONATABLES_FETCH_SUCCEEDED:
            return action.payload.map(d => ({...d, minDonationAmount: d.minDonationAmount.toFixed(2)}));
        case DONATABLE_CREATED:
            return [...state, {
                ...action.payload.donatable,
                minDonationAmount: action.payload.donatable.minDonationAmount.toFixed(2)
            }];
        default:
            return state;
    }
};