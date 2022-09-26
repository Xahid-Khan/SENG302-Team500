import React from "react";
import {observer} from "mobx-react-lite";
import {Avatar, Box, MenuItem, Typography} from "@mui/material";

interface IChatListItemProps {
    userId: string
    name: string
    seen: boolean
}

export const ChatListItem: React.FC<IChatListItemProps> = observer((props: IChatListItemProps) => {

    const globalImagePath = localStorage.getItem("globalImagePath");

    return (
        <MenuItem disabled style={{whiteSpace: 'normal', opacity: 1}}>
            <Box sx={{display: "flex", justifyContent: "space-between"}}>
                <Avatar sx={{p: 1}} src={`//${globalImagePath}${props.userId}`}/>
                <Box  sx={{flexGrow: 1, display: "flex", flexDirection: "column"}}>
                    <Typography variant="subtitle2">{props.name}</Typography>
                    <Typography variant="body2">{"maybe you ha..."}</Typography>
                    {/*<Box sx={{flexGrow: 0}}><Typography variant="subtitle1">{props.sentBy}</Typography></Box>*/}
                </Box>
            </Box>
        </MenuItem>

    )
});