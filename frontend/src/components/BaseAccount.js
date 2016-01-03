import React from 'react'
import md5 from 'md5'
import GridTile from 'material-ui/lib/grid-list/grid-tile'

class BaseAccount extends React.Component {
  static propTypes = {
    account: React.PropTypes.object.isRequired,
    onClick: React.PropTypes.func.isRequired,
    title: React.PropTypes.string.isRequired
  }

  render () {
    const { account, onClick } = this.props
    const imageUrl = account.imageUrl || `http://www.gravatar.com/avatar/${md5(account.email || account.id)}?s=200&d=identicon&r=PG`
    const imgTag = <img src={imageUrl} />
    return <GridTile
      onClick={onClick} {...this.props}>
      { imgTag }
    </GridTile>
  }
}

export default BaseAccount
