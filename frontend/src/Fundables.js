//noinspection ES6UnusedImports
import Inferno from "inferno";
import Component from "inferno-component";
import {connect} from "inferno-redux";
import {Link} from "inferno-router";
import * as FundableReducer from "./redux/fundables";
import * as DonaterReducer from "./redux/donaters";
import * as Api from "./api";
import md5 from "md5";
import injectSheet from 'react-jss';
import swal from 'sweetalert';

const styles = {
    fundable: {
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
        fontSize: '32px',
        textDecoration: 'none'
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
    spacer: {
        backgroundColor: '#000',
        border: 'none',
        color: '#00ff00',
        fontSize: '32px',
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

const getFundableMD5 = (name) =>
    md5(`donatr+fundable-${name}@fnordeingang.de`);

const donate = (from, to) => () => {
    swal({
            title: "A Donation!",
            text: "Donate whatever you like",
            type: "input",
            showCancelButton: true,
            closeOnConfirm: false,
            animation: "slide-from-top",
            inputPlaceholder: "amount"
        },
        function(inputValue){
            if (inputValue === false) return false;

            if (inputValue === "") {
                swal.showInputError("You need to donate something!");
                return false
            }

            Api.createDonation({from, to, value: inputValue});

            swal("Nice!", "You donated: " + inputValue, "success");
        });
};

const Fundable = injectSheet(styles)(({classes, fundable, userId, dispatch}) => (
    <div
        className={`border break-word m1 inline-block align-top ${classes.fundable}`}
        onClick={donate(userId, fundable.id)}
    >
        <img alt="gravatar" src={`https://www.gravatar.com/avatar/${getFundableMD5(fundable.name)}?s=115`} width="115"/>
        <div className="p1">
            {fundable.name} /
            {fundable.fundingTarget} /
            {fundable.balance}
        </div>
    </div>
));

const _onSubmitCreate = (f) => (e) => {
    e.preventDefault();
    Api.createFundable({
        name: e.target.elements['name'].value,
        fundingTarget: e.target.elements['fundingTarget'].value
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
                <input placeholder="target" name="fundingTarget" type="decimal"/>
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
        createFundableFormOpen: false,
        createDepositFormOpen: false
    };

    componentWillMount() {
        const {dispatch} = this.props;
        dispatch({type: FundableReducer.FUNDABLES_FETCH_REQUESTED});
        if (this.props.donaters.length === 0) {
            dispatch({type: DonaterReducer.DONATERS_FETCH_REQUESTED});
        }
    }

    toggleForm = () => {
        this.setState({createFundableFormOpen: !this.state.createFundableFormOpen})
    };

    toggleDepositForm = () => {
        this.setState({createDepositFormOpen: !this.state.createDepositFormOpen})
    };

    render() {
        return (
            <div className={`block mx-auto ${this.props.classes.app}`}>
                { this.state.createFundableFormOpen && <CreateForm onSubmitCreate={this.toggleForm} /> }
                { this.state.createDepositFormOpen && <DepositForm userId={this.props.params.userId}
                                                                   onSubmitCreate={this.toggleDepositForm} /> }
                <button className={this.props.classes.button} onClick={this.toggleForm}>+fundable</button>
                <span className={this.props.classes.spacer}> ~ </span>
                <button className={this.props.classes.button} onClick={this.toggleDepositForm}>+€</button>
                <span className={this.props.classes.spacer}> ~ </span>
                <Link className={this.props.classes.link} to={`/${this.props.params.userId}/donatables`}>&lt;items</Link>
                <div className={`block ${this.props.classes.grid}`}>
                    { this.props.fundables.map(f => <Fundable
                        userId={this.props.params.userId}
                        fundable={f}/>)}
                </div>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {fundables: state.fundables, donaters: state.donaters}
}

export default connect(mapStateToProps)(injectSheet(styles)(App))
