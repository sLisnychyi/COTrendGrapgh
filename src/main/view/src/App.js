import React from 'react';
import './App.css';
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import Charts from "./component/Charts";

function App() {
    return (
        <div className="App">
            <AppBar position={"static"}>
                <Toolbar>
                    <Typography variant="h6">
                        COTrendGrapgh
                    </Typography>
                </Toolbar>
            </AppBar>
            <Charts/>
        </div>
    );
}

export default App;
