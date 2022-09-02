import React from "react";
import {ToasterRoot} from "../../component/toast/ToasterRoot";
import {PageLayout} from "../../component/layout/PageLayout";
import styles from "../monthly_planner/MonthlyPlannerPage.module.css";
import {NotificationList} from "../monthly_planner/component/NotificationList";

/**
 * The root of the MonthlyPlannerPage. This does a few jobs:
 * 1. Construct the MonthlyPlannerPageStore and wrap the whole page in a Provider so it can be used by children.
 * 2. Wrap the entire page in a PageLayout.
 * 3. Place the PageContent component inside that Layout component.
 */
export const NotificationsPage: React.FC = () => {
    return (
        <ToasterRoot>
            <PageLayout>
                <div className={styles.monthlyPlannerPage}>
                <h1>Welcome to the Notifications page!</h1>

                    <div className="raised-card" style={{padding: 20}}>
                        <NotificationList/>
                    </div>
                </div>
            </PageLayout>
        </ToasterRoot>
    )
}