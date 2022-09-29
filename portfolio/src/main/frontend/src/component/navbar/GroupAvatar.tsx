import React from "react";
import {observer} from "mobx-react-lite";
import {Avatar, AvatarGroup} from "@mui/material";

interface IGroupAvatarProps {
    userIds: any[]
}

/**
 * A ListItem component for a clickable contact
 */
export const GroupAvatar: React.FC<IGroupAvatarProps> = observer((props: IGroupAvatarProps) => {

    const globalImagePath = localStorage.getItem("globalImagePath");

    return (
        <AvatarGroup max={2} sx={{mr: 2}}>
            {props.userIds.map(
                (userId) => {
                    <Avatar src={`//${globalImagePath}${userId}`}/>
                }
            )}
        </AvatarGroup>
    )
})

export const getUserNamesList = (usernames: any[]) => {
    return usernames.join(', ')
}