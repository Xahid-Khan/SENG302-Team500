import React from "react";
import {observer} from "mobx-react-lite";
import {AppBar, Box, Button, Toolbar, Typography} from "@mui/material";
import {NotificationDropdown} from "../notifications/NotificationDropdown";
import {AccountDropdown} from "./AccountDropdown";

export const navigateTo = (page: string) => {
  const globalUrlPathPrefix = window.localStorage.getItem("globalUrlPathPrefix")
  window.location.href = location.protocol + '//' + location.host + globalUrlPathPrefix + '/' + page
}

export const NavBar: React.FC = observer(() => {

  return (
      <React.Fragment>
        <AppBar position="fixed" sx={{bgcolor: "#788"}}>
          <Toolbar>
            <Box>
              <Typography
                  variant="h5">SENG302</Typography>
            </Box>

            <Box sx={{pl: 2, flexGrow: 1}}>
              <Button color='inherit' onClick={() => navigateTo("home_feed")}>
                <Typography textAlign="center">Home</Typography>
              </Button>
              <Button color='inherit' onClick={() => navigateTo("project-details")}>
                <Typography textAlign="center">Project</Typography>
              </Button>
              <Button color='inherit' onClick={() => navigateTo("user-list")}>
                <Typography textAlign="center">Users</Typography>
              </Button>
              <Button color='inherit' onClick={() => navigateTo("groups")}>
                <Typography textAlign="center">Groups</Typography>
              </Button>
            </Box>

            <NotificationDropdown></NotificationDropdown>
            <AccountDropdown></AccountDropdown>

          </Toolbar>
        </AppBar>
        <Toolbar sx={{mb: 3}}/>
      </React.Fragment>
  )
})