import React from "react";
import {observer} from "mobx-react-lite";
import {Box, MenuItem, Typography} from "@mui/material";
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';
import {getUserNamesList, GroupAvatar} from "./GroupAvatar";

interface IChatListItemProps {
    //TODO use contract type
    contract: any
    clickCallback: (event: React.MouseEvent<HTMLElement>, contract: any) => void
}

/**
 * A ListItem component for a clickable contact
 */
export const ChatListItem: React.FC<IChatListItemProps> = observer((props: IChatListItemProps) => {
    return (
        <MenuItem id={`chat-button-${props.contract.conversationId}`} onClick={(event) => {console.log("clicked");props.clickCallback(event, props.contract)}}>
            <Box sx={{flexGrow: 1, display: "flex", justifyContent: "space-between", maxWidth: '100%'}}>
                <GroupAvatar users={props.contract.users}/>
                <Box  sx={{flexGrow: 1, display: "flex", flexDirection: "column", maxWidth: '100%'}} style={{overflow:'hidden'}}>
                    <Typography variant="subtitle2" noWrap>{getUserNamesList(props.contract.users)}</Typography>
                    <Typography variant="body2" noWrap>{props.contract.mostRecentMessage?.messageContent}</Typography>
                </Box>
                {props.contract.seen ? "" : <FiberManualRecordIcon fontSize={'small'} color="primary"></FiberManualRecordIcon>}
            </Box>
        </MenuItem>
    )
});