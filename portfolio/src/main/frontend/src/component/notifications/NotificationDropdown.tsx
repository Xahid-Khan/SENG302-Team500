import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import {Badge, Box, Divider, IconButton, Menu, MenuItem, Typography} from "@mui/material";
import NotificationsIcon from "@mui/icons-material/Notifications";
import {NotificationContract} from "../../contract/NotificationContract";
import {NotificationItem} from "./NotificationItem";

export const NotificationDropdown: React.FC = observer(() => {

    const userId = parseInt(window.localStorage.getItem("userId"))

    const [notifications, setNotifications] = React.useState([])
    const [numUnseen, setNumUnseen] = React.useState(0)

    //the element that was last clicked on
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    //uses the last clicked element to determine which menu to open
    const open = anchorEl?.id === 'notification-button';

    const getNotifications = async () => {
        const notifications = await fetch(`api/v1/notifications/${userId}`, {
                method: 'GET'
            }
        )
        return notifications.json()
    }

    const markAllAsSeen = async () => {
        await fetch(`api/v1/notifications/seen/${userId}`, {
                method: 'POST'
            }
        )
        getNotifications().then((result) => {
            setNotifications(result)
        })
    }

    useEffect(() => {
        getNotifications().then((result) => {
            setNotifications(result)
        })
    }, [])

    useEffect(() => {
        if(open){
            setNumUnseen(0)
        } else {
            setNumUnseen(notifications.filter((contract: NotificationContract) => !contract.seen).length)
        }
    }, [notifications])

    const notifications_items = () =>
        notifications.map((contract: NotificationContract) =>
            <NotificationItem
                key={contract.id}
                description={contract.description}
                from={contract.notifiedFrom}
                time={contract.timeNotified}
            />
        )

    const no_notifications_item = () => {
        return (
            <MenuItem disabled style={{whiteSpace: 'normal', opacity: 1}} sx={{pt: 10, pb: 10}}>
                <Typography variant="body1">Looks like you have no notifications.</Typography>
            </MenuItem>
        )
    }

    return (
        <React.Fragment>
            <Box sx={{display: 'flex', alignItems: 'center', textAlign: 'center'}}>
                <IconButton
                    id={'notification-button'}
                    onClick={(x) => {
                        handleClick(x);
                        markAllAsSeen();
                    }}
                    size="small"
                    sx={{ml: 2}}
                    aria-controls={open ? 'notification-menu' : undefined}
                    aria-haspopup="true"
                    aria-expanded={open ? 'true' : undefined}
                >
                    <Badge badgeContent={numUnseen} color="primary">
                        <NotificationsIcon sx={{width: 32, height: 32}}></NotificationsIcon>
                    </Badge>
                </IconButton>

            </Box>
            <Menu
                anchorEl={anchorEl}
                id="notification-menu"
                open={open}
                onClose={handleClose}
                PaperProps={{sx: {maxHeight: 0.7, maxWidth: 0.3, minWidth: '300px'}}}
                transformOrigin={{horizontal: 'right', vertical: 'top'}}
                anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}
            >
                <MenuItem disabled style={{opacity: 1}}>
                    <Typography>Notifications</Typography>
                </MenuItem>
                <Divider/>
                {notifications.length === 0 ? no_notifications_item() : notifications_items()}
            </Menu>
        </React.Fragment>
    )
})
