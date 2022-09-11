import React from "react";
import {observer} from "mobx-react-lite";
import {AppBar, Avatar, Box, Button, IconButton, Menu, MenuItem, Toolbar, Typography} from "@mui/material";

export const NavBar: React.FC = observer(() => {
    return (
        <AppBar>
            <Toolbar>
                <Box>
                    <Typography
                        variant="h4">SENG302</Typography>
                </Box>
                <Box sx={{ flexGrow: 1 }}>
                    <Button color='inherit'>
                        <Typography textAlign="center">Projects</Typography>
                    </Button>
                    <Button color='inherit'>
                        <Typography textAlign="center">Users</Typography>
                    </Button>
                    <Button color='inherit'>
                        <Typography textAlign="center">Groups</Typography>
                    </Button>
                </Box>
                <Box sx={{ flexGrow: 0 }}>
                    <IconButton>
                        <Avatar/>
                    </IconButton>
                </Box>
            </Toolbar>
        </AppBar>
    )
})