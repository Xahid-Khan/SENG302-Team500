import React from "react"
import styles from "./MonthlyPlannerPage.module.css"
import {PageLayout} from "../../component/layout/PageLayout"
import FullCalendar from "@fullcalendar/react"
import dayGridPlugin from "@fullcalendar/daygrid"

export const MonthlyPlannerPage: React.FC = () => {
    return (
        <PageLayout>
            <div className={styles.monthlyPlannerPage}>
                <h1>Welcome to the Monthly Planner page!</h1>
                <div className="raised-card" style={{padding: 20}}>
                    <FullCalendar
                        plugins={[ dayGridPlugin ]}
                        initialView="dayGridMonth"
                        events={[
                            {
                                start: (() => {
                                    const date = new Date()
                                    date.setHours(0)
                                    date.setMinutes(0)
                                    date.setSeconds(0)
                                    date.setMilliseconds(0)
                                    return date
                                })(),
                                end: Date.parse("2023-01-01T00:00:00.00Z"),
                                backgroundColor: "orange",
                                title: "Test Event"
                            }
                        ]}
                    />
                </div>
            </div>
        </PageLayout>
    )
}