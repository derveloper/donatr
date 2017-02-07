//noinspection ES6UnusedImports
import Inferno from "inferno";
import Component from "inferno-component";
import {connect} from "inferno-redux";
import Link from "./components/Link";
import Button from "./components/Button";
import * as DonaterReducer from "./redux/donaters";
import * as Api from "./api";
import md5 from "md5";
import injectSheet from "react-jss";
import dialog from "./components/dialog";

const styles = {
    app: {
        padding: 20,
        paddingTop: 10
    },
    donater: {
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
    },
    link: {
        fontSize: 16
    }
};

const Donater = injectSheet(styles)(({classes, donater}) => (
    <Link className={classes.link} to={`/${donater.id}/donatables`}>
        <div
            className={`border break-word inline-block align-top ${classes.donater}`}
        >
            <img alt="gravatar" src={`https://www.gravatar.com/avatar/${md5(donater.email)}?s=115`} width="115"/>
            <span className="p1">{donater.name}</span>
        </div>
    </Link>
));

const CreateDonater = () => {
    const inputs = '<input id="username-input" placeholder="name" class="swal2-input swal2-donatr-input">' +
        '<input id="email-input" placeholder="email" type="email" class="swal2-input swal2-donatr-input">';
    dialog('Create a User', inputs,
        resolve => (resolve([
            document.querySelector('#username-input').value,
            document.querySelector('#email-input').value
        ])),
        () => document.querySelector('#username-input').focus(),
        result => {
            Api.createDonater({
                name: result[0],
                email: result[1]
            });
            return "You created a User: " + result[0];
        }
    );
};

class App extends Component {

    componentWillMount() {
        const {dispatch} = this.props;
        dispatch({type: DonaterReducer.DONATERS_FETCH_REQUESTED})
    }

    render() {
        return (
            <div className={`block mx-auto ${this.props.classes.app}`}>
                <div>
                    <Button onClick={CreateDonater}>+user</Button>
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
