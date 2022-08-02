/**
 * Entrypoint for the JS on the groups page
 */

import React from "react"
import ReactDOM from "react-dom"
import {GroupPage} from "../page/group_page/GroupPage"
import "./monthly_planner.css"

ReactDOM.render(
    <React.StrictMode>
        <GroupPage/>
    </React.StrictMode>,
    document.getElementById("react-root")
)