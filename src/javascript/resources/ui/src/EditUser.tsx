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

type EditUserProps = {
    // using `interface` is also ok
};
type EditUserState = {
    username?: string;
    setAdmin?: boolean;
    setActive?: boolean;
    redirect?: boolean;
    isAdmin?: boolean;
};

class EditUser extends React.Component<EditUserProps, EditUserState> {
    constructor(props: EditUserProps) {
        super(props);

        this.state = {
            username: "",
            setAdmin: false,
            setActive: false,
            redirect: !this.getUser(this),
            isAdmin: false
        };

        this.handleAdminChange = this.handleAdminChange.bind(this);
        this.handleActiveChange = this.handleActiveChange.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleAdminChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({setAdmin: event.currentTarget.checked});
        }
    }

    handleActiveChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({setActive: event.currentTarget.checked});
        }
    }

    async handleSubmit(e: FormEvent) {
        e.preventDefault();

        await this.updateUser();
    }

    getUser(comp: EditUser): boolean {
        let params = new URLSearchParams(window.location.search);
        if (params != null) {
            let username = params.get("u");
            if (username != null && username != "") {
                axios.get("/api/v1/user/" + username, {
                    responseType: "json",
                }).then(function (response) {
                    if (response.status === 200) {
                        comp.setState({
                            isAdmin: true,
                            username: response.data["api"]["username"],
                            setAdmin: response.data["api"]["admin"],
                            setActive: response.data["api"]["active"]
                        });
                    } else {
                        comp.setState({redirect: true});
                    }
                }).catch(function () {
                    console.log("asd");
                    comp.setState({redirect: true});
                });
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    async updateUser() {
        let message = "Invalid request";
        let color = "ruby";
        try {
            const response = await axios.patch("/api/v1/editUser", {
                updateUsername: this.state.username,
                admin: this.state.setAdmin,
                active: this.state.setActive
            }, {
                responseType: "json",
            });
            if (response.status === 200) {
                message = "User updated successfully";
                color = "lime";
            } else {
                message = "Error updating user";
            }
        } catch (e) {
            if (e.message.endsWith("500")) {
                message = "Internal server error"
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
            return <Redirect push to="/editUsers"/>;
        } else {
            if (this.state.isAdmin) {
                return (
                    <div className="login-box">
                        <form id="newUserForm" className="p-4" onSubmit={(e) => this.handleSubmit(e)}>
                            <h1 className="mb-0">Edit User</h1>
                            <div className="text-muted mb-4">Edit select user information.</div>
                            <div className="form-group">
                                <div className="input disabled">
                                    <input
                                        id="username"
                                        type="text"
                                        placeholder="Username"
                                        value={this.state.username}
                                    />
                                    <div className="append darcula-append">
                                        <span className="mif-user"/>
                                    </div>
                                </div>
                            </div>
                            <div className="form-group d-flex flex-justify-between">
                                <label className="switch transition-on"><input type="checkbox" data-role="switch"
                                                                               data-caption="Switch"
                                                                               data-role-switch="true"
                                                                               checked={this.state.setActive}
                                                                               onChange={this.handleActiveChange}/>
                                    <span className="check"/>
                                    <span className="caption">Is User Active</span>
                                </label>
                                <label className="switch transition-on"><input type="checkbox" data-role="switch"
                                                                               data-caption="Switch"
                                                                               data-role-switch="true"
                                                                               checked={this.state.setAdmin}
                                                                               onChange={this.handleAdminChange}/>
                                    <span className="check"/>
                                    <span className="caption">Is User Admin</span>
                                </label>
                            </div>
                            <div className="form-group d-flex flex-align-center">
                                <Link className="button warning outline form-control mr-3" to="/editUsers"><span
                                    className="caption">Users</span></Link>
                                <Button cls="success form-control ml-3" title="Modify User" type="submit"/>
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

export default EditUser;
