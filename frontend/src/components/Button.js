//noinspection ES6UnusedImports
import Inferno from "inferno";
import injectSheet from 'react-jss';

const styles = {
    button: {
        backgroundColor: '#000',
        border: 'none',
        color: '#00ff00',
        fontSize: '28px',
        cursor: 'pointer',
        '&:active': {
            backgroundColor: 'rgba(0,255,0,0.4)'
        }
    },
    '@media (min-width: 1px) and (max-width: 360px)': {
        button: {
            fontSize: '16px'
        }
    },
    '@media (min-width: 361px) and (max-width: 479px)': {
        button: {
            fontSize: '20px'
        }
    },
    '@media (min-width: 480px)': {
        button: {
            fontSize: '28px'
        }
    }
};

const Button = injectSheet(styles)(({ children, classes, className, ...props }) => (
    <button {...props} className={`${classes.button} ${className}`}>{children}</button>
));

export default Button;