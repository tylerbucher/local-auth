class Login extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            username: "",
            password: ""
        };

        this.loginAjax = this.loginAjax.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
    }


    loginAjax(e) {
        e.preventDefault();
        console.log(this.state.username);
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
            let params = (new URL(document.location)).searchParams;
            let rDirect = params.get("r");
            if (rDirect !== null) {
                window.location.replace(rDirect);
            } else {
                window.history.pushState({}, document.title, "./dash");
                reloadPage();
            }
        });
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    render() {
        return [
            element('div', {className: "login-box"},
                element('form', {className: "bg-white p-4", onSubmit: this.loginAjax}, [
                    element('img', {
                        className: "place-right mt-4-minus mr-6-minus",
                        src: "https://metroui.org.ua/themes/pandora/images/p-120x120.png"
                    }),
                    element('h1', {className: "mb-0"}, 'Login'),
                    element('div', {className: "text-muted mb-4"}, "Sign in to start your session"),
                    element('div', {className: "form-group"},
                        element('div', {className: "input"}, [
                            element('input', {
                                id: "username",
                                type: "text",
                                placeholder: "Username",
                                value: this.state.username,
                                onChange: this.handleUsernameChange
                            }),
                            element('div', {className: "append"},
                                element('span', {className: "mif-user"})
                            )
                        ])
                    ),
                    element('div', {className: "form-group"},
                        element('div', {className: "input"}, [
                            element('input', {
                                id: "password",
                                type: "password",
                                placeholder: "Password",
                                value: this.state.password,
                                onChange: this.handlePasswordChange
                            }),
                            element('div', {className: "append"},
                                element('span', {className: "mif-key"})
                            )
                        ])
                    ),
                    element('div', {className: "form-group d-flex flex-align-center", style: {justifyContent: "right"}},
                        element('button', {
                            className: "button primary",
                            type: "submit"
                        }, "Sign In")
                    )
                ])
            )
        ];
    }
}

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return [
            "<div></div>"
        ];
    }
}