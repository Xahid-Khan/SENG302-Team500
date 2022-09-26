import React from "react";
import {observer} from "mobx-react-lite";
import {Avatar, Box, MenuItem, Typography} from "@mui/material";
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';

interface IChatListItemProps {
    userId: string
    name: string
    seen: boolean
    lastMessage: string
}

export const ChatListItem: React.FC<IChatListItemProps> = observer((props: IChatListItemProps) => {

    const globalImagePath = localStorage.getItem("globalImagePath");

    return (
        <MenuItem>
            <Box sx={{flexGrow: 1, display: "flex", justifyContent: "space-between", maxWidth: '100%'}}>
                <Avatar sx={{mr: 2}} src={`//${globalImagePath}${props.userId}`}/>
                <Box  sx={{flexGrow: 1, display: "flex", flexDirection: "column", maxWidth: '100%'}} style={{overflow:'hidden'}}>
                    <Typography variant="subtitle2" noWrap>{props.name}</Typography>
                    <Typography variant="body2" noWrap>{props.lastMessage}</Typography>
                </Box>
                {props.seen ? "" : <FiberManualRecordIcon fontSize={'small'}></FiberManualRecordIcon>}
            </Box>
        </MenuItem>
    )
});