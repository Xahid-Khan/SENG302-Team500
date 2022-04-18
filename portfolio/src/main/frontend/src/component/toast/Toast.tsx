import React from "react";
import styles from "./Toast.module.css"
import defaultStyles from "./DefaultToast.module.css"
import {ToastTheme} from "./ToastTheme";
import {getClassNameFromThemes} from "./internal/util";
import {ToastBase, ToastBaseProps} from "./ToastBase";

interface ToastProps extends Omit<ToastBaseProps, "themes"> {
    title: string
    subtitle?: string

    before?: React.ReactNode
    after?: React.ReactNode
    theme?: ToastTheme
}

export const Toast: React.FC<ToastProps> = ({title, subtitle, dismissable, onDismiss, theme = defaultStyles, before, after}) => {
    return (
        <ToastBase themes={[styles, theme]} dismissable={dismissable} onDismiss={onDismiss}>
            {(before) ? (
                <div className={getClassNameFromThemes("before", theme)}>{before}</div>
            ) : undefined}

            <div className={getClassNameFromThemes("body", theme)}>
                <div className={getClassNameFromThemes("title", theme)}>{title}</div>
                {(subtitle) ? (
                    <div className={getClassNameFromThemes("subtitle", theme)}>{subtitle}</div>
                ) : undefined}
            </div>

            {(after) ? (
                <div className={getClassNameFromThemes("after", theme)}>{after}</div>
            ) : undefined}
        </ToastBase>
    )
}