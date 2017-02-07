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
import deposit from './components/deposit';

const styles = {
    app: {
        padding: 20,
        paddingTop: 10
    },
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
    multiplicatorWrap: {
        border: '1px inset #000',
        fontSize: '28px',
        color: '#00ff00',
        textAlign: 'right',
    },
    multiplicator: {
        border: 'none',
        fontSize: '28px',
        color: '#00ff00',
        textAlign: 'right',
        width: 56,
    },
    '@media (max-width: 479px)': {
        multiplicatorWrap: {
            border: '1px inset #000',
            fontSize: '20px',
            color: '#00ff00',
            textAlign: 'right',
        },
        multiplicator: {
            fontSize: '20px',
            width: 30,
        }
    }
};

const getDonatableMD5 = (name) =>
    md5(`donatr+${name}@fnordeingang.de`);

const donate = (from, to, value, multiplicator, afterDonate) => () => {
    for (let i = 0; i < multiplicator; i++) {
        Api.createDonation({from, to, value});
    }
    afterDonate();
};

const Donatable = injectSheet(styles)(({classes, donatable, userId, dispatch, multiplicator, afterDonate}) => (
    <div className="flex">
        <div
            className={`border break-word align-top ${classes.donatable}`}
            onClick={donate(userId, donatable.id, donatable.minDonationAmount, multiplicator, afterDonate)}
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

const CreateForm = injectSheet(styles)(({classes, onSubmitCreate}) => (
    <div className={`z4 fixed block mx-auto ${classes.form}`}>
        <form onSubmit={_onSubmitCreate(onSubmitCreate)}>
            <label className="block mx-auto center">
                <input placeholder="name" name="name" type="text"/>
            </label>
            <label className="block mx-auto center">
                <input placeholder="price" name="minDonationAmount" type="decimal"/>
            </label>
            <Button className={`block mx-auto center`} type="submit">Create</Button>
        </form>
    </div>
));

class App extends Component {
    state = {
        createDonatableFormOpen: false,
        multiplicator: 1
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

    render() {
        return (
            <div className={`block mx-auto ${this.props.classes.app}`}>
                { this.state.createDonatableFormOpen && <CreateForm onSubmitCreate={this.toggleForm} /> }
                <div className="clearfix">
                    <div className="col">
                        <Button onClick={this.toggleForm}>+item</Button>
                        <Spacer> ~ </Spacer>
                        <Button onClick={deposit(this.props.params.userId)}>+€</Button>
                        <Spacer> ~ </Spacer>
                        <Link to={`/${this.props.params.userId}/fundables`}>&gt;funding</Link>
                    </div>
                    <div className="col col-right right-align">
                        <label className={this.props.classes.multiplicatorWrap}>
                            x<input onInput={(e) => this.setStateSync({multiplicator: e.target.value})}
                                    className={this.props.classes.multiplicator}
                                    // defaultValue="1"
                                    value={this.state.multiplicator}
                                    name="multiplicator"
                                    type="number"/>
                        </label>
                    </div>
                </div>
                <hr style={{backgroundColor: '#00ff00', borderColor: '#00ff00'}}/>
                <div className={`block ${this.props.classes.grid}`}>
                    { this.props.donatables.map(f => <Donatable
                        afterDonate={() => this.setStateSync({multiplicator: 1})}
                        multiplicator={this.state.multiplicator}
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
