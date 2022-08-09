import * as polyfills from "../util/polyfill/socket_polyfill";
import React from "react";
import {action, computed, makeObservable, observable, runInAction} from "mobx";
import {
    LoadingDone,
    LoadingNotYetAttempted,
    LoadingPending,
    LoadingStatus
} from "../util/network/loading_status";
import SockJS from "sockjs-client";
import {Message as StompMessage, Stomp} from "@stomp/stompjs";

/**
 * MobX-enabled store for the Ping/Socket Test page.
 *
 * This serves as a basic working model of how to use WebSockets with Spring Boot.
 */
class PingPageStore {
    stomp: any = null

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

        console.log("Created new PingPageStore.")
    }

    get connected(): boolean {
        return this.connectStatus instanceof LoadingDone
    }

    setNextPingValue(newValue: string) {
        this.nextPingValue = newValue
    }

    showEdit(location: string) {
        if (this.connected) {
            console.log("Attempting to send ping...")
            this.stomp.publish({
                destination: "/app/show",
                body: location
            })
        } else {
            console.log("Not connected")
        }
    }

    cancelEdit(location: string) {
        if (this.connected) {
            console.log("Attempting to send ping...")
            this.stomp.publish({
                destination: "/app/cancel",
                body: location
            })
        } else {
            console.log("Not connected")
        }
    }

    saveEdit(location: string) {
        if (this.connected) {
            console.log("Attempting to send ping...")
            this.stomp.publish({
                destination: "/app/save",
                body: location
            })
        } else {
            console.log("Not connected")
        }
    }

    async start() {
        await new Promise<void>((res) => {
            console.log("res :", res)
            this.connect(res)
        }).then(() => {
            console.log(this.connected)
            console.log(this.connectStatus)
            if (!this.connected) {
                throw new Error("Connect resolved but we aren't connected yet!")
            }
        })
    }

    connect(onConnected: VoidFunction): void {
        if (this.connectStatus instanceof LoadingPending || this.connectStatus instanceof LoadingDone) {
            console.warn("Cannot connect twice.")
            onConnected()
            return
        }
        let store = this;
        let socket = new SockJS("socket")
        store.stomp = Stomp.over(socket);

        store.stomp.connect({}, () => {
            console.log("Connected.")
            console.log("Subscribing...")
            store.connectStatus = new LoadingDone()
            store.stomp.subscribe("/topic/pongs", (message: StompMessage) => {
                store.onReceivePong(message)
            })
            onConnected()
        })

        console.log("stomp :", this.stomp)
    }

    onReceivePong(frame: StompMessage) {
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