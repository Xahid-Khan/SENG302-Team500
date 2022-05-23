import * as polyfills from "../util/polyfill/socket_polyfill";
import React, {useEffect} from "react";
import ReactDOM from "react-dom";
import {observer, useLocalObservable} from "mobx-react-lite";
import {Client as StompClient, Message} from "@stomp/stompjs";
import {action, makeObservable, observable, runInAction} from "mobx";
import {
    LoadingDone,
    LoadingError,
    LoadingNotYetAttempted,
    LoadingPending,
    LoadingStatus
} from "../util/network/loading_status";

class PingPageStore {
    socket: WebSocket
    readonly stomp: StompClient

    connectStatus: LoadingStatus = new LoadingNotYetAttempted()

    pongArray: string[] = observable.array()

    constructor() {
        makeObservable(this, {
            socket: observable,
            connectStatus: observable,
            pongArray: observable
        })

        this.stomp = new StompClient({
            webSocketFactory: () => {
                runInAction(() => {
                    this.socket = new WebSocket("ws://localhost:9000/gs-guide-websocket")
                })
                return this.socket
            },
            connectionTimeout: 10000,
            heartbeatIncoming: 0,
            heartbeatOutgoing: 0,
            debug: (msg) => {
                console.log(new Date(), msg)
            }
        })

        console.log("Created new PingPageStore.")
    }

    get connected(): boolean {
        return this.connectStatus instanceof LoadingDone
    }

    sendPing() {
        console.log("Attempting to send ping...")
        if (this.connected) {
            this.stomp.publish({
                destination: "/app/ping",
                body: "test123"
            })
        }
    }

    async start() {
        await new Promise<void>((res) => {
            this.connect(res)
        })
        if (this.connected) {
            this.subscribe()
        }
        else {
            throw new Error("Connect resolved but we aren't connected yet!")
        }
    }

    protected connect(onConnected: VoidFunction): void {
        if (this.connectStatus instanceof LoadingPending || this.connectStatus instanceof LoadingDone) {
            console.warn("Cannot connect twice.")
            onConnected()
            return
        }

        runInAction(() => {
            this.connectStatus = new LoadingPending()
        })

        this.stomp.onConnect = (frame: Message) => {
            // Connected
            console.log("Connected")
            runInAction(() => {
                this.connectStatus = new LoadingDone()
            })

            onConnected()
        }
        this.stomp.onStompError = (err: Message) => {
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

        this.stomp.activate()
    }

    protected onReceivePong(frame: Message) {
        this.pongArray.push(frame.body)
    }

    protected subscribe(): void {
        console.log("Subscribing...")
        this.stomp.subscribe("/topic/pongs", (frame: Message) => {
            this.onReceivePong(frame)
        })
    }
}

const PingPage: React.FC = observer(() => {
    const store = useLocalObservable(() => new PingPageStore())

    useEffect(() => {
        store.start().then(() => {
            console.log("Start completed.")
        })
    }, [store])

    return (
        <div>
            <div>{store.connectStatus.constructor.name}</div>

            <button onClick={() => store.sendPing()}>Send Ping</button>

            <ol>
                {store.pongArray.map((index, value) => (
                    <li key={index}>{value}</li>
                ))}
            </ol>
        </div>
    )
})

polyfills.polyfill()
ReactDOM.render(
    <React.StrictMode>
        <PingPage/>
    </React.StrictMode>,
    document.getElementById("react-root")
)