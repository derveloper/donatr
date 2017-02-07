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
import dialog from "./components/dialog";
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
    const inputs = '<input id="amount-input" placeholder="amount" type="number" step="any" class="swal2-input swal2-donatr-input">';
    dialog('A Donation!', inputs,
        resolve => (resolve([
            document.querySelector('#amount-input').value
        ])),
        () => document.querySelector('#amount-input').focus(),
        result => {
            Api.createDonation({from, to, value: result[0]});
            return "You donated: " + result[0];
        }
    );
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

const CreateFundable = () => {
    const inputs = '<input id="name-input" placeholder="name" class="swal2-input swal2-donatr-input">' +
        '<input id="fundingTarget-input" placeholder="funding target" type="number" step="any" class="swal2-input swal2-donatr-input">';
    dialog('Create a funding', inputs,
        resolve => (resolve([
            document.querySelector('#name-input').value,
            document.querySelector('#fundingTarget-input').value
        ])),
        () => document.querySelector('#name-input').focus(),
        result => {
            Api.createFundable({
                name: result[0],
                fundingTarget: result[1]
            });
            return "You created a Funding: " + result[0];
        }
    );
};

class App extends Component {
    componentWillMount() {
        const {dispatch} = this.props;
        dispatch({type: FundableReducer.FUNDABLES_FETCH_REQUESTED});
        if (this.props.donaters.length === 0) {
            dispatch({type: DonaterReducer.DONATERS_FETCH_REQUESTED});
        }
    }

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
