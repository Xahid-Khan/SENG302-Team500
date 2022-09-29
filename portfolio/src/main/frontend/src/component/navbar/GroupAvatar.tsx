import React from "react";
import {observer} from "mobx-react-lite";
import {Avatar, AvatarGroup} from "@mui/material";

interface IGroupAvatarProps {
    userIds: any[]
}

/**
 * A Group of avatar images that overlap, and is limited to showing 2 avatars
 */
export const GroupAvatar: React.FC<IGroupAvatarProps> = observer((props: IGroupAvatarProps) => {

    const globalImagePath = localStorage.getItem("globalImagePath");

    return (
        <AvatarGroup max={2} sx={{mr: 2}} spacing={20}>
            {props.userIds.map(
                (userId) => {
                    <Avatar src={`//${globalImagePath}${userId}`}/>
                }
            )}
        </AvatarGroup>
    )
})

/**
 * Returns a comma separated list of the users names
 * @param usernames a list of usernames
 */
export const getUserNamesList = (usernames: any[]) => {
    return usernames.join(', ')
}