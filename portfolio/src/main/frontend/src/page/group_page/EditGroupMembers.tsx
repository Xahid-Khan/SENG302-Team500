import * as React from "react";

export function EditGroupMembers({viewGroupId}: any) {

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

    const [otherGroupViewing, setOtherGroupViewing] = React.useState(groups)

    let group = {"id": -1,
        "longName": "Default Group",
        "shortName": "Default",
        "users": [{"id": -1,
            "name": "Default",
            "username": "User",
            "alias": "",
            "roles": ["Student"]}]}

    const showFilter = (groupName: string) => {
        document.getElementById("filter-groups-button").innerText = groupName;
        document.getElementById("other-users-title").innerText = groupName;
        if (groupName === "All users") {
            setOtherGroupViewing(groups)
        } else {
            setOtherGroupViewing(groups.filter((item) => item.shortName === groupName))
        }
    }

    const [otherUsersSelected, setOtherUsersSelected] = React.useState([])
    const [currentGroupUsersSelected, setCurrentGroupUsersSelected] = React.useState([])
    const [shiftClickOther, setShiftClickOther] = React.useState([])
    const [shiftClickCurrent, setShiftClickCurrent] = React.useState([])

    const handleOtherShiftPress = (user: any) => {
        let firstSelection
        if (shiftClickOther.length === 0 && otherUsersSelected.length === 0) {
            setShiftClickOther([user.id])
            setOtherUsersSelected([user.id])
            document.getElementById(`other-users-${user.id}`).style.backgroundColor = "#ccc"
        } else {
            if (shiftClickOther.length > 0) {
                firstSelection = document.getElementById(`other-users-${shiftClickOther[0]}`)
            } else {
                const userIdClicked = otherUsersSelected[otherUsersSelected.length - 1]
                setShiftClickOther([userIdClicked])
                firstSelection = document.getElementById(`other-users-${userIdClicked}`)
            }
            const nextSelection = document.getElementById(`other-users-${user.id}`)
            const children = document.getElementById("other-users-list").children
            let startIndex = null
            let endIndex = null
            for (let i = 0; i < children.length; i++) {
                if (children[i] === firstSelection || children[i] === nextSelection) {
                    if (startIndex === null) {
                        startIndex = i
                    } else {
                        endIndex = i
                    }
                }
            }
            if (nextSelection === firstSelection) {
                endIndex = startIndex
            }
            let newSelections = []
            for (let i = 1; i < children.length; i++) {
                if (i >= startIndex && i <= endIndex) {
                    document.getElementById(children[i].id).style.backgroundColor = "#ccc"
                    newSelections.push(children[i].id.split('-')[2])
                } else {
                    document.getElementById(children[i].id).style.backgroundColor = "transparent"
                }
            }
            setOtherUsersSelected(newSelections)
        }
    }

    const handleOtherUserSelect = (event: any, user: any) => {
        currentGroupUsersSelected.forEach((id) => {
            document.getElementById(`current-group-users-${id}`).style.backgroundColor = "transparent"
        })
        setCurrentGroupUsersSelected([])
        setShiftClickCurrent([])
        if (event.shiftKey) {
            handleOtherShiftPress(user);
        } else {
            setShiftClickOther([])
            if (otherUsersSelected.filter((id) => id === user.id).length > 0) {
                setOtherUsersSelected(otherUsersSelected.filter((id) => id != user.id))
                document.getElementById(`other-users-${user.id}`).style.backgroundColor = "transparent"
            } else {
                if (event.ctrlKey || event.metaKey) {
                    setOtherUsersSelected([...otherUsersSelected, user.id])
                    document.getElementById(`other-users-${user.id}`).style.backgroundColor = "#ccc"
                } else {
                    otherUsersSelected.forEach((id) => {
                        document.getElementById(`other-users-${id}`).style.backgroundColor = "transparent"
                    })
                    setOtherUsersSelected([user.id])
                    document.getElementById(`other-users-${user.id}`).style.backgroundColor = "#ccc"
                }
            }
        }
    }

    const handleCurrentGroupShiftPress = (user: any) => {
        let firstSelection
        if (shiftClickCurrent.length === 0 && currentGroupUsersSelected.length === 0) {
            setShiftClickCurrent([user.id])
            setCurrentGroupUsersSelected([user.id])
            document.getElementById(`current-group-users-${user.id}`).style.backgroundColor = "#ccc"
        } else {
            if (shiftClickCurrent.length > 0) {
                firstSelection = document.getElementById(`current-group-users-${shiftClickCurrent[0]}`)
            } else {
                const userIdClicked = currentGroupUsersSelected[currentGroupUsersSelected.length - 1]
                setShiftClickCurrent([userIdClicked])
                firstSelection = document.getElementById(`current-group-users-${userIdClicked}`)
            }
            const nextSelection = document.getElementById(`current-group-users-${user.id}`)
            const children = document.getElementById("current-group-users-list").children
            let startIndex = null
            let endIndex = null
            for (let i = 0; i < children.length; i++) {
                if (children[i] === firstSelection || children[i] === nextSelection) {
                    if (startIndex === null) {
                        startIndex = i
                    } else {
                        endIndex = i
                    }
                }
            }
            if (nextSelection === firstSelection) {
                endIndex = startIndex
            }
            let newSelections = []
            for (let i = 1; i < children.length; i++) {
                if (i >= startIndex && i <= endIndex) {
                    document.getElementById(children[i].id).style.backgroundColor = "#ccc"
                    newSelections.push(children[i].id.split('-')[3])
                } else {
                    document.getElementById(children[i].id).style.backgroundColor = "transparent"
                }
            }
            setCurrentGroupUsersSelected(newSelections)
        }
    }

    const handleCurrentGroupUserSelect = (event: any, user: any) => {
        otherUsersSelected.forEach((id) => {
            document.getElementById(`other-users-${id}`).style.backgroundColor = "transparent"
        })
        setOtherUsersSelected([])
        setShiftClickOther([])
        if (event.shiftKey) {
            handleCurrentGroupShiftPress(user);
        } else {
            setShiftClickCurrent([])
            if (currentGroupUsersSelected.filter((id) => id === user.id).length > 0) {
                setCurrentGroupUsersSelected(currentGroupUsersSelected.filter((id) => id != user.id))
                document.getElementById(`current-group-users-${user.id}`).style.backgroundColor = "transparent"
            } else {
                if (event.ctrlKey || event.metaKey) {
                    setCurrentGroupUsersSelected([...currentGroupUsersSelected, user.id])
                    document.getElementById(`current-group-users-${user.id}`).style.backgroundColor = "#ccc"
                } else {
                    currentGroupUsersSelected.forEach((id) => {
                        document.getElementById(`current-group-users-${id}`).style.backgroundColor = "transparent"
                    })
                    setCurrentGroupUsersSelected([user.id])
                    document.getElementById(`current-group-users-${user.id}`).style.backgroundColor = "#ccc"
                }
            }
        }
    }

    groups.forEach((item) => {
        if (item.id === viewGroupId) {
            group = item;
        }
    })

    return (
        <div className={"edit-group-members-container"}>
            <div className={"current-group"}>
                <h2>Current Group</h2>
                <div className={'raised-card group'} id={`current-group-members-${group['id']}`}>
                    <div className={"group-header"}>
                        <h2 className={'group-name-long'}>{group['longName']}</h2>
                    </div>
                    <h3 className={'group-name-short'}>{group['shortName']}</h3>
                    <div className={"table groups-table"} id={"current-group-users-list"}>
                        <div className={"groups-header"}>
                            <div className="tableCell"><b>Name</b></div>
                            <div className="tableCell"><b>Username</b></div>
                            <div className="tableCell"><b>Alias</b></div>
                            <div className="tableCell"><b>Roles</b></div>
                        </div>
                        {group['users'].map((user: any) => (
                            <div className="groups-row" id={`current-group-users-${user.id}`} key={user.id} onClick={(event => handleCurrentGroupUserSelect(event, user))}>
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
                    <div className={"filter-groups"}>
                        <div className={"filter-groups-dropdown"}>
                            <div className={"filter-groups-header"}>
                                <button className={"filter-groups-button"} id={"filter-groups-button"}>All users</button>
                            </div>
                            <div className={"filter-groups-options"} id={"filter-groups-options"}>
                                <button className={"filter-option-button"} onClick={() => showFilter("All users")}>All users</button>
                                {groups.filter((item) => item.id != group.id).map((item) => (
                                    <button className={"filter-option-button"} key={item.id} onClick={() => showFilter(item['shortName'])}>{item['shortName']}</button>
                                ))}
                            </div>
                        </div>
                    </div>
                    <div className={"filter-label"}>Select Group:</div>
                    <div>
                        <h2>Other Users</h2>
                    </div>
                </div>
                <div className={'raised-card group'} id={`current-group-members-${group['id']}`}>
                    <h2 id={"other-users-title"}>All Users</h2>
                    <div className={"table groups-table"} id={"other-users-list"}>
                        <div className={"groups-header"}>
                            <div className="tableCell"><b>Name</b></div>
                            <div className="tableCell"><b>Username</b></div>
                            <div className="tableCell"><b>Alias</b></div>
                            <div className="tableCell"><b>Roles</b></div>
                        </div>
                            {otherGroupViewing.filter((item) => item.id != group.id).map((item) => (item['users'].map((user: any) => (
                                <div className="groups-row" id={`other-users-${user.id}`} key={user.id} onClick={(event => handleOtherUserSelect(event, user))}>
                                    <div className="tableCell">{user['name']}</div>
                                    <div className="tableCell">{user['username']}</div>
                                    <div className="tableCell">{user['alias']}</div>
                                    <div className="tableCell">{user['roles'].toString()}</div>
                                </div>
                            ))))}
                    </div>
                </div>
            </div>
        </div>
    );
}