import {Box, Divider, IconButton, ListSubheader, Menu, MenuItem, Typography} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import {ChatListItem} from "./ChatListItem";

interface IChatListProps{
    open: boolean
    onClose: () => void
    addButtonCallback: (event: React.MouseEvent<HTMLElement>) => void
    chatButtonCallback: (event: React.MouseEvent<HTMLElement>, contract: any) => void
}

export const ChatList: React.FC<IChatListProps> = observer((props: IChatListProps) => {

    const [chats, setChats] = React.useState([]);

    const getChats = async () => {
        //TODO fetch and sort by seen, see NotificationDropdown getNotifications
        //mock data
        return [
            {
                conversationId: "1",
                userIds: ["3", "3"],
                creationDate: new Date(),
                seen: false,
                mostRecentMessage: " wadfawdfaw dfwa fawf awf waf waf awf awf wa f",
            },
            {
                conversationId: "2",
                userIds: ["A", "1"],
                creationDate: new Date(),
                seen: true,
                mostRecentMessage: " wadfawdfaw dfwa fawf awf waf waf awf awf wa f",
            },
            {
                conversationId: "3",
                userIds: ["Shay", "3"],
                creationDate: new Date(),
                seen: true,
                mostRecentMessage: " wadfawdfaw dfwa fawf awf waf waf awf awf wa f",
            },
            {
                conversationId: "3",
                userIds: ["Shay", "3"],
                creationDate: new Date(),
                seen: true,
                mostRecentMessage: " wadfawdfaw dfwa fawf awf waf waf awf awf wa f",
            },
            {
                conversationId: "3",
                userIds: ["Shay", "3"],
                creationDate: new Date(),
                seen: true,
                mostRecentMessage: " wadfawdfaw dfwa fawf awf waf waf awf awf wa f",
            },
            {
                conversationId: "3",
                userIds: ["Shay", "3"],
                creationDate: new Date(),
                seen: true,
                mostRecentMessage: " wadfawdfaw dfwa fawf awf waf waf awf awf wa f",
            },
            {
                conversationId: "3",
                userIds: ["Shay", "3"],
                creationDate: new Date(),
                seen: true,
                mostRecentMessage: " wadfawdfaw dfwa fawf awf waf waf awf awf wa f",
            },
        ]
    }

    useEffect(() => {
        getChats().then((result) => {
            setChats(result)
        })
    }, [])

    const chats_items = () =>
        chats.map((contract: any) =>
            <>
                <ChatListItem
                    key={contract.conversationId}
                    contract={contract}
                    clickCallback={props.chatButtonCallback}
                />
                <Divider/>
            </>
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
            <Menu
                anchorEl={document.body}
                id="chat-menu"
                open={props.open}
                onClose={props.onClose}
                PaperProps={{sx: {maxHeight: 0.5, maxWidth: 0.3, minWidth: "300px"}}}
                transformOrigin={{horizontal: "right", vertical: "bottom"}}
                anchorOrigin={{horizontal: "right", vertical: "bottom"}}
            >
                <ListSubheader>
                    <Box sx={{
                        flexGrow: 1,
                        display: "flex",
                        alignItems: "center",
                        textAlign: "center",
                        justifyContent: "space-between"
                    }}>
                        <Typography>Chats</Typography>
                        <IconButton onClick={props.addButtonCallback}>
                            <AddIcon></AddIcon>
                        </IconButton>
                    </Box>
                </ListSubheader>
                <Divider/>
                {chats.length === 0 ? no_chats_item() : chats_items()}
            </Menu>
        </React.Fragment>

    )



})