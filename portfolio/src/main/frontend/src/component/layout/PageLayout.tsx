import React from "react"
import styles from "./PageLayout.module.css"

export const PageLayout: React.FC = ({children}) => {
    return (
        <div className={styles.pageLayout}>
            <main>
                {children}
            </main>
        </div>
    )
}