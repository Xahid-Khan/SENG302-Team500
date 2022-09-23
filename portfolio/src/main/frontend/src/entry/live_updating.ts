import * as polyfills from "../util/polyfill/socket_polyfill";
import {action, computed, makeObservable, observable, runInAction} from "mobx";
import {LoadingDone, LoadingNotYetAttempted, LoadingPending, LoadingStatus} from "../util/network/loading_status";
import SockJS from "sockjs-client";
import {Client as StompClient, Message as StompMessage, Stomp} from "@stomp/stompjs";
import {NotificationDropdown} from "../component/notifications/NotificationDropdown";

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
                body: location + "~show~" + localStorage.getItem("username")
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
                body: location + "~cancel~" + localStorage.getItem("username")
            })
        } else {
            console.log("Not connected")
        }
    }

    saveEdit(location: string) {
      console.log(this)
        if (this.connected) {
            console.log("Attempting to send ping...")
            this.stomp.publish({
                destination: "/app/alert",
                body: location + "~save~" + localStorage.getItem("username")
            })
        } else {
            console.log("Not connected")
        }
    }

  async start(location: string, destination: string) {
    await new Promise<void>((res) => {
      this.connect(res, location, destination)
    }).then(() => {
      if (!this.connected) {
        console.log("Not connected yet")
      }
    })
  }

  connect(onConnected: VoidFunction, location: string, destination: string): void {
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
        console.log(destination)
        console.log(location)
      if (destination === "alert") {
        store.stomp.subscribe("/topic/" + location, (message: StompMessage) => {
          store.onReceiveEditAlert(message)
        })
      } else {
          store.stomp.subscribe("/topic/" + location, (message: StompMessage) => {
              store.onNotification(message)
          })
      }

      onConnected()
    })
  }

    protected onReceiveEditAlert(frame: StompMessage) {
        runInAction(() => {
            this.pongArray.push(frame.body)
            const message = JSON.parse(frame.body)
            if (document.title !== "Calendar") {
                if (message['username'] !== localStorage.getItem("username")) {
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
                if (message['action'] !== "show") {
                    window.location.reload();
                }
            }
        })
    }
    protected onNotification(frame: StompMessage) {
      runInAction(() => {


      })
    }
}

export class Socket {
  private static store: PingPageStore = new PingPageStore();

  static start(location: string, destination: string) {
    this.store.start(location, destination).then(() => {
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