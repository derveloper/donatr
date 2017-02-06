const NavBar = ({elements}) => (
    <div>
        <button className={this.props.classes.button} onClick={this.toggleForm}>+item</button>
        <span className={this.props.classes.spacer}> ~ </span>
        <button className={this.props.classes.button} onClick={this.toggleDepositForm}>+€</button>
        <span className={this.props.classes.spacer}> ~ </span>
        <Link className={this.props.classes.link} to={`/${this.props.params.userId}/fundables`}>&gt;funding</Link>
    </div>
);