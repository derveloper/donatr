//noinspection ES6UnusedImports
import Inferno from "inferno";
import Component from "inferno-component";
import {connect} from "inferno-redux";
import Link from "./components/Link";
import Button from "./components/Button";
import Spacer from "./components/Spacer";
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
        overflow: 'hidden',
        margin: '.1rem',
        '&:active': {
            backgroundColor: 'rgba(0,255,0,0.4)'
        }
    },
    form: {
        backgroundColor: '#000',
        width: '100vw',
        height: '100vh'
    },
    grid: {
        display: 'flex',
        justifyContent: 'space-around',
        flexFlow: 'row wrap'
    },
    '@media (min-width: 480px)': {
        app: {
            padding: 20,
            paddingTop: 10
        }
    },
    '@media (max-width: 479px)': {
        app: {
            padding: 20,
            paddingTop: 10
        }
    }
};

const getDonatableMD5 = (name) =>
    md5(`donatr+${name}@fnordeingang.de`);

const donate = (from, to, value) => () => Api.createDonation({from, to, value});

const Donatable = injectSheet(styles)(({classes, donatable, userId, dispatch}) => (
    <div className="flex">
        <div
            className={`border break-word align-top ${classes.donatable}`}
            onClick={donate(userId, donatable.id, donatable.minDonationAmount)}
        >
            <img alt="gravatar" src={`https://www.gravatar.com/avatar/${getDonatableMD5(donatable.name)}?s=115`} width="115"/>
            <span className="p1">{donatable.name} ({donatable.minDonationAmount})</span>
        </div>
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
                    <Button onClick={this.toggleForm}>+item</Button>
                    <Spacer> ~ </Spacer>
                    <Button onClick={this.toggleDepositForm}>+€</Button>
                    <Spacer> ~ </Spacer>
                    <Link to={`/${this.props.params.userId}/fundables`}>&gt;funding</Link>
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
