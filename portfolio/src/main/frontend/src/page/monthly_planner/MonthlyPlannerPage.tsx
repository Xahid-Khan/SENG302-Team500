import React, {useCallback, useState} from "react"
import styles from "./MonthlyPlannerPage.module.css"

export const MonthlyPlannerPage: React.FC = () => {
    const [state, setState] = useState(0)

    const onClick = useCallback(() => {
        setState(state + 1)
    }, [state])

    return (
        <div className={styles.monthlyPlannerPage}>
            <h1>Welcome to the Monthly Planner page!</h1>
            <p>So far you've clicked <button onClick={onClick}>this button</button> {state} times!</p>
        </div>
    )
}