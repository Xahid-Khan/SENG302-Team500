import * as React from "react";

export function EditGroupMembers() {

    const groups = [{
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
        }]

    let group = {"id": -1,
        "longName": "Default Group",
        "shortName": "Default",
        "users": [{"id": -1,
            "name": "Default",
            "username": "User",
            "alias": "Start",
            "roles": ["Student"]}]}

    if (document.getElementById("modal-edit-group-members-open")) {
        group = groups.find(item => item.id.toString() === document.getElementById("modal-edit-group-members-open").title)
    }
    return (
        <div className={"edit-group-members-container"}>
            <div className={"current-group"}>
                <h2>Current Group</h2>
                <div className={'raised-card group'} id={`current-group-members-${group['id']}`}>
                    <div className={"group-header"}>
                        <h2 className={'group-name-long'}>{group['longName']}</h2>
                    </div>
                    <h3 className={'group-name-short'}>{group['shortName']}</h3>
                    <div className={"table"} id={"group-list-"}>
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
            <div>
                <div className={"edit-group-members-buttons"}>
                    <button className={"edit-group-members-button"}>Add to current</button>
                    <button className={"edit-group-members-button"}>Copy <span className="material-icons" style={{fontSize: 14}}>arrow_forward</span></button>
                    <button className={"edit-group-members-button"}>Remove user</button>
                </div>
            </div>
            <div className={"other-group-users"}>
                <div className={"other-groups-users-header"}>
                    <h2>Other Users</h2>
                    <p>Filter By:</p>
                </div>
                <div className={'raised-card group'} id={`current-group-members-${group['id']}`}>
                    <div className={"group-header"}>
                        <h2 className={'group-name-long'}>{group['longName']}</h2>
                    </div>
                    <h3 className={'group-name-short'}>{group['shortName']}</h3>
                    <div className={"table"} id={"group-list-"}>
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
        </div>
    );
}