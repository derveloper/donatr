//noinspection ES6UnusedImports
import Inferno from "inferno";
import {connect} from "inferno-redux";

const CurrentDonater = ({donater}) => (
    <div className="clearfix border">
        {!donater ? <div className="col-12 center p1">{'select user'}</div> : null}
        {donater && <div className="col p1">{donater.name}</div>}
        {donater && <div className="col p1 col-right">{donater.balance}</div>}
    </div>
);

function mapStateToProps(state, ownProps) {
    return {donater: state.donaters.filter(d => d.id === ownProps.params.userId)[0]}
}

export default connect(mapStateToProps)(CurrentDonater)