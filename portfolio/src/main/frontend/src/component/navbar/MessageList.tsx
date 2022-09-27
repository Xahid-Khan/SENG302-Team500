import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import {
    Avatar,
    Box, Button,
    Divider,
    IconButton, List,
    ListSubheader,
    MenuItem,
    Popover, TextField,
    Typography
} from "@mui/material";
import {MessageListItem} from "./MessageListItem";
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import SendIcon from '@mui/icons-material/Send';

interface IMessageListProps{
    open: boolean
    onClose: () => void
    conversationId: string
    backButtonCallback: (event: React.MouseEvent<HTMLElement>) => void
}

export const MessageList: React.FC<IMessageListProps> = observer((props: IMessageListProps) => {

    const globalImagePath = localStorage.getItem("globalImagePath");
    const [messages, setMessages] = React.useState([]);
    const [message, setMessage] = React.useState("");
    const [selectedMessageId, setSelectedMessageId] = React.useState("");

    const openDeleteBox = Boolean(selectedMessageId)

    const getMessages = async () => {
        //TODO fetch and sort by time, see NotificationDropdown getNotifications
        //mock data
        return [
            {
                conversationId: "1",
                messageId: "1",
                sentBy: 1,
                messageContent: " 23452 3523 523 5",
                timeSent: new Date(),
            },
            {
                conversationId: "1",
                messageId: "2",
                sentBy: 3,
                messageContent: "1212hw dw 523 5",
                timeSent: new Date(),
            },
            {
                conversationId: "1",
                messageId: "3",
                sentBy: 3,
                messageContent: "1212hw dw 523 5",
                timeSent: new Date(),
            },
            {
                conversationId: "1",
                messageId: "4",
                sentBy: 1,
                messageContent: " 23452 3523 523 5",
                timeSent: new Date(),
            },
            {
                conversationId: "1",
                messageId: "5",
                sentBy: 1,
                messageContent: " 23452 3523 523 5 23452 3523 523 5 23452 3523 523 5 23452 3523 523 5 23452 3523 523 5 23452 3523 523 5 23452 3523 523 5 23452 3523 523 5 23452 3523 523 5 23452 3523 523 5",
                timeSent: new Date(),
            },
        ]
    }

    useEffect(() => {
        getMessages().then((result) => {
            setMessages(result)
        })
    }, [])

    const messages_items = () =>
        messages.map((contract: any) =>
            <MessageListItem
                key={contract.messageId}
                messageButtonCallback={handleMessageClick}
                contract={contract}
            />
        )

    const no_messages_item = () => {
        return (
            <MenuItem disabled style={{whiteSpace: 'normal', opacity: 1}} sx={{pt: 10, pb: 10}}>
                <Typography variant="body1">Looks like you have no messages.</Typography>
            </MenuItem>
        )
    }

    //TODO validate further maybe?
    const validMessage = (message: string): boolean => {
        return message != null && message.trim() !== ''
    }

    const handleSendClick = () =>{
        //TODO post message state
        if(validMessage(message)) {
            console.log('send: ', message)
        }
    }

    const handleMessageChange = (event: React.ChangeEvent<HTMLInputElement>) =>{
        setMessage(event.target.value)
    }

    const handleMessageKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) =>{
        if(event.key === 'Enter'){
            handleSendClick()
        }
    }

    const handleMessageClick = (event: React.MouseEvent<HTMLElement>, id: string) =>{
        setSelectedMessageId(id);
    }

    const handleDeleteMessageClick = () =>{
        //TODO fetch delete message
        setSelectedMessageId("");
    }

    const handleCancelMessageClick = () =>{
        setSelectedMessageId("");
    }


    const sendMessageBox = () => {
        return <Box sx={{
            p: 1,
            flexGrow: 1,
            display: "flex",
            alignItems: "flex-end",
            textAlign: "center",
            justifyContent: "space-between",
            //Sticks to bottom of scroll window
            position: 'sticky',
            bottom: 0,
            backgroundColor: 'white',
        }}>
            <TextField
                multiline
                maxRows={3}
                label={"message"}
                hiddenLabel
                variant={"outlined"}
                size={"small"}
                onChange={handleMessageChange}
                onKeyDown={handleMessageKeyDown}
                sx={{flexGrow: 1}}/>
            <IconButton onClick={handleSendClick} disabled={!validMessage(message)}>
                <SendIcon color={"primary"}/>
            </IconButton>
        </Box>;
    }

    const deleteMessageBox = () => {
        return (
            <Box sx={{
                p: 1,
                flexGrow: 1,
                display: "flex",
                alignItems: "center",
                textAlign: "center",
                justifyContent: "space-evenly",
                //Sticks to bottom of scroll window
                position: 'sticky',
                bottom: 0,
                backgroundColor: 'white',
            }}>
                <Button variant="contained" onClick={handleCancelMessageClick}>Cancel</Button>
                <Button variant="contained" color={"error"} onClick={handleDeleteMessageClick}>Delete</Button>
            </Box>
        )
    }

    return (
        <Popover
            // Adapted from https://mui.com/material-ui/react-menu/
            anchorEl={document.body}
            id="messages-menu"
            open={props.open}
            onClose={() =>{
                setSelectedMessageId("");
                props.onClose()
            }}
            PaperProps={{sx: {maxHeight: 0.5, maxWidth: 0.4, minWidth: "300px"}}}
            transformOrigin={{horizontal: "right", vertical: "bottom"}}
            anchorOrigin={{horizontal: "right", vertical: "bottom"}}
        >
            <List>
                <ListSubheader sx={{pt: 1, pb: 1, pr: 0, pl: 0}}>
                    <Box sx={{
                        flexGrow: 1,
                        display: "flex",
                        alignItems: "center",
                        textAlign: "center",
                    }}>
                        <IconButton onClick={props.backButtonCallback}>
                            <ChevronLeftIcon></ChevronLeftIcon>
                        </IconButton>
                        {/*TODO how to do groups -avatarGroup?*/}
                        {/*TODO use other id*/}
                        <Avatar sx={{mr: 2}} src={`//${globalImagePath}${3}`}/>
                        {/*TODO name*/}
                        <Typography>Name</Typography>
                    </Box>
                </ListSubheader>
                <Divider/>
                {messages.length === 0 ? no_messages_item() : messages_items()}
            </List>
            <Divider/>
            {openDeleteBox ? deleteMessageBox() : sendMessageBox()}
        </Popover>
    )
})