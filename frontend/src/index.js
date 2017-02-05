import Inferno from "inferno";
import {Provider} from "inferno-redux";
import { Router, Route, IndexRoute } from 'inferno-router';
import { createBrowserHistory } from 'history';
import App from "./App";
import Donatables from "./Donatables";
import "./index.css";
import "./basscss.min.css";
import store from "./redux/store";

const browserHistory = createBrowserHistory();

function Main({ children }) {
    return (
        <Provider store={store}>
            <div>{children}</div>
        </Provider>
    );
}

const routes = (
    <Router history={ browserHistory }>
        <Route component={ Main }>
            <IndexRoute component={ App }/>
            <Route path="donatables" component={ Donatables } />
            <Route path="*" component={ App }/>
        </Route>
    </Router>
);

Inferno.render(
    routes,
    document.getElementById('app')
);
