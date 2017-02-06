//noinspection ES6UnusedImports
import Inferno from "inferno";
import Component from "inferno-component";
import {connect} from "inferno-redux";
import Link from "./components/Link";
import Button from "./components/Button";
import * as DonaterReducer from "./redux/donaters";
import * as Api from "./api";
import md5 from "md5";
import injectSheet from 'react-jss'

const styles = {
    donater: {
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
    grid: {
        display: 'flex',
        justifyContent: 'space-around',
        flexFlow: 'row wrap'
    },
    link: {
        fontSize: 16
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

const Donater = injectSheet(styles)(({classes, donater}) => (
    <Link className={classes.link} to={`/${donater.id}/donatables`}>
        <div
            className={`border break-word m1 inline-block align-top ${classes.donater}`}
        >
            <img alt="gravatar" src={`https://www.gravatar.com/avatar/${md5(donater.email)}?s=115`} width="115"/>
            <span className="p1">{donater.name}</span>
        </div>
    </Link>
));

const _onSubmitCreate = (f) => (e) => {
    e.preventDefault();
    Api.createDonater({
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
        createDonaterFormOpen: false
    };

    componentWillMount() {
        const {dispatch} = this.props;
        dispatch({type: DonaterReducer.DONATERS_FETCH_REQUESTED})
    }

    toggleForm = () => {
        this.setState({createDonaterFormOpen: !this.state.createDonaterFormOpen})
    };

    render() {
        return (
            <div className={`block mx-auto ${this.props.classes.app}`}>
                { this.state.createDonaterFormOpen && <CreateForm onSubmitCreate={this.toggleForm} /> }
                <div>
                    <Button onClick={this.toggleForm}>+user</Button>
                </div>
                <hr style={{backgroundColor: '#00ff00', borderColor: '#00ff00'}}/>
                <div className={`block ${this.props.classes.grid}`}>
                    { this.props.donaters.map(f => <Donater donater={f}/>)}
                </div>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {donaters: state.donaters}
}

export default connect(mapStateToProps)(injectSheet(styles)(App))
