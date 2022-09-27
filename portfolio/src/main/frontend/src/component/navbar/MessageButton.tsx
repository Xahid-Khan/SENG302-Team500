import React from "react";
import {observer} from "mobx-react-lite";
import {Badge, Box, IconButton} from "@mui/material";
import {ChatList} from "./ChatList";
import {MessageList} from "./MessageList";
import MailIcon from '@mui/icons-material/Mail';

export const MessageButton: React.FC = observer(() => {

    const [numUnseen, setNumUnseen] = React.useState(0)
    const [conversation, setConversation] = React.useState(undefined);

    //the element that was last clicked on
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
        console.log(event.currentTarget.id)
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleChatClick = (event: React.MouseEvent<HTMLElement>, contract: any) => {
        handleClick(event)
        setConversation(contract)
    };

    const handleBackClick = () => {
        setAnchorEl(document.getElementById('chats-list-button'));
        setConversation(undefined)
    }

    //uses the last clicked element to determine which menu to open
    const openChats = anchorEl?.id === 'chats-list-button';
    const openAdd = anchorEl?.id === 'add-button';
    const openChat = anchorEl?.id.startsWith('chat-button');

    //TODO fetch num unseen messages

    return (
        <React.Fragment>
            <Box sx={{display: 'flex', alignItems: 'center', textAlign: 'center'}}>
                <IconButton
                    id={'chats-list-button'}
                    onClick={handleClick}
                    size="small"
                    sx={{ml: 2}}
                    aria-controls={openChats ? 'chat-menu' : undefined}
                    aria-haspopup="true"
                    aria-expanded={openChats ? 'true' : undefined}
                >
                    <Badge badgeContent={numUnseen} color="primary">
                        <MailIcon sx={{width: 32, height: 32}}></MailIcon>
                    </Badge>
                </IconButton>
            </Box>

            <ChatList
                open={openChats}
                onClose={handleClose}
                addButtonCallback={handleClick}
                chatButtonCallback={handleChatClick}
            />

            <MessageList
                open={openChat}
                onClose={handleClose}
                conversation={conversation}
                backButtonCallback={handleBackClick}
            />

        </React.Fragment>
    )
})