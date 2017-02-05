import { call, put, takeEvery } from 'redux-saga/effects'
import * as Api from '../api';
import * as Donaters from './donaters';

function* fetchUser() {
    try {
        const donaters = yield call(Api.fetchDonaters);
        yield put({type: Donaters.DONATERS_FETCH_SUCCEEDED, payload: donaters});
    } catch (e) {
        yield put({type: Donaters.DONATERS_FETCH_FAILED, message: e.message});
    }
}

function* donatrSaga() {
    yield takeEvery(Donaters.DONATERS_FETCH_REQUESTED, fetchUser);
}

export default donatrSaga;