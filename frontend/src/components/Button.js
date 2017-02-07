//noinspection ES6UnusedImports
import Inferno from "inferno";
import injectSheet from 'react-jss';

const styles = {
    button: {
        backgroundColor: '#000',
        border: 'none',
        color: '#00ff00',
        fontSize: '28px',
        cursor: 'pointer'
    },
    '@media (max-width: 479px)': {
        button: {
            fontSize: '20px'
        }
    }
};

const Button = injectSheet(styles)(({ children, classes, className, ...props }) => (
    <button {...props} className={`${classes.button} ${className}`}>{children}</button>
));

export default Button;