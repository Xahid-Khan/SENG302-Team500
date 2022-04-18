import {ReactNode} from "react";
import {action, makeObservable, observable} from "mobx";

interface ToastData {
    component: () => ReactNode,
    timeout: number,
    created: Date,
    id: number
}

interface ToasterStoreShowProps {
    timeout: number
}
const DEFAULT_TOASTER_STORE_SHOW_PROPS = {
    timeout: 1000
}

/**
 * Stores and manages active 'toasts' (notifications / status updates for the user).
 */
export class ToasterStore {
    toasts: ToastData[]
    toastsById: Map<number, ToastData>

    protected nextId: number = 0
    protected generateId() {
        return this.nextId++
    }

    constructor() {
        makeObservable(this, {
            toasts: observable,
            toastsById: observable,

            show: action,
            dismiss: action
        })

        this.toasts = observable.array()
        this.toastsById = observable.map()
    }

    public dismiss(id: number): void {
        this.toastsById.delete(id)
        const toastIndex = this.toasts.findIndex(toast => toast.id === id)
        if (toastIndex !== -1) {
            this.toasts.splice(toastIndex, 1)
        }
    }

    protected timeoutDismissToast(toastId: number) {
        const toast = this.toastsById.get(toastId)
        if (toast !== null && new Date() >= new Date(toast.created.getTime() + toast.timeout)) {
            this.dismiss(toastId)
        }
    }

    public replace(id: number, component: () => ReactNode, props: ToasterStoreShowProps = DEFAULT_TOASTER_STORE_SHOW_PROPS): void {
        const toastIndex = this.toasts.findIndex(toast => toast.id === id)
        if (toastIndex === -1 || !this.toastsById.has(id)) {
            throw new Error("Cannot replace a toast that does not exist.")
        }

        const newToast: ToastData = {
            id: id,
            component,
            timeout: props.timeout,
            created: new Date()
        }

        this.toasts.splice(toastIndex, 1, newToast)
        this.toastsById.set(id, newToast)

        if (props.timeout !== Infinity) {
            setTimeout(() => this.timeoutDismissToast(id), props.timeout)
        }
    }

    public show(component: () => ReactNode, props: ToasterStoreShowProps = DEFAULT_TOASTER_STORE_SHOW_PROPS): number {
        const toast = {
            id: this.generateId(),
            component,
            timeout: props.timeout,
            created: new Date()
        }

        this.toasts.push(toast)
        this.toastsById.set(toast.id, toast)

        if (props.timeout !== Infinity) {
            setTimeout(() => this.timeoutDismissToast(toast.id), props.timeout)
        }

        return toast.id
    }
}