//noinspection ES6UnusedImports
import Inferno from "inferno";
import Component from "inferno-component";
import {connect} from "inferno-redux";
import {Link} from "inferno-router";
import * as DonatableReducer from "./redux/donatables";
import * as DonaterReducer from "./redux/donaters";
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
    link: {
        color: '#00ff00',
        textDecoration: 'none',
        fontSize: '28px'
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
        fontSize: '28px',
        cursor: 'pointer'
    },
    spacer: {
        backgroundColor: '#000',
        border: 'none',
        color: '#00ff00',
        fontSize: '28px',
    },
    grid: {
        display: 'flex',
        justifyContent: 'space-around',
        flexFlow: 'row wrap'
    },
    '@media (min-width: 480px)': {
        app: {
            padding: 20
        },
        link: {
            fontSize: '28px'
        },
        but: {
            fontSize: '28px'
        }
    },
    '@media (max-width: 479px)': {
        app: {
            padding: 20,
            paddingTop: 10
        },
        link: {
            fontSize: '20px'
        },
        button: {
            fontSize: '20px'
        }
    }
};

const getDonatableMD5 = (name) =>
    md5(`donatr+${name}@fnordeingang.de`);

const donate = (from, to, value) => () => Api.createDonation({from, to, value});

const Donatable = injectSheet(styles)(({classes, donatable, userId, dispatch}) => (
    <div
        className={`border break-word m1 inline-block align-top ${classes.donatable}`}
        onClick={donate(userId, donatable.id, donatable.minDonationAmount)}
    >
        <img alt="gravatar" src={`https://www.gravatar.com/avatar/${getDonatableMD5(donatable.name)}?s=115`} width="115"/>
        <span className="p1">{donatable.name}</span>
    </div>
));

const _onSubmitCreate = (f) => (e) => {
    e.preventDefault();
    Api.createDonatable({
        name: e.target.elements['name'].value,
        minDonationAmount: e.target.elements['minDonationAmount'].value
    });
    f();
};

const _onSubmitDeposit = (f, userId) => (e) => {
    e.preventDefault();
    Api.createDonation({
        to: userId,
        value: e.target.elements['value'].value
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
                <input placeholder="price" name="minDonationAmount" type="decimal"/>
            </label>
            <button className={`block mx-auto center ${classes.button}`} type="submit">Create</button>
        </form>
    </div>
));

const DepositForm = injectSheet(styles)(({classes, onSubmitCreate, userId}) => (
    <div className={`z4 fixed block mx-auto ${classes.form}`}>
        <form onSubmit={_onSubmitDeposit(onSubmitCreate, userId)}>
            <label className="block mx-auto center">
                <input placeholder="amount" name="value" type="decimal"/>
            </label>
            <button className={`block mx-auto center ${classes.button}`} type="submit">Deposit</button>
        </form>
    </div>
));

class App extends Component {
    state = {
        createDonatableFormOpen: false,
        createDepositFormOpen: false
    };

    componentWillMount() {
        const {dispatch} = this.props;
        dispatch({type: DonatableReducer.DONATABLES_FETCH_REQUESTED});
        if (this.props.donaters.length === 0) {
            dispatch({type: DonaterReducer.DONATERS_FETCH_REQUESTED});
        }
    }

    toggleForm = () => {
        this.setState({createDonatableFormOpen: !this.state.createDonatableFormOpen})
    };

    toggleDepositForm = () => {
        this.setState({createDepositFormOpen: !this.state.createDepositFormOpen})
    };

    render() {
        return (
            <div className={`block mx-auto ${this.props.classes.app}`}>
                { this.state.createDonatableFormOpen && <CreateForm onSubmitCreate={this.toggleForm} /> }
                { this.state.createDepositFormOpen && <DepositForm userId={this.props.params.userId}
                                                                   onSubmitCreate={this.toggleDepositForm} /> }
                <div>
                    <button className={this.props.classes.button} onClick={this.toggleForm}>+item</button>
                    <span className={this.props.classes.spacer}> ~ </span>
                    <button className={this.props.classes.button} onClick={this.toggleDepositForm}>+€</button>
                    <span className={this.props.classes.spacer}> ~ </span>
                    <Link className={this.props.classes.link} to={`/${this.props.params.userId}/fundables`}>&gt;funding</Link>
                </div>
                <hr style={{backgroundColor: '#00ff00', borderColor: '#00ff00'}}/>
                <div className={`block ${this.props.classes.grid}`}>
                    { this.props.donatables.map(f => <Donatable
                        userId={this.props.params.userId}
                        donatable={f}/>)}
                </div>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {donatables: state.donatables, donaters: state.donaters}
}

export default connect(mapStateToProps)(injectSheet(styles)(App))
