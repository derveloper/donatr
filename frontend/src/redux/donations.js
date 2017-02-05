export const DONATIONS_FETCH_SUCCEEDED = 'DONATIONS_FETCH_SUCCEEDED';
export const DONATIONS_FETCH_FAILED = "DONATIONS_FETCH_FAILED";
export const DONATIONS_FETCH_REQUESTED = "DONATIONS_FETCH_REQUESTED";
export const DONATION_CREATED = "DonationCreated";

export default function donationReducer(state = [], action) {
    switch (action.type) {
        case DONATIONS_FETCH_SUCCEEDED:
            console.log("fetched", action);
            return action.payload;
        case DONATION_CREATED:
            console.log("fetcheddfsdfsd", action);
            return [...state, action.payload.donation];
        default:
            return state;
    }
};