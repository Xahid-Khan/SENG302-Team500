import {
    Avatar, Box, Button, Chip,
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
import {getAPIAbsolutePath} from "../../util/RelativePathUtil";

interface IUserListProps{
    open: boolean
    onClose: () => void
    backButtonCallback: () => void
}

/**
 * A popover component for adding conversations.
 * Contains a list of users, a search bar to filter the list and a create button.
 * Also contains a series of deletable chips for each selected user.
 */
export const AddChatPopover: React.FC<IUserListProps> = observer((props: IUserListProps) => {

    const globalUrlPathPrefix = localStorage.getItem("globalUrlPathPrefix");
    const globalImagePath = localStorage.getItem("globalImagePath");

    const [search, setSearch] = React.useState("");

    const [users, setUsers] = React.useState([]);
    const [selectedUsers, setSelectedUsers] = React.useState([]);

    const fetchUsers = async () => {
        const messages = await fetch(getAPIAbsolutePath(globalUrlPathPrefix, `messages/all-users`), {
                method: 'GET'
            }
        )
        return messages.json()
    }

    useEffect(() => {
        fetchUsers().then((result) => {
            setUsers(result)
        })
    }, [])

    const users_items = () =>
        users
            .filter((user: any) => user.fullName.toLowerCase().includes(search.toLowerCase()))
            .map((user: any) =>
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

    const handleCreateClick = async() => {
        await fetch(getAPIAbsolutePath(globalUrlPathPrefix, `messages`), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                "userIds": selectedUsers
            })
        });
        props.backButtonCallback()
    }

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) =>{
        setSearch(event.target.value)
    }

    return (
        <Popover
            // Adapted from https://mui.com/material-ui/react-menu/
            anchorEl={document.getElementById('chats-list-button')}
            id="messages-menu"
            open={props.open}
            onClose={() =>{
                props.onClose()
            }}
            PaperProps={{sx: {maxHeight: 0.8, maxWidth: 0.3, minWidth: "300px"}}}
            transformOrigin={{horizontal: "right", vertical: "top"}}
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
                            <TextField label="Search" variant="standard" onChange={handleSearchChange}/>
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