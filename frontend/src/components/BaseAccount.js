import React from 'react';
import md5 from 'md5';
import GridTile from 'material-ui/lib/grid-list/grid-tile';

class BaseAccount extends React.Component {
  static propTypes = {
    account: React.PropTypes.object.isRequired,
    onClick: React.PropTypes.func.isRequired,
    title: React.PropTypes.object.isRequired
  };

  constructor (props) {
    super(props);
    this.state = {mouseDown: false};
  }

  onClick = (e) => {
    this.props.onClick(e);
  };

  getStyles = () => {
    return {
      cursor: 'pointer',
      transform: this.state.mouseDown ? 'scale(0.9)' : 'scale(1)'
    };
  };

  handleMouseDown = () => {
    this.setState({mouseDown: true});
  };

  handleMouseUp = () => {
    this.setState({mouseDown: false});
  };

  render () {
    const { account } = this.props;
    const id = (account.email || account.id).toLowerCase();
    const imageUrl = account.imageUrl || `http://www.gravatar.com/avatar/${md5(id)}?s=200&d=identicon&r=PG`;
    const imgTag = <img src={imageUrl} />;
    return <GridTile
      style={this.getStyles()}
      onMouseDown={this.handleMouseDown}
      onMouseUp={this.handleMouseUp}
      onClick={this.onClick} {...this.props}>
      { imgTag }
    </GridTile>;
  }
}

export default BaseAccount;
