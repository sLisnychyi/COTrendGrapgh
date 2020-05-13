import React from 'react';
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";

const Header = (props) => {
    return <AppBar className='header' position={"static"}>
        <Toolbar>
            <Typography variant="h6">
                COTrendGrapgh
            </Typography>
        </Toolbar>
    </AppBar>
};

export default Header;