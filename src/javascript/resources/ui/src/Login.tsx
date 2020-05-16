import React from "react";
import axios from "axios";
import Metro from "metro4-react";

import "./styles/global.less";

type LoginProps = {
    // using `interface` is also ok
};
type LoginState = {
    username: string;
    password: string;
};

class Login extends React.Component<LoginProps, LoginState> {

    constructor(props: LoginProps) {
        super(props);
        this.state = {
            username: "",
            password: ""
        };

        this.loginAjax = this.loginAjax.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
    }


    loginAjax(e: Event) {
        e.preventDefault();
        axios({
            method: 'POST',
            url: './api/v1/login',
            responseType: 'json',
            data: this.state,
        }).catch(function (error) {
            Metro.dialog.create({
                title: "Use Windows location service?",
                content: "<div>Bassus abactors ducunt ad triticum...</div>",
                closeButton: true
            });
        }).then(function (response) {

        });
    }

    handleUsernameChange(event: React.FormEvent<HTMLInputElement>) {
        this.setState({username: event.currentTarget.value});
    }

    handlePasswordChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({password: event.currentTarget.value});
        }
    }

    render() {
        return (
            <div className="login-box">
                <form className="bg-white p-4">
                    <img className="place-right" src="/rlg_favicon.png" width="100px" height="100px"/>
                    <h1 className="mb-0">Login</h1>
                    <div className="text-muted mb-4">Sign in to start your session</div>
                    <div className="form-group">
                        <div className="input">
                            <input id="username" type="text" placeholder="Username" value={this.state.username}
                                   onChange={this.handleUsernameChange}/>
                            <div className="append">
                                <span className="mif-user"/>
                            </div>
                        </div>
                    </div>
                    <div className="form-group">
                        <div className="input">
                            <input id="password" type="password" placeholder="Password" value={this.state.password}
                                   onChange={this.handlePasswordChange}/>
                            <div className="append">
                                <span className="mif-key"/>
                            </div>
                        </div>
                    </div>
                    <div className="form-group d-flex flex-align-center" style={{justifyContent: "right"}}>
                        <button className="button primary" type="submit">Sign In</button>
                    </div>
                </form>
            </div>
        );
    }
}

export default Login