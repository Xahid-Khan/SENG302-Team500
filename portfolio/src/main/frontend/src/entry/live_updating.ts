import * as polyfills from "../util/polyfill/socket_polyfill";
import {action, computed, makeObservable, observable, runInAction} from "mobx";
import {LoadingDone, LoadingNotYetAttempted, LoadingPending, LoadingStatus} from "../util/network/loading_status";
import SockJS from "sockjs-client";
import {Client as StompClient, Message as StompMessage, Stomp} from "@stomp/stompjs";

/**
 * MobX-enabled store for the Ping/Socket Test page.
 *
 * This serves as a basic working model of how to use WebSockets with Spring Boot.
 */
class PingPageStore {
    stomp: any = null

    connectStatus: LoadingStatus = new LoadingNotYetAttempted()
    path = window.localStorage.getItem("relativePath") + "/socket"
    pongArray: string[] = observable.array()
    nextPingValue: string = ""

    constructor() {
        makeObservable(this, {
            pongArray: observable,
            nextPingValue: observable,

            connected: computed,

            setNextPingValue: action,
            showEdit: action,
            start: action
        })
        this.stomp = new StompClient({
            webSocketFactory: () => new SockJS(this.path),
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
        if (this.connected) {
            console.log("Attempting to send ping...")
            this.stomp.publish({
                destination: "/app/alert",
                body: location + "~show~" + document.getElementsByClassName("username")[0].textContent
            })
        } else {
            console.log("Not connected")
        }
    }

    cancelEdit(location: string) {
        if (this.connected) {
            console.log("Attempting to send ping...")
            this.stomp.publish({
                destination: "/app/alert",
                body: location + "~cancel~" + document.getElementsByClassName("username")[0].textContent
            })
        } else {
            console.log("Not connected")
        }
    }

    saveEdit(location: string) {
        if (this.connected) {
            console.log("Attempting to send ping...")
            this.stomp.publish({
                destination: "/app/alert",
                body: location + "~save~" + document.getElementsByClassName("username")[0].textContent
            })
        } else {
            console.log("Not connected")
        }
    }

    async start() {
        await new Promise<void>((res) => {
            this.connect(res)
        }).then(() => {
            if (!this.connected) {
                console.log("Not connected yet")
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
        let socket = new SockJS(this.path)
        store.stomp = Stomp.over(socket);

        store.stomp.connect({}, () => {
            console.log("Connected.")
            console.log("Subscribing...")
            store.connectStatus = new LoadingDone()
            store.stomp.subscribe("/topic/edit-project", (message: StompMessage) => {
                store.onReceiveEditAlert(message)
            })
            onConnected()
        })
    }

    protected onReceiveEditAlert(frame: StompMessage) {
        runInAction(() => {
            this.pongArray.push(frame.body)
            const message = JSON.parse(frame.body)
            if (document.title !== "Calendar") {
                if (message['username'] !== document.getElementsByClassName("username")[0].textContent) {
                    if (message['action'] === "show") {
                        if (document.getElementById("event-form-" + message['location'])) {
                            document.getElementById("event-form-" + message['location']).innerText = message['name'] + " is currently editing"
                        } else {
                            document.getElementById("editing-form-" + message['location']).innerText = message['name'] + " is currently editing"
                        }
                    } else {
                        if (message['action'] === "save") {
                            window.location.reload();
                        }
                        if (document.getElementById("event-form-" + message['location'])) {
                            document.getElementById("event-form-" + message['location']).innerText = "";
                        } else {
                            document.getElementById("editing-form-" + message['location']).innerText = "";
                        }
                    }
                }
            } else {
                if (message['action'] === "save") {
                    window.location.reload();
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

    static isConnected(): boolean {
        return this.store.connected;
    }
}

polyfills.polyfill()