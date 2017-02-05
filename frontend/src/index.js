import Inferno from "inferno";
import {Provider} from "inferno-redux";
import App from "./App";
import "./index.css";
import "./basscss.min.css";
import store from "./redux/store";

Inferno.render(
    <Provider store={store}>
        <App />
    </Provider>,
    document.getElementById('app')
);
