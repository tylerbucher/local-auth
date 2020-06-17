import React from "react";
// @ts-ignore
// @ts-ignore

type DashProps = {
    // using `interface` is also ok
};
type DashState = {};

class FourOOneError extends React.Component<DashProps, DashState> {
    constructor(props: DashProps) {
        super(props);

        this.state = {};
    }

    render() {
        console.log(window.location.href);
        return <div/>;
    }
}

export default FourOOneError;