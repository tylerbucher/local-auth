import React from "react";
// @ts-ignore
// @ts-ignore
import {Redirect} from "react-router";
import axios from "axios";
import {Link} from "react-router-dom";

type EditUsersProps = {
    // using `interface` is also ok
};
type EditUsersState = {
    redirect?: boolean;
    userData?: JSX.Element[];
};

class EditUsers extends React.Component<EditUsersProps, EditUsersState> {
    constructor(props: EditUsersProps) {
        super(props);

        this.state = {
            redirect: false,
            userData: new Array<JSX.Element>()
        };
        this.getUsers();
    }

    getUsers() {
        let dash = this;
        axios.get("/api/v1/users", {
            responseType: "json",
        }).then(function (response) {
            if (response.status === 200) {
                let data = new Array<JSX.Element>();
                response.data["api"]["usernameList"].forEach(function (value: String, index: number) {
                    data.push(
                        <li className="form-control"><Link to={"/editUser/?u=" + value}><span className="mif-user icon"/> {value}</Link></li>
                    );
                    if(index != response.data["api"]["usernameList"].length -1) {
                        data.push(<li className="divider"/>)
                    }
                });
                dash.setState({userData: data});
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
            if(this.state.userData != null && this.state.userData.length != 0) {
                return (
                    <div className="login-box" data-role="master" data-effect="fade">
                        <ul className="d-menu open pos-static bg-steel fg-white form-control">
                            {this.state.userData}
                        </ul>
                        <Link className="command-button warning outline mt-6" to="/management"><span
                            className="caption">Management</span></Link>
                    </div>
                );
            }
        }
        return <div/>;
    }
}

export default EditUsers;