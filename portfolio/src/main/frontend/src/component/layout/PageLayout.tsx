import React from "react"
import styles from "./PageLayout.module.css"
import {CssBaseline, ThemeProvider} from "@mui/material";
import theme from "../../theme";

export const PageLayout: React.FC = ({children}) => {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline/>
            {children}
        </ThemeProvider>
    )
}