import { createStore, applyMiddleware, compose, combineReducers } from 'redux';
import createSagaMiddleware from 'redux-saga'
import donaters from './donaters';
import donatables from './donatables';
import fundables from './fundables';
import {donaterSaga, donatablesSaga, fundablesSaga} from './saga';

const sagaMiddleware = createSagaMiddleware();

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const middleware = composeEnhancers(
    applyMiddleware(sagaMiddleware)
);

const rootReducer = combineReducers({
    donaters,
    donatables,
    fundables
});

const store = createStore(rootReducer, middleware);

sagaMiddleware.run(donaterSaga);
sagaMiddleware.run(donatablesSaga);
sagaMiddleware.run(fundablesSaga);

const ws = new WebSocket('ws://'+window.location.hostname+':'+window.location.port+'/api/events');
ws.onmessage = msg => {
    const data = JSON.parse(msg.data);
    const type = Object.keys(data)[0];
    store.dispatch({
        type: type,
        payload: data[type]
    });
};

export default store;