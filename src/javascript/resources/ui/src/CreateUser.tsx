import React, {FormEvent} from "react";
import axios from "axios";
// @ts-ignore
import {Button} from "metro4-react";
// @ts-ignore
import {Notific8} from 'notific8';
import "../node_modules/notific8/src/sass/notific8.scss";
import {Redirect} from "react-router";

import "./styles/darcula.less";
import {Link} from "react-router-dom";

type CreateUserProps = {
    // using `interface` is also ok
};
type CreateUserState = {
    username?: string;
    password?: string;
    rePassword?: string;
    redirect?: boolean;
    isAdmin?: boolean;
};

class CreateUser extends React.Component<CreateUserProps, CreateUserState> {
    constructor(props: CreateUserProps) {
        super(props);

        this.state = {
            username: "",
            password: "",
            rePassword: "",
            redirect: false,
            isAdmin: false
        };

        this.isAdmin(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleRePasswordChange = this.handleRePasswordChange.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleUsernameChange(event: React.FormEvent<HTMLInputElement>) {
        this.setState({username: event.currentTarget.value});
    }

    handlePasswordChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({password: event.currentTarget.value});
        }
    }

    handleRePasswordChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({rePassword: event.currentTarget.value});
        }
    }

    async handleSubmit(e: FormEvent) {
        e.preventDefault();

        await this.createUser();
    }

    isAdmin(comp: CreateUser) {
        axios.get("/api/v1/adminStatus", {
            responseType: "json",
        }).then(function (response) {
            if (response.status === 200) {
                comp.setState({isAdmin: true});
            } else {
                comp.setState({redirect: true});
            }
        }).catch(function () {
            comp.setState({redirect: true});
        });
    }

    async createUser() {
        let message = "Passwords do not match";
        let color = "ruby";
        if (this.state.password === this.state.rePassword) {
            try {
                const response = await axios.post("/api/v1/createUser", {
                    username: this.state.username,
                    password: this.state.password
                }, {
                    responseType: "json",
                });
                if (response.status === 200) {
                    message = "User create successfully";
                    color = "lime";
                    this.setState({username: "", password: "", rePassword: ""})
                } else {
                    message = "Error creating user";
                }
            } catch (e) {
                if (e.message.endsWith("409")) {
                    message = "A user with that username already exists"
                } else {
                    message = "Invalid request"
                }
            }
        }
        // @ts-ignore
        Notific8.create(message, {themeColor: color, life: 4000}).then((notification) => {
            // open the notification
            notification.open();
        });
    }

    render() {
        if (this.state.redirect) {
            return <Redirect push to="/dash"/>;
        } else {
            if (this.state.isAdmin) {
                return (
                    <div className="login-box">
                        <form id="newUserForm" className="p-4" onSubmit={(e) => this.handleSubmit(e)}>
                            <h1 className="mb-0">Create User</h1>
                            <div className="text-muted mb-4">Fill out data to create a new user.</div>
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
                            <div className="form-group">
                                <div className="input">
                                    <input
                                        id="rePassword"
                                        type="password"
                                        placeholder="Re-Password"
                                        value={this.state.rePassword}
                                        onChange={this.handleRePasswordChange}
                                    />
                                    <div className="append darcula-append">
                                        <span className="mif-key"/>
                                    </div>
                                </div>
                            </div>
                            <div className="form-group d-flex flex-align-center">
                                <Link className="button warning outline form-control mr-3" to="/management"><span
                                    className="caption">Management</span></Link>
                                <Button cls="success form-control ml-3" title="Create user" type="submit"/>
                            </div>
                        </form>
                    </div>
                );
            } else {
                return <div/>;
            }
        }
    }
}

export default CreateUser;
