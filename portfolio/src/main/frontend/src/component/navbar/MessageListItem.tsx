import React from "react";
import {observer} from "mobx-react-lite";
import {Box, ListItem, Typography} from "@mui/material";

interface IMessageListItemProps {
    //TODO use contract type
    contract: any
    messageButtonCallback: (event: React.MouseEvent<HTMLElement>, id: string) => void
}

const theirBubbleStyle =
    {
        maxWidth: '100%',
        padding: '6px',
        borderRadius: '0px 8px 8px 8px',
        color: '#f9f9f9',
        background: 'royalblue',
        marginRight: '10%',
    };

const myBubbleStyle =
    {
        maxWidth: '100%',
        padding: '6px',
        borderRadius: '8px 0px 8px 8px',
        color: '#444444',
        background: '#eeeeee',
        marginLeft: '10%',
    };


export const MessageListItem: React.FC<IMessageListItemProps> = observer((props: IMessageListItemProps) => {

    const userId = parseInt(window.localStorage.getItem("userId"));
    const sentByMe = userId === props.contract.sentBy;
    const alignment = sentByMe ? "flex-end" : "flex-start";
    const bubbleStyle = sentByMe ? myBubbleStyle : theirBubbleStyle;

    return (
        <ListItem sx={{maxWidth: '100%', pt: '2px', pb: '2px'}}>
            <Box sx={{flexGrow: 1, display: "flex", justifyContent: alignment, maxWidth: '100%',}}>
                <Box sx={bubbleStyle} onClick={(e) => { props.messageButtonCallback(e, props.contract.messageId)}}>
                    <Typography variant="body2">{props.contract.messageContent}</Typography>
                </Box>
            </Box>
        </ListItem>
    )
});