import React, {FormEvent} from "react";
import axios from "axios";
// @ts-ignore
import {Button} from "metro4-react";
// @ts-ignore
import {Redirect} from "react-router";

type DashProps = {
    // using `interface` is also ok
};
type DashState = {
    username?: string;
    password?: string;
    redirect?: boolean;
};

class Dash extends React.Component<DashProps, DashState> {
    constructor(props: DashProps) {
        super(props);

        this.state = {
            username: "",
            password: "",
            redirect: false
        };
    }

    render() {
        if (this.state.redirect) {
            return <Redirect push to="/dash"/>;
        } else {
            return (
                <div className="login-box">
                    <form className="bg-white p-4">
                        <img
                            className="place-right"
                            src="/assets/images/rlg_favicon.png"
                            width="100px"
                            height="100px"
                        />
                        <h1 className="mb-0">DASH</h1>
                        <div className="text-muted mb-4">Sign in to start your session</div>
                        <div className="form-group">
                            <div className="input">
                                <input
                                    id="username"
                                    type="text"
                                    placeholder="Username"
                                    value={this.state.username}
                                />
                                <div className="append">
                                    <span className="mif-user"/>
                                </div>
                            </div>
                        </div>
                        <div className="form-group">
                            <div className="input">
                                <input
                                    id="password"
                                    type="password"
                                    placeholder="Password"
                                    value={this.state.password}
                                />
                                <div className="append">
                                    <span className="mif-key"/>
                                </div>
                            </div>
                        </div>
                        <div
                            className="form-group d-flex flex-align-center"
                            style={{justifyContent: "right"}}
                        >
                            <Button cls="primary" title="Sign In" type="submit"/>
                        </div>
                    </form>
                </div>
            );
        }
    }
}

export default Dash;