import React, {FormEvent} from "react";
import axios from "axios";
// @ts-ignore
import {Button} from "metro4-react";
// @ts-ignore
import {Notific8} from 'notific8';
import "../node_modules/notific8/src/sass/notific8.scss";
import {Redirect} from "react-router";

type LoginProps = {
    // using `interface` is also ok
};
type LoginState = {
    username?: string;
    password?: string;
    redirect?: boolean;
    authenticated?: boolean;
};

class Login extends React.Component<LoginProps, LoginState> {
    constructor(props: LoginProps) {
        super(props);

        this.state = {
            username: "",
            password: "",
            redirect: false,
            authenticated: true
        };

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.isAuthenticated(this);
    }

    handleUsernameChange(event: React.FormEvent<HTMLInputElement>) {
        this.setState({username: event.currentTarget.value});
    }

    handlePasswordChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({password: event.currentTarget.value});
        }
    }

    isAuthenticated(comp: Login) {
        axios.get("/api/v1/tokenValidity", {
            responseType: "json",
        }).then(function (response) {
            if (response.status === 200) {
                comp.setState({redirect: true});
            } else {
                comp.setState({authenticated: false});
            }
        }).catch(function (response) {
            comp.setState({authenticated: false});
        });
    }

    async handleSubmit(e: FormEvent) {
        e.preventDefault();

        await this.login();
    }

    async login() {
        try {
            const response = await axios.post("/api/v1/login", {
                username: this.state.username,
                password: this.state.password
            }, {
                responseType: "json",
            });
            if (response.status === 200) {
                this.setState({redirect: true});
            }
        } catch (e) {
            let message = "";
            if (e.message.endsWith("409")) {
                message = "Invalid username or password"
            } else {
                message = "Invalid request"
            }
            // @ts-ignore
            Notific8.create(message, {themeColor: 'ruby', life: 4000}).then((notification) => {
                // open the notification
                notification.open();
            });
        }
    }

    render() {
        if (this.state.redirect) {
            return <Redirect push to="/dash"/>;
        } else {
            if (!this.state.authenticated) {
                return (
                    <div className="login-box">
                        <form className="p-4" onSubmit={(e) => this.handleSubmit(e)}>
                            <h1 className="mb-0">Login</h1>
                            <div className="text-muted mb-4">Sign in to start your session</div>
                            <div className="form-group">
                                <div className="input">
                                    <input
                                        id="username"
                                        type="text"
                                        placeholder="Username"
                                        value={this.state.username}
                                        onChange={this.handleUsernameChange}
                                    />
                                    <div className="append darcula-append">
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
                                        onChange={this.handlePasswordChange}
                                    />
                                    <div className="append darcula-append">
                                        <span className="mif-key"/>
                                    </div>
                                </div>
                            </div>
                            <div className="form-group d-flex flex-align-center">
                                <Button cls="success form-control" title="Sign In" type="submit"/>
                            </div>
                        </form>
                    </div>
                );
            } else {
                return <div/>
            }
        }
    }
}

export default Login;
