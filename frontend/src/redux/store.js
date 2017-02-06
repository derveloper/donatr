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

const schema = window.location.port === ""
    ? "wss://"
    : "ws://";
const port = window.location.port === ""
    ? ""
    : ":"+window.location.port;
const wss = schema+window.location.hostname+port+'/api/events';

function start(websocketServerLocation){
    const ws = new WebSocket(websocketServerLocation);
    ws.onmessage = msg => {
        const data = JSON.parse(msg.data);
        const type = Object.keys(data)[0];
        store.dispatch({
            type: type,
            payload: data[type]
        });
    };
    ws.onclose = function(){
        //try to reconnect in 5 seconds
        setTimeout(function(){start(websocketServerLocation)}, 1000);
    };
}

start(wss);

export default store;