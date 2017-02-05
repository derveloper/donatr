//noinspection ES6UnusedImports
import Inferno from "inferno";
import Component from "inferno-component";
import {connect} from "inferno-redux";
import * as DonatableReducer from "./redux/donatables";
import * as Api from "./api";
import md5 from "md5";
import injectSheet from 'react-jss'

const styles = {
    donatable: {
        cursor: 'pointer',
        borderWidth: 3,
        borderColor: '#00ff00',
        width: 115,
        maxHeight: 200,
        minHeight: 120,
        overflow: 'hidden'
    },
    form: {
        backgroundColor: '#000',
        width: '100vw',
        height: '100vh'
    },
    button: {
        backgroundColor: '#000',
        border: 'none',
        color: '#00ff00',
        fontSize: '32px',
        cursor: 'pointer'
    },
    grid: {
        display: 'flex',
        justifyContent: 'space-around',
        flexFlow: 'row wrap'
    },
    '@media (min-width: 480px)': {
        app: {
            padding: 20
        }
    }
};

const getDonatableMD5 = (name) =>
    md5(`donat+${name}@fnordeingang.de`);

const donate = (dispatch, from, to, amount) => () => Api.createDonation({from, to, amount});

const Donatable = injectSheet(styles)(({classes, donatable, userId, dispatch}) => (
    <div
        className={`border break-word m1 inline-block align-top ${classes.donatable}`}
        onClick={donate(userId, donatable.id, donatable.minDonationAmount)}
    >
        <img alt="gravatar" src={`https://www.gravatar.com/avatar/${getDonatableMD5(donatable.name)}?s=115`} width="115"/>
        {donatable.name}
    </div>
));

const _onSubmitCreate = (f) => (e) => {
    e.preventDefault();
    Api.createDonatable({
        name: e.target.elements['name'].value,
        email: e.target.elements['email'].value
    });
    f();
};

const CreateForm = injectSheet(styles)(({classes, onSubmitCreate}) => (
    <div className={`z4 fixed block mx-auto ${classes.form}`}>
        <form onSubmit={_onSubmitCreate(onSubmitCreate)}>
            <label className="block mx-auto center">
                <input placeholder="name" name="name" type="text"/>
            </label>
            <label className="block mx-auto center">
                <input placeholder="email" name="email" type="email"/>
            </label>
            <button className={`block mx-auto center ${classes.button}`} type="submit">Create</button>
        </form>
    </div>
));

class App extends Component {
    state = {
        createDonatableFormOpen: false
    };

    componentWillMount() {
        const {dispatch} = this.props;
        dispatch({type: DonatableReducer.DONATABLES_FETCH_REQUESTED})
    }

    toggleForm = () => {
        this.setState({createDonatableFormOpen: !this.state.createDonatableFormOpen})
    };

    render() {
        return (
            <div className={`block mx-auto ${this.props.classes.app}`}>
                <h1>Donatables</h1>
                { this.state.createDonatableFormOpen && <CreateForm onSubmitCreate={this.toggleForm} /> }
                <button className={this.props.classes.button} onClick={this.toggleForm}>+</button>
                <div className={`block ${this.props.classes.grid}`}>
                    { this.props.donatables.map(f => <Donatable
                        userId={this.props.userId}
                        donatable={f}/>)}
                </div>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {donatables: state.donatables}
}

export default connect(mapStateToProps)(injectSheet(styles)(App))
