import { call, put, takeEvery } from 'redux-saga/effects'
import * as Api from '../api';
import * as Donaters from './donaters';
import * as Donatables from './donatables';
import * as Fundables from './fundables';

function* fetchDonaters() {
    try {
        const donaters = yield call(Api.fetchDonaters);
        yield put({type: Donaters.DONATERS_FETCH_SUCCEEDED, payload: donaters});
    } catch (e) {
        yield put({type: Donaters.DONATERS_FETCH_FAILED, message: e.message});
    }
}

function* fetchDonatables() {
    try {
        const donatables = yield call(Api.fetchDonatables);
        yield put({type: Donatables.DONATABLES_FETCH_SUCCEEDED, payload: donatables});
    } catch (e) {
        yield put({type: Donatables.DONATABLES_FETCH_FAILED, message: e.message});
    }
}

function* fetchFundables() {
    try {
        const fundables = yield call(Api.fetchFundables);
        yield put({type: Fundables.FUNDABLES_FETCH_SUCCEEDED, payload: fundables});
    } catch (e) {
        yield put({type: Fundables.FUNDABLES_FETCH_FAILED, message: e.message});
    }
}

export function* donaterSaga() {
    yield takeEvery(Donaters.DONATERS_FETCH_REQUESTED, fetchDonaters);
}

export function* donatablesSaga() {
    yield takeEvery(Donatables.DONATABLES_FETCH_REQUESTED, fetchDonatables);
}

export function* fundablesSaga() {
    yield takeEvery(Fundables.FUNDABLES_FETCH_REQUESTED, fetchFundables);
}