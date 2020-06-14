import React from "react";
// @ts-ignore
// @ts-ignore
import {Redirect} from "react-router";
import axios from "axios";
import {Link} from "react-router-dom";

type ManagementProps = {
    // using `interface` is also ok
};
type ManagementState = {
    redirect?: boolean;
    authenticated?: boolean;
};

class Management extends React.Component<ManagementProps, ManagementState> {
    constructor(props: ManagementProps) {
        super(props);

        this.state = {
            redirect: false,
            authenticated: false
        };
        this.isAuthenticated();
    }

    isAuthenticated() {
        let dash = this;
        axios.get("/api/v1/adminStatus", {
            responseType: "json",
        }).then(function (response) {
            if (response.status === 200) {
                dash.setState({authenticated: true});
            } else {
                dash.setState({redirect: true});
            }
        }).catch(function () {
            dash.setState({redirect: true});
        });
    }

    render() {
        if (this.state.redirect) {
            return <Redirect push to="/login"/>;
        } else {
            if(this.state.authenticated) {
                return (
                    <div className="login-box" data-role="master" data-effect="fade">
                        <Link className="command-button success outline mb-6" to="/createUser"><span className="caption">Create User</span></Link>
                        <Link className="command-button info outline mb-6" to="/editUsers"><span className="caption">Edit User</span></Link>
                        <Link className="command-button warning outline" to="/dash"><span
                            className="caption">Dashboard</span></Link>
                    </div>
                );
            }
        }
        return <div/>;
    }
}

export default Management;