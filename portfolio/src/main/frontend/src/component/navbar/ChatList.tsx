import {Box, Divider, IconButton, ListItem, Menu, MenuItem, Typography} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import React from "react";
import {observer} from "mobx-react-lite";
import {ChatListItem} from "./ChatListItem";

interface IChatListProps{
    open: boolean
    onClose: () => void
    chats: any[]
}

export const ChatList: React.FC<IChatListProps> = observer((props: IChatListProps) => {

    const chats_items = () =>
        props.chats.map((contract: any) =>
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

    return <Menu
        anchorEl={document.body}
        id="chat-menu"
        open={props.open}
        onClose={props.onClose}
        PaperProps={{sx: {maxHeight: 0.4, maxWidth: 0.3, minWidth: "300px"}}}
        transformOrigin={{horizontal: "right", vertical: "bottom"}}
        anchorOrigin={{horizontal: "right", vertical: "bottom"}}
    >
        <ListItem style={{opacity: 1}}>
            <Box sx={{
                flexGrow: 1,
                display: "flex",
                alignItems: "center",
                textAlign: "center",
                justifyContent: "space-between"
            }}>
                <Typography>Chats</Typography>
                <IconButton>
                    <AddIcon></AddIcon>
                </IconButton>
            </Box>
        </ListItem>
        <Divider/>
        {props.chats.length === 0 ? no_chats_item() : chats_items()}
    </Menu>;
})