import * as React from "react";


const getGroups = () => {
    return [{
        "id": 1,
        "longName": "Teaching Team",
        "shortName": "Teachers",
        "users": [
            {
                "id": 1,
                "name": "John",
                "username": "johnstewart",
                "alias": "Johnny",
                "roles": ["Teacher, Student"]
            },
            {
                "id": 2,
                "name": "Sarah",
                "username": "sarahjohn",
                "alias": "Sa",
                "roles": ["Student"]
            }
        ]},
        {
            "id": 2,
            "longName": "Group 500 - IDK",
            "shortName": "G500",
            "users": [
                {
                    "id": 4,
                    "name": "John",
                    "username": "cal127",
                    "alias": "Cod",
                    "roles": ["Teacher, Student"]
                },
                {
                    "id": 3,
                    "name": "James",
                    "username": "Potter",
                    "alias": "Jimmy",
                    "roles": ["Student"]
                }
            ]
        },
        {
            "id": 3,
            "longName": "The Boys",
            "shortName": "Lads",
            "users": [
                {
                    "id": 6,
                    "name": "Ben Stewart",
                    "username": "kingturtle12HD",
                    "alias": "Beef",
                    "roles": ["Teacher, Student"]
                },
                {
                    "id": 7,
                    "name": "Darren Patrick",
                    "username": "dpatrick2002",
                    "alias": "Dazza",
                    "roles": ["Student"]
                }
            ]}]
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

const toggleGroupView = (id: number) => {
    document.getElementById(`groups-user-list-${id}`).style.display = document.getElementById(`groups-user-list-${id}`).style.display === "none" ? "block" : "none"
    document.getElementById(`group-toggle-button-${id}`).innerText = document.getElementById(`group-toggle-button-${id}`).innerText === "visibility" ? "visibility_off" : "visibility"
}

export function ShowAllGroups({setViewGroupId}: any) {

    const groups = getGroups()

    return (
        <div>
            {groups.map((group: any) => (
                <div className={'raised-card group'} id={`group-${group['id']}`} key={group['id']}>
                    <div className={"group-header"}>
                        <div className={"delete-group"}>
                            <span className={"material-icons"} onClick={() => wireModal(group['id'])}>clear</span>
                        </div>
                        <button className="button edit-group-button" id="edit-group" data-privilege="teacher" onClick={() => {document.getElementById("modal-edit-group-members-open").style.display = "block"; setViewGroupId(group.id)}}> Manage Group Members</button>
                        <div id={`toggle-group-details-${group['id']}`}>
                            <span className={"material-icons toggle-group-details"} id={`group-toggle-button-${group['id']}`} onClick={() => toggleGroupView(group['id'])}>visibility</span>
                        </div>
                        <h2 className={'group-name-long'}>{group['longName']}</h2>
                    </div>
                    <h3 className={'group-name-short'}>{group['shortName']}</h3>
                    <div className={"groups-page-user-list"} id={`groups-user-list-${group['id']}`}>
                        <div className={"table"} id={"group-list"}>
                            <div className={"groups-header"}>
                                <div className="tableCell"><b>Name</b></div>
                                <div className="tableCell"><b>Username</b></div>
                                <div className="tableCell"><b>Alias</b></div>
                                <div className="tableCell"><b>Roles</b></div>
                            </div>
                            {group['users'].map((user: any) => (
                                <div className="tableRow" id={`group-${group['id']}-user-${user.id}`} key={user.id}>
                                    <div className="tableCell">{user['name']}</div>
                                    <div className="tableCell">{user['username']}</div>
                                    <div className="tableCell">{user['alias']}</div>
                                    <div className="tableCell">{user['roles'].toString()}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}