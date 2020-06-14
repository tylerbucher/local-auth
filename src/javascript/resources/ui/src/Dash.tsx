import React from "react";
// @ts-ignore
// @ts-ignore
import {Redirect} from "react-router";
import axios from "axios";
import {Link} from "react-router-dom";

type DashProps = {
    // using `interface` is also ok
};
type DashState = {
    items?: null | Array<any>;
    redirect?: boolean;
};

class Dash extends React.Component<DashProps, DashState> {
    constructor(props: DashProps) {
        super(props);

        this.state = {
            items: null,
            redirect: false
        };
        this.getDashContent();
    }

    getDashContent() {
        let dash = this;
        axios.get("/api/v1/dash", {
            responseType: "json",
        }).then(function (response) {
            if (response.status !== 200) {
                dash.setState({redirect: true});
            }
            console.log(response.data.api.endpoints);
            let arr = new Array<JSON>();
            response.data.api.endpoints.forEach(function (item: string) {
               arr.push(JSON.parse(item));
            });
            dash.setState({items: arr});
        }).catch(function (response) {
            dash.setState({redirect: true});
        });
    }

    getDashItems(): Array<JSX.Element> {
        let buttons = new Array<JSX.Element>();
        if (this.state.items != null) {
            this.state.items.forEach(function (item) {
                buttons.push(
                    <a className={item["cssClasses"]} href={item["link"]}>
                        <span className="caption">{item["displayText"]}</span>
                    </a>
                );
            });
        }
        return buttons;
    }

    render() {
        if (this.state.redirect) {
            return <Redirect push to="/login"/>;
        } else {
            if (this.state.items != null) {
                return (
                    <div className="login-box-extended" data-role="master" data-effect="fade">
                        {this.getDashItems()}
                        <Link className="command-button primary" to="/management"><span className="caption">App Management</span></Link>
                    </div>
                );
            } else {
                return <div/>;
            }
        }
    }
}

export default Dash;