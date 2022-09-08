/**
 * Entrypoint for the JavaScript on the home_feed page.
 */
import ReactDOM from "react-dom";
import React from "react";
import {HomeFeedPage} from "../page/feed/home_feed/HomeFeedPage";

ReactDOM.render(
    <React.StrictMode>
        <HomeFeedPage/>
    </React.StrictMode>,
    document.getElementById("react-root")
)