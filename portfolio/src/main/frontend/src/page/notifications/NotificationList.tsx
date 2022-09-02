import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import {NotificationContract} from "../../contract/NotificationContract";

export const NotificationList: React.FC = observer(() => {

    const [notifications, setNotifications] = React.useState([])

    const userId = parseInt(window.localStorage.getItem("userId"))

    const getNotifications = async (id: number)  => {
        console.log("getNotifications " + id)
        const notifications = await fetch(`api/v1/notifications/${id}`, {
                method: 'GET'
            }
        )
        return notifications.json()
    }

    useEffect(() => {
        getNotifications(userId).then((result) => {
            setNotifications(result)
        })
    }, [])

    const notifications_items = () =>
        notifications.map((contract: NotificationContract) =>
                <div>{contract.description}</div>
        )

    console.log(notifications)
    if (notifications) {
        return (
            <div>{notifications_items()}</div>
        )
    }
    return (
        <div>You have no notifications.</div>
    )
})