import React from "react"
import ReactDOM from "react-dom"
import {MonthlyPlannerPage} from "../page/monthly_planner/MonthlyPlannerPage";
import "./globals"

ReactDOM.render(
    <React.StrictMode>
        <MonthlyPlannerPage/>
    </React.StrictMode>,
    document.getElementById("react-root")
)