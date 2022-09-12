import {Box, Typography} from "@mui/material";
import React from "react";
import {observer} from "mobx-react-lite";

interface INotificationItemProps {
    description: string
    from: string
    time: Date
}

export const NotificationItem: React.FC<INotificationItemProps> = observer((props: INotificationItemProps) => {
    return (
    <Box sx={{display: "flex", flexDirection: "column"}}>
        <Box sx={{display: "flex", justifyContent: "space-between"}}>
            <Box sx={{flexGrow: 1}}><Typography>{props.time}</Typography></Box>
            <Box sx={{flexGrow: 0}}><Typography>{props.from}</Typography></Box>
        </Box>
        <Typography noWrap>{props.description}</Typography>
    </Box>
    )
});