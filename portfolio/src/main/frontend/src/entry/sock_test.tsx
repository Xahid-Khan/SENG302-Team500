import * as polyfills from "../util/polyfill/socket_polyfill";
import React, {useEffect} from "react";
import ReactDOM from "react-dom";
import {observer, useLocalObservable} from "mobx-react-lite";
import {action, computed, makeObservable, observable, runInAction} from "mobx";
import {
    LoadingDone,
    LoadingError,
    LoadingNotYetAttempted,
    LoadingPending,
    LoadingStatus
} from "../util/network/loading_status";
import SockJS from "sockjs-client";
import {Stomp, CompatClient as StompClient, Message as StompMessage} from "@stomp/stompjs";

class PingPageStore {
    socket: WebSocket
    readonly stomp: StompClient

    connectStatus: LoadingStatus = new LoadingNotYetAttempted()

    pongArray: string[] = observable.array()
    nextPingValue: string = ""

    constructor() {
        makeObservable(this, {
            socket: observable,
            connectStatus: observable,
            pongArray: observable,
            nextPingValue: observable,

            connected: computed,

            setNextPingValue: action,
            sendPing: action,
            start: action
        })

        this.socket = new WebSocket("ws://localhost:9000/socket");
        this.stomp = Stomp.over(this.socket)

        console.log("Created new PingPageStore.")
    }

    get connected(): boolean {
        return this.connectStatus instanceof LoadingDone
    }

    setNextPingValue(newValue: string) {
        this.nextPingValue = newValue
    }

    sendPing() {
        console.log("Attempting to send ping...")
        if (this.connected) {
            this.stomp.send("/app/ping", {}, this.nextPingValue)
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

    protected onStompError(errorMaybe?: any) {
        console.log("Error!")
        runInAction(() => {
            this.connectStatus = new LoadingError(errorMaybe)
        })
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

        this.stomp.connect(
            {},
            (frame: StompMessage) => {
                // Connected
                console.log("Connected", frame)
                runInAction(() => {
                    this.connectStatus = new LoadingDone()
                })

                onConnected()
            },
            this.onStompError.bind(this)
        )
    }

    protected onReceivePong(frame: StompMessage) {
        runInAction(() => {
            this.pongArray.push(frame.body)
        })
    }

    protected subscribe(): void {
        console.log("Subscribing...")
        this.stomp.subscribe("/topic/pongs", (frame: StompMessage) => {
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

            <input type='text' value={store.nextPingValue} onChange={(evt) => store.setNextPingValue(evt.target.value)} placeholder='Next Ping value'/>
            <button onClick={() => store.sendPing()}>Send Ping</button>

            <ol>
                {store.pongArray.map((value, index) => (
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