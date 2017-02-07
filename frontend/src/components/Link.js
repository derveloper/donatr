//noinspection ES6UnusedImports
import Inferno from "inferno";
import injectSheet from 'react-jss';
import {Link as RouterLink} from "inferno-router";

const styles = {
    link: {
        color: '#00ff00',
        textDecoration: 'none',
        fontSize: '28px',
        '&:active': {
            backgroundColor: 'rgba(0,255,0,0.4)'
        }
    },
    '@media (min-width: 1px) and (max-width: 360px)': {
        link: {
            fontSize: '16px'
        }
    },
    '@media (min-width: 361px) and (max-width: 479px)': {
        link: {
            fontSize: '20px'
        }
    },
    '@media (min-width: 480px)': {
        link: {
            fontSize: '28px'
        }
    }
};

const Link = injectSheet(styles)(({ children, classes, className, ...props }) => (
    <RouterLink {...props} className={`${classes.link} ${className}`}>{children}</RouterLink>
));

export default Link;