import React, {useCallback} from "react";
import {useToasterStore} from "./internal/ToasterStoreProvider";
import {useToast} from "./internal/ToastContextProvider";
import {mergeClassNames} from "../../util/style_util";
import {getClassNameFromThemes} from "./internal/util";
import styles from "./ToastBase.module.css";
import {ToastTheme} from "./ToastTheme";

export interface ToastBaseProps {
    dismissable?: boolean
    onDismiss?: VoidFunction
    themes: ToastTheme[]
}
export const ToastBase: React.FC<ToastBaseProps> = ({dismissable = true, onDismiss, themes, children}) => {
    const toaster = useToasterStore()
    const toast = useToast()

    const dismissToast = useCallback(() => {
        toaster.dismiss(toast.id)
        if (onDismiss !== undefined) {
            onDismiss()
        }
    }, [toaster, toast, onDismiss])

    return (
        <div
            className={mergeClassNames(getClassNameFromThemes("toast", ...themes), (dismissable) ? styles.dismissable : undefined)}
            onClick={(dismissable) ? dismissToast : undefined}
        >
            {children}
        </div>
    )
}