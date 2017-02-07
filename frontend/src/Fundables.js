//noinspection ES6UnusedImports
import Inferno from "inferno";
import Component from "inferno-component";
import {connect} from "inferno-redux";
import Link from "./components/Link";
import Button from "./components/Button";
import Spacer from "./components/Spacer";
import * as FundableReducer from "./redux/fundables";
import * as DonaterReducer from "./redux/donaters";
import * as Api from "./api";
import md5 from "md5";
import injectSheet from "react-jss";
import swal from "sweetalert2";
import deposit from "./components/deposit";

const styles = {
    app: {
        padding: 20,
        paddingTop: 10
    },
    fundable: {
        cursor: 'pointer',
        borderWidth: 3,
        borderColor: '#00ff00',
        width: 115,
        maxHeight: 200,
        minHeight: 120,
        overflow: 'hidden',
        margin: '.1rem'
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
    }
};

const getFundableMD5 = (name) =>
    md5(`donatr+fundable-${name}@fnordeingang.de`);

const donate = (from, to) => () => {
    swal({
        title: "A Donation!",
        text: "Donate whatever you like",
        input: "text",
        showCancelButton: true,
        animation: "slide-from-top",
        inputPlaceholder: "amount"
    }).then(function (inputValue) {
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
        className={`border break-word inline-block align-top ${classes.fundable}`}
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

const CreateFundable = () => {
    swal({
        title: 'Create a funding',
        background: '#000',
        html:
        '<input id="name-input" placeholder="name" class="swal2-input swal2-donatr-input">' +
        '<input id="fundingTarget-input" placeholder="target" class="swal2-input swal2-donatr-input">',
        preConfirm: function () {
            return new Promise(function (resolve) {
                resolve([
                    document.querySelector('#name-input').value,
                    document.querySelector('#fundingTarget-input').value
                ])
            })
        },
        onOpen: function () {
            document.querySelector('#name-input').focus()
        }
    }).then(function (result) {
        const name = result[0];
        const fundingTarget = result[1];
        Api.createFundable({
            name,
            fundingTarget
        });
        swal({
            titleText: "Nice!",
            background: '#000',
            text: "You created a Funding: " + name,
            type: "success"
        });
    }).catch(swal.noop)
};

class App extends Component {
    state = {
        createFundableFormOpen: false
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

    render() {
        return (
            <div className={`block mx-auto ${this.props.classes.app}`}>
                <div>
                    <Button onClick={CreateFundable}>+fund</Button>
                    <Spacer> ~ </Spacer>
                    <Button onClick={deposit(this.props.params.userId)}>+€</Button>
                    <Spacer> ~ </Spacer>
                    <Link to={`/${this.props.params.userId}/donatables`}>&lt;items</Link>
                </div>
                <hr style={{backgroundColor: '#00ff00', borderColor: '#00ff00'}}/>
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
