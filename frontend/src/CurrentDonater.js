//noinspection ES6UnusedImports
import Inferno from "inferno";
import {connect} from "inferno-redux";

const CurrentDonater = ({donater}) => donater && (
    <div className="clearfix border">
        <div className="col p1">{donater.name}</div>
        <div className="col p1">{donater.balance}</div>
    </div>
);

function mapStateToProps(state, ownProps) {
    return {donater: state.donaters.filter(d => d.id === ownProps.params.userId)[0]}
}

export default connect(mapStateToProps)(CurrentDonater)