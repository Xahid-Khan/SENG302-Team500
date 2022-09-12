import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import {
    AppBar,
    Avatar,
    Box,
    Button, Divider,
    Icon,
    IconButton, ListItem, ListItemButton,
    ListItemIcon, ListItemText,
    Menu,
    MenuItem,
    Toolbar,
    Tooltip,
    Typography
} from "@mui/material";
import NotificationsIcon from "@mui/icons-material/Notifications";
import {NotificationItem} from "../../page/notifications/NotificationItem";
import {NotificationContract} from "../../contract/NotificationContract";



export const NavBar: React.FC = observer(() => {
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

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

    const notifications_items = () =>
        notifications.map((contract: NotificationContract) =>
            <MenuItem>
                <NotificationItem
                    description={contract.description}
                    from={contract.notifiedFrom}
                    time={new Date()}
                />
            </MenuItem>
        )

    return (
        <AppBar>
            <Toolbar>
                <Box>
                    <Typography
                        variant="h4">SENG302</Typography>
                </Box>
                <Box sx={{flexGrow: 1}}>
                    <Button color='inherit'>
                        <Typography textAlign="center">Projects</Typography>
                    </Button>
                    <Button color='inherit'>
                        <Typography textAlign="center">Users</Typography>
                    </Button>
                    <Button color='inherit'>
                        <Typography textAlign="center">Groups</Typography>
                    </Button>
                </Box>


                <Box sx={{display: 'flex', alignItems: 'center', textAlign: 'center'}}>
                    <IconButton
                        onClick={handleClick}
                        size="small"
                        sx={{ml: 2}}
                        aria-controls={open ? 'account-menu' : undefined}
                        aria-haspopup="true"
                        aria-expanded={open ? 'true' : undefined}
                    >
                        <NotificationsIcon sx={{width: 32, height: 32}}></NotificationsIcon>
                    </IconButton>
                </Box>
                <Menu
                    anchorEl={anchorEl}
                    id="account-menu"
                    open={open}
                    onClose={handleClose}
                    onClick={handleClose}
                    PaperProps={{
                        elevation: 0,
                        sx: {
                            maxHeight: 0.5,
                            // overflowY: 'scroll',
                            filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))',
                            mt: 1.5,
                            '& .MuiAvatar-root': {
                                width: 32,
                                height: 32,
                                ml: -0.5,
                                mr: 1,
                            },
                            '&:before': {
                                content: '""',
                                display: 'block',
                                position: 'absolute',
                                top: 0,
                                right: 14,
                                width: 10,
                                height: 10,
                                bgcolor: 'background.paper',
                                transform: 'translateY(-50%) rotate(45deg)',
                                zIndex: 0,
                            },
                        },
                    }}
                    transformOrigin={{horizontal: 'right', vertical: 'top'}}
                    anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}
                >
                    {notifications_items()}
                </Menu>


                <Box sx={{flexGrow: 0}}>
                    <IconButton>
                        <Avatar/>
                    </IconButton>
                </Box>
            </Toolbar>
        </AppBar>
    )
})