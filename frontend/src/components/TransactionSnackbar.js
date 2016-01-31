import React from 'react';
import Snackbar from 'material-ui/lib/snackbar';
import { connect } from 'react-redux';
import _ from 'underscore';

function buildMessage (donatables, account, snackbarMessageId) {
  if (!donatables || !donatables.length) return null;
  if (!account) return null;

  let toDonatableIndex = _.findIndex(donatables, {id: account});
  const donatable = donatables[toDonatableIndex];
  const id = snackbarMessageId.split('-')[0];
  return `Donated ${donatable.amount}€ to ${donatable.name} -- ${id}`;
}

const mapStateToProps = (state) => ({
  snackbarMessageId: state.session.snackbarMessageId,
  snackbarMessage: buildMessage(state.donatables.donatables, state.session.snackbarMessage, state.session.snackbarMessageId)
});

export default class TransactionSnackbar extends React.Component {
  static propTypes = {
    snackbarMessage: React.PropTypes.string
  };

  constructor (props) {
    super(props);
    this.state = {
      open: false
    };
  }

  componentWillReceiveProps (props) {
    this.setState({open: this.props.snackbarMessage !== props.snackbarMessage});
  }

  handleRequestClose = () => {
    this.setState({
      open: false
    });
  };

  render () {
    return (
      this.props.snackbarMessage
        ? (<div>
            <Snackbar
              open={this.state.open}
              message={this.props.snackbarMessage}
              autoHideDuration={4000}
              onRequestClose={this.handleRequestClose}
            />
          </div>)
        : null
    );
  }
}

export default connect(mapStateToProps)(TransactionSnackbar);
