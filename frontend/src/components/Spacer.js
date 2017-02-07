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
    '@media (min-width: 1px) and (max-width: 360px)': {
        spacer: {
            fontSize: '16px'
        }
    },
    '@media (min-width: 361px) and (max-width: 479px)': {
        spacer: {
            fontSize: '20px'
        }
    },
    '@media (min-width: 480px)': {
        spacer: {
            fontSize: '28px'
        }
    }
};

const Spacer = injectSheet(styles)(({ children, classes, ...props }) => (
    <span {...props} className={classes.spacer}>{children}</span>
));

export default Spacer;