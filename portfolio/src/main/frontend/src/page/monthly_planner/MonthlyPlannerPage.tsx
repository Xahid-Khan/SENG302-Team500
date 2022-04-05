import React, {useCallback, useState} from "react"
import styles from "./MonthlyPlannerPage.module.css"
import {PageLayout} from "../../component/layout/PageLayout";
import {Box, Button, Typography} from "@mui/material";

export const MonthlyPlannerPage: React.FC = () => {
    const [state, setState] = useState(0)

    const onClick = useCallback(() => {
        setState(state + 1)
    }, [state])

    return (
        <PageLayout>
            <Box className={styles.monthlyPlannerPage}>
                <Typography variant='h1'>Welcome to the Monthly Planner page!</Typography>
                <Typography variant='body1'>So far you've clicked <Button onClick={onClick}>this button</Button> {state} times!</Typography>
            </Box>
        </PageLayout>
    )
}