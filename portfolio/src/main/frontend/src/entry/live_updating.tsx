import * as polyfills from "../util/polyfill/socket_polyfill";
import React from "react";
import {action, computed, makeObservable, observable, runInAction} from "mobx";
import {
    LoadingDone,
    LoadingError,
    LoadingNotYetAttempted,
    LoadingPending,
    LoadingStatus
} from "../util/network/loading_status";
import SockJS from "sockjs-client";
import {Client as StompClient, Message as StompMessage} from "@stomp/stompjs";

/**
 * MobX-enabled store for the Ping/Socket Test page.
 *
 * This serves as a basic working model of how to use WebSockets with Spring Boot.
 */
class PingPageStore {
    readonly stomp: StompClient

    connectStatus: LoadingStatus = new LoadingNotYetAttempted()

    pongArray: string[] = observable.array()
    nextPingValue: string = ""

    constructor() {
        makeObservable(this, {
            connectStatus: observable,
            pongArray: observable,
            nextPingValue: observable,

            connected: computed,

            setNextPingValue: action,
            showEdit: action,
            start: action
        })

        this.stomp = new StompClient({
            webSocketFactory: () => new SockJS("/socket"),
            connectionTimeout: 10000,
            debug: (msg) => console.log(new Date(), msg)
        })

        console.log("Created new PingPageStore.")
    }

    get connected(): boolean {
        return this.connectStatus instanceof LoadingDone
    }

    setNextPingValue(newValue: string) {
        this.nextPingValue = newValue
    }

    showEdit(location: string) {
        console.log("Attempting to send ping...")
        if (this.connected) {
            this.stomp.publish({
                destination: "/app/show",
                body: location
            })
        }
    }

    cancelEdit(location: string) {
        console.log("Attempting to send ping...")
        if (this.connected) {
            this.stomp.publish({
                destination: "/app/cancel",
                body: location
            })
        }
    }

    saveEdit(location: string) {
        console.log("Attempting to send ping...")
        if (this.connected) {
            this.stomp.publish({
                destination: "/app/save",
                body: location
            })
        }
    }

    async start() {
        await new Promise<void>((res) => {
            console.log("res :", res)
            this.connect(res)
        })
        if (!this.connected) {
            throw new Error("Connect resolved but we aren't connected yet!")
        }
    }

    protected connect(onConnected: VoidFunction): void {
        if (this.connectStatus instanceof LoadingPending || this.connectStatus instanceof LoadingDone) {
            console.warn("Cannot connect twice.")
            onConnected()
            return
        }

        this.stomp.beforeConnect = () => {
            runInAction(() => {
                this.connectStatus = new LoadingPending()
            })
        }
        this.stomp.onConnect = (frame: StompMessage) => {
            // Connected
            console.log("Connected")
            runInAction(() => {
                this.connectStatus = new LoadingDone()
                this.subscribe()
            })

            onConnected()
        }
        this.stomp.onStompError = (err: StompMessage) => {
            // Error
            console.log("Error!")
            runInAction(() => {
                this.connectStatus = new LoadingError(err)
            })
        }
        this.stomp.onWebSocketError = (evt) => {
            // Error
            console.log("Error!")
            runInAction(() => {
                this.connectStatus = new LoadingError(evt)
            })
        }
        this.stomp.onWebSocketClose = (evt) => {
            console.log("Socket closed!")
            runInAction(() => {
                this.connectStatus = new LoadingError(evt)
            })
        }
        console.log("stomp :", this.stomp)
        this.stomp.activate()
    }

    protected onReceivePong(frame: StompMessage) {
        runInAction(() => {
            this.pongArray.push(frame.body)
            const locAndName = frame.body.split("~");
            console.log(frame)
            if (locAndName[0] === "show") {
                if (document.getElementById("event-form-" + locAndName[1])) {
                    document.getElementById("event-form-" + locAndName[1]).innerText = locAndName[2] + " is currently editing"
                } else {
                    document.getElementById("editing-form-" + locAndName[1]).innerText = locAndName[2] + " is currently editing"
                }
            } else {
                if (locAndName[0] === "save") {
                    window.location.reload();
                }
                if (document.getElementById("event-form-" + locAndName[1])) {
                    document.getElementById("event-form-" + locAndName[1]).innerText = "";
                } else {
                    document.getElementById("editing-form-" + locAndName[1]).innerText = "";
                }
            }
        })
    }

    protected subscribe(): void {
        console.log("Subscribing...")
        this.stomp.subscribe("/topic/pongs", (frame: StompMessage) => {
            this.onReceivePong(frame)
        })
    }
}

export class Socket {
    private static store: PingPageStore = new PingPageStore();

    static start() {
        this.store.start().then(() => {
            console.log("Start completed.")
        })
    }

    static showEdit(location: string) {
        this.store.showEdit(location);
    }

    static cancelEdit(location: string) {
        this.store.cancelEdit(location);
    }

    static saveEdit(location: string) {
        this.store.saveEdit(location);
    }
}

polyfills.polyfill()