import React from "react";
import {
    BrowserRouter as Router,
    Switch,
    Route,
} from "react-router-dom";

import Login from "./Login";
import Dash from "./Dash";

import "./styles/global.less";
import "./styles/darcula.less";
import Management from "./Management";
import EditUsers from "./EditUsers";
import CreateUser from "./CreateUser";
import EditUser from "./EditUser";

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
                    <Route path="/management">
                        <Management />
                    </Route>
                    <Route path="/editUsers">
                        <EditUsers />
                    </Route>
                    <Route path="/createUser">
                        <CreateUser />
                    </Route>
                    <Route path="/editUser">
                        <EditUser />
                    </Route>
                </Switch>
            </div>
        </Router>
    );
}

export default App;
