/**
 * Entrypoint for the JavaScript on the notifications page.
 */
import React from "react"
import ReactDOM from "react-dom"
import "./monthly_planner.css"
import {NotificationsPage} from "../page/notifications/NotificationPage";

ReactDOM.render(
    <React.StrictMode>
        <NotificationsPage/>
    </React.StrictMode>,
    document.getElementById("react-root")
)