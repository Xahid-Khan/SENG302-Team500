import React from "react";
import {observer} from "mobx-react-lite";
import {Avatar, Box, MenuItem, Typography} from "@mui/material";
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';

interface IChatListItemProps {
    //TODO use contract type
    contract: any
    clickCallback: (event: React.MouseEvent<HTMLElement>, id: string) => void
}

/**
 * A ListItem component for a clickable contact
 */
export const ChatListItem: React.FC<IChatListItemProps> = observer((props: IChatListItemProps) => {

    const globalImagePath = localStorage.getItem("globalImagePath");

    return (
        <MenuItem id={`chat-button-${props.contract.conversationId}`} onClick={(event) => {props.clickCallback(event, props.contract.conversationId)}}>
            <Box sx={{flexGrow: 1, display: "flex", justifyContent: "space-between", maxWidth: '100%'}}>
                {/*TODO user id->name and handle groups name?*/}
                <Avatar sx={{mr: 2}} src={`//${globalImagePath}${props.contract.userIds[1]}`}/>
                <Box  sx={{flexGrow: 1, display: "flex", flexDirection: "column", maxWidth: '100%'}} style={{overflow:'hidden'}}>
                    <Typography variant="subtitle2" noWrap>{props.contract.userIds[1]}</Typography>
                    <Typography variant="body2" noWrap>{props.contract.mostRecentMessage}</Typography>
                </Box>
                {props.contract.seen ? "" : <FiberManualRecordIcon fontSize={'small'} color="primary"></FiberManualRecordIcon>}
            </Box>
        </MenuItem>
    )
});