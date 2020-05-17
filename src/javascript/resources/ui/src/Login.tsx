import React, { FormEvent } from "react";
import axios from "axios";

// @ts-ignore
import { Button, Dialog } from "metro4-react";

import "./styles/global.less";

type LoginProps = {
    // using `interface` is also ok
};
type LoginState = {
    dialogOpen: boolean;
    username?: string;
    password?: string;
};

class Login extends React.Component<LoginProps, LoginState> {
    constructor(props: LoginProps) {
        super(props);

        this.state = {
            dialogOpen: false,
            username: "",
            password: "",
        };

        this.toggleDialog = this.toggleDialog.bind(this);

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    toggleDialog() {
        this.setState(state => ({
            dialogOpen: !state.dialogOpen,
        }));
    }

    handleUsernameChange(event: React.FormEvent<HTMLInputElement>) {
        this.setState({ username: event.currentTarget.value });
    }

    handlePasswordChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({ password: event.currentTarget.value });
        }
    }

    async handleSubmit(e: FormEvent) {
        e.preventDefault();

        await this.login();
    }

    async login() {
        try {
            const response = await axios.post("/api/v1/login", this.state, {
                responseType: "json",
            });

            if (response.status === 200) {
                // todo
            } else {
                this.toggleDialog();
            }
        } catch (e) {
            this.toggleDialog();
        }
    }

    render() {
        return (
            <div className="login-box">
                {this.state.dialogOpen && (
                    <Dialog
                        open={this.state.dialogOpen}
                        title={"Error"}
                        onClose={this.toggleDialog}
                    >
                        <div>Placeholder</div>
                    </Dialog>
                )}
                <form className="bg-white p-4" onSubmit={(e) => this.handleSubmit(e)}>
                    <img
                        className="place-right"
                        src="/rlg_favicon.png"
                        width="100px"
                        height="100px"
                    />
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
                            <div className="append">
                                <span className="mif-user" />
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
                            <div className="append">
                                <span className="mif-key" />
                            </div>
                        </div>
                    </div>
                    <div
                        className="form-group d-flex flex-align-center"
                        style={{ justifyContent: "right" }}
                    >
                        <Button cls="primary" title="Sign In" type="submit" />
                    </div>
                </form>
            </div>
        );
    }
}

export default Login;
