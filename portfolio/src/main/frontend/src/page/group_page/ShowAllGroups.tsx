import * as React from "react";
import {Box, Modal, Button} from "@material-ui/core";


const getGroups = () => {
    return [{
        "id": 1,
        "longName": "Teaching Team",
        "shortName": "Teachers",
        "users": [
                {
                    "id": "1",
                    "name": "John",
                    "username": "johnstewart",
                    "alias": "Johnny",
                    "roles": ["Teacher, Student"]
                },
                {
                    "id": "2",
                    "name": "Sarah",
                    "username": "sarahjohn",
                    "alias": "Sa",
                    "roles": ["Student"]
                }
            ]
    }]
}

const deleteGroup = (id: number) => {
    document.getElementById(`group-${id}`).style.display = "none"
    document.getElementById("modal-delete-group-open").style.display = "none"
}

const clearEventListeners = (id: number) => {
    document.getElementById("group-delete-confirm").removeEventListener("click", () => deleteGroup(id))
    document.getElementById("group-delete-x").removeEventListener("click", () => clearEventListeners(id))
    document.getElementById("group-delete-cancel").removeEventListener("click", () => clearEventListeners(id))
    document.getElementById("modal-delete-group-open").style.display = "none"
}

const wireModal = (id: number) => {
    document.getElementById("modal-delete-group-open").style.display = "block"
    document.getElementById("group-delete-confirm").addEventListener("click", () => deleteGroup(id))
    document.getElementById("group-delete-x").addEventListener("click", () => clearEventListeners(id))
    document.getElementById("group-delete-cancel").addEventListener("click", () => clearEventListeners(id))
}

export function ShowAllGroups() {
    const [open, setOpen] = React.useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const groups = getGroups()

    return (
        <div>
            {groups.map((group: any) => (
                <div className={'raised-card group'} id={`group-${group.id}`} key={group.id}>
                    <div className={"group-header"}>
                        <div className={"delete-group"}>
                            <span className={"material-icons"} onClick={() => wireModal(group.id)}>clear</span>
                        </div>
                        <h2 className={'group-name-long'}>{group['longName']}</h2>
                        <button className="button edit-group-button" id="edit-group" data-privilege="teacher" onClick={handleOpen}> Edit
                            Group
                        </button>
                        <Modal
                            open={open}
                            onClose={handleClose}
                            aria-labelledby="modal-modal-title"
                            aria-describedby="modal-modal-description"
                        >
                            <Box >
                                <ShowAllGroups/>
                                <Button className="button add-member-group-button" id="edit-group" data-privilege="teacher" onClick={handleOpen}> Add
                                </Button>
                                <Button className="button delete-member-group-button" id="edit-group" data-privilege="teacher" onClick={handleOpen}> Copy
                                </Button>
                                <Button className="button copy-group-button" id="edit-group" data-privilege="teacher" onClick={handleOpen}> Delete
                                </Button>
                                <ShowAllGroups/>


                            </Box>
                        </Modal>
                    </div>
                    <h3 className={'group-name-short'}>{group['shortName']}</h3>
                    <div className={"table"}>
                        <div className={"groups-header"}>
                            <div className="tableCell"><b>Name</b></div>
                            <div className="tableCell"><b>Username</b></div>
                            <div className="tableCell"><b>Alias</b></div>
                            <div className="tableCell"><b>Roles</b></div>
                        </div>
                        {group['users'].map((user: any) => (
                            <div className="tableRow" id={`group-${group.id}-user-${user.id}`} key={user.id}>
                                <div className="tableCell">{user['name']}</div>
                                <div className="tableCell">{user['username']}</div>
                                <div className="tableCell">{user['alias']}</div>
                                <div className="tableCell">{user['roles'].toString()}</div>
                            </div>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
}