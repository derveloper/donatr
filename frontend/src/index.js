import Inferno from "inferno";
import {Provider} from "inferno-redux";
import { Router, Route, IndexRoute } from 'inferno-router';
import { createBrowserHistory } from 'history';
import Donatables from "./Donatables";
import "./index.css";
import "./basscss.min.css";
import store from "./redux/store";
import App from "./App";
import CurrentDonater from './CurrentDonater';

const browserHistory = createBrowserHistory();

function Layout({children, params}) {
    return (
        <div className="root">
            <h1 className="block mx-auto max-width-3 center">donatr</h1>
            <h2 className="block mx-auto max-width-3 right pr3">
                <CurrentDonater params={params} />
            </h2>
            {children}
        </div>
    )
}

function Main({ children, params }) {
    return (
        <Provider store={store}>
            <Layout params={params}>{children}</Layout>
        </Provider>
    );
}

const routes = (
    <Router history={ browserHistory }>
        <Route component={ Main }>
            <IndexRoute component={ App }/>
            <Route path=":userId/donatables" component={ Donatables } />
            <Route path="*" component={ App }/>
        </Route>
    </Router>
);

Inferno.render(
    routes,
    document.getElementById('app')
);
