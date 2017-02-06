//noinspection ES6UnusedImports
import Inferno from "inferno";
import injectSheet from 'react-jss';

const styles = {
    spacer: {
        backgroundColor: '#000',
        border: 'none',
        color: '#00ff00',
        fontSize: '28px',
    },
    '@media (max-width: 479px)': {
        spacer: {
            fontSize: '20px'
        }
    }
};

const Spacer = injectSheet(styles)(({ children, classes, ...props }) => (
    <span {...props} className={classes.spacer}> {children} </span>
));

export default Spacer;