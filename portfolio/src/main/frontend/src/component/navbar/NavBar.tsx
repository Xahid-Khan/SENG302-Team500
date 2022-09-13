import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import {
    AppBar,
    Avatar, Badge,
    Box,
    Button,
    IconButton,
    Menu,
    MenuItem,
    Toolbar,
    Typography
} from "@mui/material";
import NotificationsIcon from "@mui/icons-material/Notifications";
import {NotificationItem} from "../../page/notifications/NotificationItem";
import {NotificationContract} from "../../contract/NotificationContract";



export const NavBar: React.FC = observer(() => {
    const globalImagePath = localStorage.getItem("globalImagePath");

    const [notifications, setNotifications] = React.useState([])
    const userId = parseInt(window.localStorage.getItem("userId"))

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
    const open2 = anchorEl?.id === 'account-button';

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

    const notifications_items = () =>
        notifications.map((contract: NotificationContract) =>
            <MenuItem>
                <NotificationItem
                    key={contract.id}
                    description={contract.description}
                    from={contract.notifiedFrom}
                    time={contract.timeNotified}
                />
            </MenuItem>
        )

    const numUnseen = () => {
        return notifications.filter((contract: NotificationContract) => !contract.seen).length
    }

    const navigateTo = (page: string) => {
        window.location.href=page
    }

    return (
        <React.Fragment>
            <AppBar position="fixed" sx={{ bgcolor: "#788" }}>
                <Toolbar>
                    <Box>
                        <Typography
                            variant="h5">SENG302</Typography>
                    </Box>

                    <Box sx={{pl: 2, flexGrow: 1}}>
                        <Button color='inherit' onClick={() => navigateTo("project-details")}>
                            <Typography textAlign="center">Project</Typography>
                        </Button>
                        <Button color='inherit' onClick={() => navigateTo("user-list")}>
                            <Typography textAlign="center">Users</Typography>
                        </Button>
                        <Button color='inherit' onClick={() => navigateTo("groups")}>
                            <Typography textAlign="center">Groups</Typography>
                        </Button>
                    </Box>

                    <Box sx={{display: 'flex', alignItems: 'center', textAlign: 'center'}}>
                        <IconButton
                            id={'notification-button'}
                            onClick={handleClick}
                            size="small"
                            sx={{ml: 2}}
                            aria-controls={open ? 'account-menu' : undefined}
                            aria-haspopup="true"
                            aria-expanded={open ? 'true' : undefined}
                        >
                            <Badge badgeContent={numUnseen()} color="primary">
                                <NotificationsIcon sx={{width: 32, height: 32}}></NotificationsIcon>
                            </Badge>
                        </IconButton>

                    </Box>
                    <Menu
                        anchorEl={anchorEl}
                        id="notification-menu"
                        open={open}
                        onClose={handleClose}
                        onClick={handleClose}
                        PaperProps={{sx: {maxHeight: 0.5}}}
                        transformOrigin={{horizontal: 'right', vertical: 'top'}}
                        anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}
                    >
                        {notifications_items()}
                    </Menu>

                    <Box sx={{flexGrow: 0}}>
                        <IconButton
                            id='account-button'
                            onClick={handleClick}
                            size="medium"
                            sx={{ml: 2}}
                            aria-controls={open ? 'account-menu' : undefined}
                            aria-haspopup="true"
                            aria-expanded={open ? 'true' : undefined}>
                            <Avatar src={"//" + globalImagePath + userId}/>
                        </IconButton>
                    </Box>
                    <Menu
                        anchorEl={anchorEl}
                        id="account-menu"
                        open={open2}
                        onClose={handleClose}
                        onClick={handleClose}
                        PaperProps={{sx: {maxHeight: 0.5}}}
                        transformOrigin={{horizontal: 'right', vertical: 'top'}}
                        anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}
                    >
                        <MenuItem onClick={() => navigateTo("my_account")}>
                            Account
                        </MenuItem>
                        <MenuItem onClick={() => navigateTo("logout")}>
                            Log out
                        </MenuItem>
                    </Menu>
                </Toolbar>
            </AppBar>
            <Toolbar sx={{mb:3}}/>
        </React.Fragment>
    )
})