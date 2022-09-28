import {
    Autocomplete,
    Avatar,
    Box, Button, Chip,
    Divider,
    IconButton,
    List,
    ListSubheader, MenuItem,
    Popover,
    TextField,
    Typography
} from "@mui/material";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import SearchIcon from '@mui/icons-material/Search';
import {ChatListItem} from "./ChatListItem";

interface IUserListProps{
    open: boolean
    onClose: () => void
    backButtonCallback: () => void
}

export const AddChatPopover: React.FC<IUserListProps> = observer((props: IUserListProps) => {

    const globalImagePath = localStorage.getItem("globalImagePath");

    const [users, setUsers] = React.useState([]);
    const [selectedUsers, setSelectedUsers] = React.useState([]);

    const fetchUsers = async () => {
        //TODO fetch and sort by seen, see NotificationDropdown getNotifications
        //mock data
        return [
            {
                userIds: "1",
                fullName: "Dracula"
            },
            {
                userIds: "2",
                fullName: "Sherlock"
            },
            {
                userIds: "3",
                fullName: "Spongebob"
            },
            {
                userIds: "4",
                fullName: "Spongebob"
            },
            {
                userIds: "5",
                fullName: "Spongebob"
            },
            {
                userIds: "6",
                fullName: "Spongebob"
            },
            {
                userIds: "57",
                fullName: "Spongebob"
            },
        ]
    }

    useEffect(() => {
        fetchUsers().then((result) => {
            setUsers(result)
        })
    }, [])

    const users_items = () =>
        users.map((user: any) =>
            <MenuItem onClick={(event) => handleContactAdd(user)}>
                <Box
                    key={user.id}
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        textAlign: "center",
                    }}
                >
                    <Avatar sx={{mr: 2}} src={`//${globalImagePath}${user.id}`}/>
                    <Typography>{user.fullName}</Typography>
                </Box>
            </MenuItem>
        )

    const no_users_item = () => {
        return (
            <MenuItem disabled style={{whiteSpace: 'normal', opacity: 1}} sx={{pt: 10, pb: 10}}>
                <Typography variant="body1">Looks like you have no notifications.</Typography>
            </MenuItem>
        )
    }

    const handleChipDelete = (id: number) => {
        setSelectedUsers(selectedUsers.filter((user) => {user.id !== id}))
    }

    const chips = () =>
        selectedUsers.map((user: any) =>
            <Chip
                variant="outlined"
                size="small"
                onDelete={() => handleChipDelete(user.id)}
                avatar={<Avatar src={`//${globalImagePath}${user.id}`} />}
                label={user.fullName}
            />
        )

    const handleContactAdd = (user: any) => {
        if(!selectedUsers.includes(user)) {
            setSelectedUsers(selectedUsers.concat(user));
        }
    }

    const handleCreateClick = () => {
        console.log("create", users)
        props.backButtonCallback()
    }

    return (
        <Popover
            // Adapted from https://mui.com/material-ui/react-menu/
            anchorEl={document.body}
            id="messages-menu"
            open={props.open}
            onClose={() =>{
                props.onClose()
            }}
            PaperProps={{sx: {maxHeight: 0.8, maxWidth: 0.3, minWidth: "300px"}}}
            transformOrigin={{horizontal: "right", vertical: "bottom"}}
            anchorOrigin={{horizontal: "right", vertical: "bottom"}}
        >
            <List>
                <ListSubheader sx={{pt: 1, pb: 1, pr: 0, pl: 0}}>
                    <Box sx={{
                        p: 1,
                        display: "flex",
                        flexDirection: 'column'
                    }}>
                        <Typography>Add Conversation</Typography>
                        <Box sx={{
                            pb:1,
                            flexGrow: 1,
                            display: "flex",
                            alignItems: "center",
                            textAlign: "center",
                        }}>
                            <IconButton onClick={props.backButtonCallback}>
                                <ChevronLeftIcon></ChevronLeftIcon>
                            </IconButton>
                            <TextField label="Search" variant="standard" />
                        </Box>
                        <Box sx={{display: 'flex', flexWrap: 'wrap'}}>{chips()}</Box>
                        <Box sx={{pt: 1, display: 'flex', justifyContent: 'flex-end'}}>
                            <Button disabled={!Boolean(selectedUsers)} size="small" variant="contained" color={"success"} onClick={handleCreateClick}>Create</Button>
                        </Box>

                    </Box>
                </ListSubheader>
                <Divider/>
                {users.length === 0 ? no_users_item() : users_items()}
            </List>
        </Popover>
    )
    })