import * as polyfills from "../util/polyfill/socket_polyfill";
import React, {useEffect} from "react";
import ReactDOM from "react-dom";
import {observer, useLocalObservable} from "mobx-react-lite";
import SockJS from "sockjs-client";
import {CompatClient, Message, Stomp} from "@stomp/stompjs";
import {makeObservable, observable, runInAction} from "mobx";

class PingPageStore {
    readonly socket: WebSocket
    readonly stomp: CompatClient

    connected: boolean = false

    pongArray: string[] = observable.array()

    constructor() {
        makeObservable(this, {
            connected: observable,
            pongArray: observable
        })

        this.socket = new SockJS("/api/v1/socket")
        this.stomp = Stomp.over(this.socket)

        console.log("Created new PingPageStore.")
    }

    sendPing() {
        console.log("Attempting to send ping...")
        if (this.connected) {
            this.stomp.send("/api/v1/ping", {}, "test123")
        }
    }

    async start() {
        await this.connect()
        if (this.connected) {
            this.subscribe()
        }
        else {
            throw new Error("Connect resolved but we aren't connected yet!")
        }
    }

    protected async connect(): Promise<Message> {
        return new Promise((res, rej) => {
            this.stomp.connect({}, (frame: Message) => {
                // Connected
                console.log("Connected")
                runInAction(() => {
                    this.connected = true
                })

                res(frame);
            }, (err: Message) => {
                // Error
                console.log("Error!")
                runInAction(() => {
                    this.connected = false
                })
                rej(err)
            })
        })
    }

    protected onReceivePong(frame: Message) {
        this.pongArray.push(frame.body)
    }

    protected subscribe(): void {
        console.log("Subscribing...")
        /*this.stomp.subscribe("/topic/pongs", (frame: IFrame) => {
            this.onReceivePong(frame)
        })*/
    }
}

const PingPage: React.FC = observer(() => {
    const store = useLocalObservable(() => new PingPageStore())

    useEffect(() => {
        setTimeout(() => {
            store.start().then(() => {
                console.log("Start completed.")
            })
        }, 1000)
    }, [store])

    return (
        <div>
            <div>{(store.connected) ? 'Connected!' : 'Disconnected'}</div>

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