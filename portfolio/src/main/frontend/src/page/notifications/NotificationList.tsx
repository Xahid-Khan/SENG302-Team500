import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import {NotificationContract} from "../../contract/NotificationContract";
import {List, ListItem, ListItemButton, ListItemText} from "@mui/material";

export const NotificationList: React.FC = observer(() => {

    const [notifications, setNotifications] = React.useState([])

    const userId = parseInt(window.localStorage.getItem("userId"))

    const getNotifications = async (id: number)  => {
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

    const notificationHeading = (contract: NotificationContract) => {
        const today = new Date();
        const timestamp = contract.timeNotified;
        if (!timestamp){
            return contract.notifiedFrom
        }
        let formattedTime = "";
        if(timestamp.getDate() == today.getDate() && timestamp.getMonth() == today.getMonth() && timestamp.getFullYear() == today.getFullYear()){
            formattedTime = timestamp.getTime() + "";
        }
        return contract.notifiedFrom + " | " + formattedTime;
    }

    const notifications_items = () =>
        notifications.map((contract: NotificationContract) =>
            <ListItem disablePadding>
                <ListItemButton>
                    <ListItemText primary={notificationHeading(contract)} secondary={contract.description}/>
                </ListItemButton>
            </ListItem>
        )

    if (notifications && notifications != []) {
        return (
            <List>{notifications_items()}</List>
        )
    }
    return (
        <div>You have no notifications.</div>
    )
})