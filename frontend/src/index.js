import Inferno from "inferno";
import {Provider} from "inferno-redux";
import { Router, Route, IndexRoute } from 'inferno-router';
import { createBrowserHistory } from 'history';
import Donatables from "./Donatables";
import "./index.css";
import "./basscss.min.css";
import store from "./redux/store";
import App from "./App";

const browserHistory = createBrowserHistory();

function Main({ children }) {
    return (
        <Provider store={store}>
            <div>{children}</div>
        </Provider>
    );
}

function Donatables_({children, params}) {
    return <Donatables userId={params.userId} />
}

const routes = (
    <Router history={ browserHistory }>
        <Route component={ Main }>
            <IndexRoute component={ App }/>
            <Route path=":userId/donatables" component={ Donatables_ } />
            <Route path="*" component={ App }/>
        </Route>
    </Router>
);

Inferno.render(
    routes,
    document.getElementById('app')
);
