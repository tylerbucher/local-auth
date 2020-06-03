import React from "react";
import {
    BrowserRouter as Router,
    Switch,
    Route,
} from "react-router-dom";

import Login from "./Login";
import Dash from "./Dash";

import "./styles/global.less";

function App() {
    return (
        <Router>
            <div className="app">
                <Switch>
                    <Route exact path="/">
                        <Login/>
                    </Route>
                    <Route path="/login">
                        <Login />
                    </Route>
                    <Route path="/dash">
                        <Dash />
                    </Route>
                    {/*<Route path="/createUser">
                        <About />
                    </Route>
                    <Route path="/users">
                        <About />
                    </Route>
                    <Route path="/editUser">
                        <About />
                    </Route>*/}
                </Switch>
            </div>
        </Router>
    );
}

export default App;
