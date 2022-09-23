import React from "react";
import {Box, IconButton, Menu, MenuItem} from "@mui/material";
import {MoreVertRounded} from "@mui/icons-material";

export function UnsubscribeDropDown (props: any) {

  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const unsubscribeUserToGroup = async (groupId: number, userId: number) => {
    console.log("UNSUB---Requested...")
    await fetch(`api/v1/unsubscribe`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        "userId": userId,
        "groupId": groupId
      })
    });
    props.getSubscriptions().then((result: any) => {
      props.setSubscriptions(result);
    })
  }

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  //uses the last clicked element to determine which menu to open
  const unsubMenu = anchorEl?.id === 'unsubscribe-button';

  return (
      <React.Fragment>
        <Box sx={{flexGrow: 0}}>
          <IconButton
              onClick={handleClick}
              id={"unsubscribe-button"}
              size={"medium"}
          >
          <MoreVertRounded/>
          </IconButton>
        </Box>
        <Menu
            anchorEl={anchorEl}
            id="unsubscribe-menu"
            open={unsubMenu}
            onClose={handleClose}
            onClick={handleClose}
            PaperProps={{sx: {maxHeight: 0.5}}}
            transformOrigin={{horizontal: 'right', vertical: 'top'}}
            anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}
        >
          <MenuItem onClick={() => unsubscribeUserToGroup(props.post.groupId, props.post.userId)}>
            Unsubscribe
          </MenuItem>
        </Menu>
      </React.Fragment>
  )
}