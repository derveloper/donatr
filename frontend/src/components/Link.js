//noinspection ES6UnusedImports
import Inferno from "inferno";
import injectSheet from 'react-jss';
import {Link as RouterLink} from "inferno-router";

const styles = {
    link: {
        color: '#00ff00',
        textDecoration: 'none',
        fontSize: '28px'
    },
    '@media (max-width: 479px)': {
        link: {
            fontSize: '20px'
        }
    }
};

const Link = injectSheet(styles)(({ children, classes, className, ...props }) => (
    <RouterLink {...props} className={`${classes.link} ${className}`}>{children}</RouterLink>
));

export default Link;