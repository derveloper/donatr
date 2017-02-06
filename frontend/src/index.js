import Inferno from "inferno";
import {Provider} from "inferno-redux";
import { Router, Route, IndexRoute, IndexLink } from 'inferno-router';
import { createBrowserHistory } from 'history';
import Donatables from "./Donatables";
import Fundables from "./Fundables";
import "./index.css";
import "./basscss.min.css";
import "sweetalert/dist/sweetalert.css";
import store from "./redux/store";
import App from "./App";
import CurrentDonater from './components/CurrentDonater';
import injectSheet from 'react-jss'

const browserHistory = createBrowserHistory();

const styles = {
    link: {
        color: '#00ff00',
        textDecoration: 'none'
    }
};

const Layout = injectSheet(styles)(({classes, params, children}) => {
    return (
        <div className="root">
            <h1 className="block mx-auto center">
                <IndexLink className={classes.link}>donatr</IndexLink>
            </h1>
            <h2 className="block mx-auto right-align pb0 pr3 pt3 pl3 m0">
                <CurrentDonater params={params} />
            </h2>
            {children}
        </div>
    )
});

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
            <Route path=":userId/fundables" component={ Fundables } />
            <Route path="*" component={ App }/>
        </Route>
    </Router>
);

Inferno.render(
    routes,
    document.getElementById('app')
);
