import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import {Badge, Box, Divider, IconButton, List, ListItem, Menu, MenuItem, Typography} from "@mui/material";
import ChatBubbleIcon from '@mui/icons-material/ChatBubble';
import {getAPIAbsolutePath} from "../../util/RelativePathUtil";
import {NotificationContract} from "../../contract/NotificationContract";
import AddIcon from '@mui/icons-material/Add';
import {ChatListItem} from "./ChatListItem";

export const MessageButton: React.FC = observer(() => {

    const userId = parseInt(window.localStorage.getItem("userId"))
    const globalUrlPathPrefix = localStorage.getItem("globalUrlPathPrefix");

    const [chats, setChats] = React.useState([])
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
    const open = anchorEl?.id === 'chat-button';

    const getChats = async () => {
        // const notifications = await fetch(getAPIAbsolutePath(globalUrlPathPrefix, `chats/${userId}`), {
        //         method: 'GET'
        //     }
        // )
        // return notifications.json()
        return [
            {
                userId: "3",
                name: "Cody",
                seen: true
            },
            {
                userId: "3",
                name: "Zahid",
                seen: false
            },
        ]
    }

    const markAllAsSeen = async () => {
        await fetch(getAPIAbsolutePath(globalUrlPathPrefix, `chats/seen/${userId}`), {
                method: 'POST'
            }
        )
        getChats().then((result) => {
            setChats(result)
        })
    }

    useEffect(() => {
        getChats().then((result) => {
            setChats(result)
        })
    }, [])

    useEffect(() => {
        if(open){
            setNumUnseen(0)
        } else {
            setNumUnseen(chats.filter((contract: any) => !contract.seen).length)
        }
    }, [chats])

    const chats_items = () =>
        chats.map((contract: any) =>
            <ChatListItem
                key={contract.userId}
                userId={contract.userId}
                name={contract.name}
                seen={contract.seen}
            />
        )

    const no_chats_item = () => {
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
                    id={'chat-button'}
                    onClick={(x) => {
                        handleClick(x);
                        markAllAsSeen();
                    }}
                    size="small"
                    sx={{ml: 2}}
                    aria-controls={open ? 'chat-menu' : undefined}
                    aria-haspopup="true"
                    aria-expanded={open ? 'true' : undefined}
                >
                    <Badge badgeContent={1} color="primary">
                        <ChatBubbleIcon sx={{width: 32, height: 32}}></ChatBubbleIcon>
                    </Badge>
                </IconButton>
            </Box>
            <Menu
                anchorEl={document.body}
                id="chat-menu"
                open={open}
                onClose={handleClose}
                PaperProps={{sx: {maxHeight: 0.4, maxWidth: 0.3, minWidth: '300px'}}}
                transformOrigin={{horizontal: 'right', vertical: 'bottom'}}
                anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}
            >
                <ListItem style={{opacity: 1}}>
                    <Box sx={{flexGrow: 1, display: 'flex', alignItems: 'center', textAlign: 'center', justifyContent: 'space-between'}}>
                        <Typography>Chats</Typography>
                        <IconButton>
                            <AddIcon></AddIcon>
                        </IconButton>
                    </Box>
                </ListItem>
                <Divider/>
                {chats.length === 0 ? no_chats_item() : chats_items()}
            </Menu>
        </React.Fragment>
    )
})