import * as React from "react";
import {useEffect} from "react";

export async function EditGroupMembers({viewGroupId}: any) {

    // const allGroupsResponse = await fetch('api/v1/groups/all');
    //
    // const groups = allGroupsResponse.body.

    const groups = [{
        "id": 10,
        "longName": "Users without a group",
        "shortName": "Non Group",
        "users": []
        },
        {
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

    const [membersAdded, setMembersAdded] = React.useState<{ [key: number]: number[] }>({})
    const [membersRemoved, setMembersRemoved] = React.useState<{ [key: number]: number[] }>({})

    const [allGroups, setAllGroups] = React.useState(groups)

    const [errorMessage, setErrorMessage] = React.useState("")

    const [otherGroupViewing, setOtherGroupViewing] = React.useState([{
        "id": -1,
        "longName": "",
        "shortName": "",
        "users": []
    }])

    const [myGroup, setMyGroup] = React.useState({
        "id": -1,
        "longName": "",
        "shortName": "",
        "users": []
    })

    const [myGroupUpdate, setMyGroupUpdate] = React.useState(false)
    const [otherGroupViewingUpdate, setOtherGroupViewingUpdate] = React.useState(false)

    useEffect(() => {
        if (myGroup === undefined || myGroup.id === -1) {
            setMyGroup(allGroups.filter((item) => item.id === viewGroupId)[0])
        }
    }, [viewGroupId, myGroupUpdate])

    useEffect(() => {
        if (otherGroupViewing === undefined || otherGroupViewing[0].id === -1) {
            setOtherGroupViewing(allGroups)
        }
    }, [otherGroupViewingUpdate, allGroups])


    const showFilter = (groupName: string) => {
        otherUsersSelected.forEach((id) => document.getElementById(`other-users-${id}`).style.backgroundColor = "transparent")
        currentGroupUsersSelected.forEach((id) => document.getElementById(`current-group-users-${id}`).style.backgroundColor = "transparent")
        setOtherUsersSelected([])
        setCurrentGroupUsersSelected([])
        setShiftClickOther([])
        setShiftClickCurrent([])
        document.getElementById("filter-groups-button").innerText = groupName;
        document.getElementById("other-users-title").innerText = groupName;
        if (groupName === "All users") {
            setOtherGroupViewing(allGroups)
        } else {
            setOtherGroupViewing(allGroups.filter((item) => item.shortName === groupName))
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
                    newSelections.push(parseInt(children[i].id.split('-')[2]))
                } else {
                    document.getElementById(children[i].id).style.backgroundColor = "transparent"
                }
            }
            setOtherUsersSelected(newSelections)
        }
    }

    const handleOtherUserSelect = (event: any, user: any) => {
        setErrorMessage("")
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
                    newSelections.push(parseInt(children[i].id.split('-')[3]))
                } else {
                    document.getElementById(children[i].id).style.backgroundColor = "transparent"
                }
            }
            setCurrentGroupUsersSelected(newSelections)
        }
    }

    const handleCurrentGroupUserSelect = (event: any, user: any) => {
        setErrorMessage("")
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

    const addToCurrent = () => {
        let duplicate = false
        let usersToAdd: any = []
        let usersToAddIds: number[] = []
        allGroups.forEach((group) => {
            group['users'].forEach((user) => {
                if (otherUsersSelected.includes(user.id)) {
                    usersToAdd.push(user)
                    usersToAddIds.push(user.id)
                }
            })
        })
        myGroup.users.forEach((user) => {
            usersToAdd.forEach((userToAdd: any) => {
                if (user.id === userToAdd.id) {
                    duplicate = true
                    setErrorMessage("One or more of the users selected are already in this group. Please deselect these users and try again")
                }
            })
        })

        if (duplicate === false) {
            myGroup.users = myGroup.users.concat(usersToAdd)
            allGroups.forEach((item, index) => {
                if (item.id === myGroup.id) {
                    allGroups[index].users = myGroup.users
                }
            })
            let filteredUserIds: number[] = []
            usersToAddIds.forEach((id, index) => {
                if (membersRemoved[myGroup.id] && membersRemoved[myGroup.id].includes(id)) {
                    membersRemoved[myGroup.id] = membersRemoved[myGroup.id].slice(0, index).concat(membersRemoved[myGroup.id].slice(index + 1, membersRemoved[myGroup.id].length))
                } else {
                    filteredUserIds.push(id)
                }
            })

            if (membersAdded[myGroup.id] === undefined) {
                membersAdded[myGroup.id] = filteredUserIds
            } else {
                filteredUserIds.forEach((id) => {
                    membersAdded[myGroup.id].push(id)
                })

            }
            setMyGroupUpdate(!myGroupUpdate)
        }
    }

    const copyToOther = () => {
        let duplicate = false
        let usersToAdd: any = []
        let usersToAddIds: number[] = []
        allGroups.forEach((group) => {
            group['users'].forEach((user) => {
                if (currentGroupUsersSelected.includes(user.id)) {
                    usersToAdd.push(user)
                    usersToAddIds.push(user.id)
                }
            })
        })
        otherGroupViewing[0].users.forEach((user: any) => {
            usersToAdd.forEach((userToAdd: any) => {
                if (user.id === userToAdd.id) {
                    duplicate = true
                    setErrorMessage("One or more of the users selected are already in this group. Please deselect these users and try again")
                }
            })
        })
        if (duplicate === false) {
            otherGroupViewing[0].users = otherGroupViewing[0].users.concat(usersToAdd)
            allGroups.forEach((item, index) => {
                if (item.id === otherGroupViewing[0].id) {
                    allGroups[index].users = otherGroupViewing[0].users
                }
            })

            let filteredUserIds: number[] = []
            usersToAddIds.forEach((id, index) => {
                if (membersRemoved[otherGroupViewing[0].id] && membersRemoved[otherGroupViewing[0].id].includes(id)) {
                    membersRemoved[otherGroupViewing[0].id] = membersRemoved[otherGroupViewing[0].id].slice(0, index).concat(membersRemoved[otherGroupViewing[0].id].slice(index + 1, membersRemoved[otherGroupViewing[0].id].length))
                } else {
                    filteredUserIds.push(id)
                }
            })

            if (membersAdded[otherGroupViewing[0].id] === undefined) {
                membersAdded[otherGroupViewing[0].id] = filteredUserIds
            } else {
                filteredUserIds.forEach((id) => {
                    membersAdded[otherGroupViewing[0].id].push(id)
                })
            }
            setOtherGroupViewingUpdate(!otherGroupViewingUpdate)
        }
    }

    const addToNonGroupIfNeeded = (user: any) => {
        let userInAnotherGroup = false
        allGroups.forEach((group) => {
            if (group.id !== otherGroupViewing[0].id) {
                group['users'].forEach((otherUser) => {
                    if (otherUser.id === user.id) {
                        userInAnotherGroup = true
                    }
                })
            }
        })
        if (userInAnotherGroup === false) {
            allGroups.forEach((group) => {
                if (group.longName === "Users without a group") {
                    group.users.push(user)
                }
            })
        }
    }

    const removeFromGroup = () => {
        let usersToRemoveIds: number[] = []
        if (currentGroupUsersSelected.length > 0) {
            currentGroupUsersSelected.forEach((id) => {
                myGroup.users.forEach((user: any, index: number) => {
                    if (user.id === id) {
                        myGroup.users = myGroup.users.slice(0, index).concat(myGroup.users.slice(index + 1, myGroup.users.length))
                        addToNonGroupIfNeeded(user)
                        usersToRemoveIds.push(user.id)
                    }
                })
            })
            allGroups.forEach((item, index) => {
                if (item.id === myGroup.id) {
                    allGroups[index].users = myGroup.users
                }
            })
            let filteredUserIds: number[] = []
            usersToRemoveIds.forEach((id, index) => {
                if (membersAdded[myGroup.id] && membersAdded[myGroup.id].includes(id)) {
                    membersAdded[myGroup.id] = membersAdded[myGroup.id].slice(0, index).concat(membersAdded[myGroup.id].slice(index + 1, membersAdded[myGroup.id].length))
                } else {
                    filteredUserIds.push(id)
                }
            })

            if (membersRemoved[myGroup.id] === undefined) {
                membersRemoved[myGroup.id] = filteredUserIds
            } else {
                filteredUserIds.forEach((id) => {
                    membersRemoved[myGroup.id].push(id)
                })
            }
            setCurrentGroupUsersSelected([])
            setMyGroupUpdate(!myGroupUpdate)
        } else {
            otherUsersSelected.forEach((id) => {
                otherGroupViewing[0].users.forEach((user: any, index: number) => {
                    if (user.id === id) {
                        otherGroupViewing[0].users = otherGroupViewing[0].users.slice(0, index).concat(otherGroupViewing[0].users.slice(index + 1, otherGroupViewing[0].users.length))
                        addToNonGroupIfNeeded(user)
                        usersToRemoveIds.push(user.id)
                    }
                })
            })
            allGroups.forEach((item, index) => {
                if (item.id === otherGroupViewing[0].id) {
                    allGroups[index].users = otherGroupViewing[0].users
                }
            })
            let filteredUserIds: number[] = []
            usersToRemoveIds.forEach((id, index) => {
                if (membersAdded[otherGroupViewing[0].id] && membersAdded[otherGroupViewing[0].id].includes(id)) {
                    membersAdded[otherGroupViewing[0].id] = membersAdded[otherGroupViewing[0].id].slice(0, index).concat(membersAdded[otherGroupViewing[0].id].slice(index + 1, membersAdded[otherGroupViewing[0].id].length))
                } else {
                    filteredUserIds.push(id)
                }
            })

            if (membersRemoved[otherGroupViewing[0].id] === undefined) {
                membersRemoved[otherGroupViewing[0].id] = filteredUserIds
            } else {
                filteredUserIds.forEach((id) => {
                    membersRemoved[otherGroupViewing[0].id].push(id)
                })
            }
            setOtherUsersSelected([])
            setOtherGroupViewingUpdate(!otherGroupViewingUpdate)
        }
    }

    const handleMemberEditSubmit = () => {
        document.getElementById("modal-edit-group-members-open").style.display = "none"
        window.location.reload()
    }

    const handleCancel = async () => {
        let response
        /* Taken from https://stackoverflow.com/questions/34913675/how-to-iterate-keys-values-in-javascript */
        for (const [key, value] of Object.entries(membersRemoved)) {
            response = await fetch(`api/v1/groups/${key}/delete-members`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(value)
            });
        }
        for (const [key, value] of Object.entries(membersAdded)) {
            response = await fetch(`api/v1/groups/${key}/add-members`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(value)
            });
        }

        document.getElementById("modal-edit-group-members-open").style.display = "none"
        window.location.reload()
    }

    if (document.getElementById("group-edit-members-confirm")) {
        document.getElementById("group-edit-members-confirm").addEventListener("click", () => handleMemberEditSubmit())
        document.getElementById("group-edit-members-cancel").addEventListener("click", () => handleCancel())
        document.getElementById("group-edit-members-x").addEventListener("click", () => handleCancel())
    }

    const openRemoveModal = () => {
        document.getElementById("modal-delete-open").style.display = "block"
        document.getElementById("modal-delete-confirm").addEventListener("click", () => {
            document.getElementById("modal-delete-open").style.display = "none"
            removeFromGroup()
        })
        document.getElementById("modal-delete-cancel").addEventListener("click", () => {
            document.getElementById("modal-delete-open").style.display = "none"
        })
        document.getElementById("modal-delete-x").addEventListener("click", () => {
            document.getElementById("modal-delete-open").style.display = "none"
        })
    }

    const getOtherViewingUsers = (): any => {
        let otherGroupViewingNoDuplicates: any = []
        otherGroupViewing.forEach((group) => {
            group['users'].forEach((user) => {
                let duplicate = false
                otherGroupViewingNoDuplicates.forEach((otherUser: any) => {
                    if (otherUser.id === user.id) {
                        duplicate = true
                    }
                })
                if (duplicate === false) {
                    otherGroupViewingNoDuplicates.push(user)
                }
            })
        })
        return otherGroupViewingNoDuplicates
    }

    return (
        <div>
            {myGroup ?
                <div className={"edit-group-members-container"}>
                    <div className={"current-group"}>
                        <h2>Current Group</h2>
                        <div className={'raised-card group'} id={`current-group-members-${myGroup['id']}`}>
                            <div className={"group-header"}>
                                <h2 className={'group-name-long'}>{myGroup['longName']}</h2>
                            </div>
                            <h3 className={'group-name-short'}>{myGroup['shortName']}</h3>
                            <div className={"user-list-table"}>
                                <div className={"table groups-table"} id={"current-group-users-list"}>
                                    <div className={"groups-header"}>
                                        <div className="tableCell"><b>Name</b></div>
                                        <div className="tableCell"><b>Username</b></div>
                                        <div className="tableCell"><b>Alias</b></div>
                                        <div className="tableCell"><b>Roles</b></div>
                                    </div>
                                    {myGroup['users'].map((user: any) => (
                                        <div className="groups-row" id={`current-group-users-${user.id}`} key={user.id}
                                             onClick={(event => handleCurrentGroupUserSelect(event, user))}>
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
                    <div>
                        <div className={"edit-group-members-buttons"}>
                            <button className={"edit-group-members-button"} disabled={otherUsersSelected.length === 0}
                                    onClick={() => addToCurrent()}><span className="material-icons"
                                                                         style={{fontSize: 14}}>arrow_back</span> Add to
                                current
                            </button>
                            <button className={"edit-group-members-button"}
                                    disabled={currentGroupUsersSelected.length === 0 || document.getElementById("filter-groups-button").innerText === "All users"}
                                    onClick={() => copyToOther()}>Copy to other <span className="material-icons"
                                                                                      style={{fontSize: 14}}>arrow_forward</span>
                            </button>
                            <button className={"edit-group-members-button"}
                                    disabled={currentGroupUsersSelected.length === 0 && (otherUsersSelected.length === 0 || document.getElementById("filter-groups-button").innerText === "All users")}
                                    onClick={() => openRemoveModal()}>Remove from group
                            </button>
                        </div>
                        <div className={"edit-group-members-error"}>{errorMessage}</div>
                    </div>
                    <div className={"other-group-users"}>
                        <div className={"other-groups-users-header"}>
                            <div className={"filter-groups"}>
                                <div className={"filter-groups-dropdown"}>
                                    <div className={"filter-groups-header"}>
                                        <button className={"filter-groups-button"} id={"filter-groups-button"}>All
                                            users
                                        </button>
                                    </div>
                                    <div className={"filter-groups-options"} id={"filter-groups-options"}>
                                        <button className={"filter-option-button"}
                                                onClick={() => showFilter("All users")}>All users
                                        </button>
                                        {allGroups.filter((item) => item.id != myGroup.id && item.longName !== "Users without a group").map((item) => (
                                            <button className={"filter-option-button"} key={item.id}
                                                    onClick={() => showFilter(item['shortName'])}>{item['shortName']}</button>
                                        ))}
                                    </div>
                                </div>
                            </div>
                            <div className={"filter-label"}>Select Group:</div>
                            <div>
                                <h2>Other Users</h2>
                            </div>
                        </div>
                        <div className={'raised-card group'} id={`current-group-members-${myGroup['id']}`}>
                            <h2 id={"other-users-title"}>All Users</h2>
                            <div className={"user-list-table"}>
                                <div className={"table groups-table"} id={"other-users-list"}>
                                    <div className={"groups-header"}>
                                        <div className="tableCell"><b>Name</b></div>
                                        <div className="tableCell"><b>Username</b></div>
                                        <div className="tableCell"><b>Alias</b></div>
                                        <div className="tableCell"><b>Roles</b></div>
                                    </div>
                                    {getOtherViewingUsers().map((user: any) => (
                                        <div className="groups-row" id={`other-users-${user.id}`} key={user.id}
                                             onClick={(event => handleOtherUserSelect(event, user))}>
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
                </div>
                : ""}
        </div>
    );
}