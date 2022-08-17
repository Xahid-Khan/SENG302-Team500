import * as React from "react";
import {useEffect} from "react";


const getAllGroups = async ()  => {
    const allGroupsResponse = await fetch('api/v1/groups/all')
    return allGroupsResponse.json()
}

const deleteGroup = async (id: number, groups: any) => {
    let usersToRemoveIds: any = []
    groups.forEach((group: any) => {
        if (group.id === id) {
            group['users'].forEach((user: any) => {
                usersToRemoveIds.push(user.id)
            })
        }
    })
    const deleteResponse = await fetch(`api/v1/groups/${id}/delete-members`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(usersToRemoveIds)
    });

    const response = await fetch(`api/v1/groups/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    });
    if (response.ok === true) {
        document.getElementById(`group-${id}`).style.display = "none"
        document.getElementById("modal-delete-group-open").style.display = "none"
        window.location.reload()
    }
}

const clearEventListeners = (id: number, groups: any[]) => {
    document.getElementById("group-delete-confirm").removeEventListener("click", () => deleteGroup(id, groups))
    document.getElementById("group-delete-x").removeEventListener("click", () => clearEventListeners(id, groups))
    document.getElementById("group-delete-cancel").removeEventListener("click", () => clearEventListeners(id, groups))
    document.getElementById("modal-delete-group-open").style.display = "none"
}

const wireModal = (id: number, groups: any[]) => {
    document.getElementById("modal-delete-group-open").style.display = "block"
    groups.forEach((group) => {
        if (group.id === id) {
            document.getElementById("group-delete-modal-body").innerText = `Are you sure you want to delete this group? All ${group['users'].length} users will be removed from the group`
        }
    })
    document.getElementById("group-delete-confirm").addEventListener("click", () => deleteGroup(id, groups))
    document.getElementById("group-delete-x").addEventListener("click", () => clearEventListeners(id, groups))
    document.getElementById("group-delete-cancel").addEventListener("click", () => clearEventListeners(id, groups))
}

const toggleGroupView = (id: number) => {
    document.getElementById(`groups-user-list-${id}`).style.display = document.getElementById(`groups-user-list-${id}`).style.display === "none" ? "block" : "none"
    document.getElementById(`group-toggle-button-${id}`).innerText = document.getElementById(`group-toggle-button-${id}`).innerText === "visibility" ? "visibility_off" : "visibility"
}

export function ShowAllGroups({setViewGroupId}: any) {

    const [groups, setGroups] = React.useState([])

    useEffect(() => {
        getAllGroups().then((result) => {
            setGroups(result)
        })
    }, [])

    const isStudent = localStorage.getItem("isStudent") === "true"

    const formatRoles = (roles: any) => {
        let toReturn: string = ""
        roles.forEach((role: string) => {
            if (role === "COURSE_ADMINISTRATOR") {
                if (toReturn === "") {
                    toReturn += "Course Admin"
                } else {
                    toReturn += ", Course Admin"
                }
            } else {
                if (toReturn === "") {
                    toReturn += role[0] + role.slice(1, role.length).toLowerCase()
                } else {
                    toReturn += ", " + role[0] + role.slice(1, role.length).toLowerCase()
                }
            }
        })
        return toReturn
    }

    return (
        <div>
            {groups.map((group: any) => (
                <div className={'raised-card group'} id={`group-${group['id']}`} key={group['id']}>
                    <div className={"group-header"}>
                        {group['shortName'] !== 'Teachers' && group['shortName'] !== 'Non Group' && !isStudent ? <div className={"delete-group"}>
                            <span className={"material-icons"} onClick={() => wireModal(group['id'], groups)}>clear</span>
                        </div>
                        : "" }
                        {!isStudent ?
                        <button className="button edit-group-button" id="edit-group" data-privilege="teacher" onClick={() => {document.getElementById("modal-edit-group-members-open").style.display = "block"; setViewGroupId(group.id)}}> Manage Group Members</button>
                        : ""}
                        <div>
                            <span className={"material-icons group-settings"} onClick={() => {
                                document.getElementById("group-settings-modal-open").style.display='block';
                                setViewGroupId(group.id)
                            }}>settings</span>
                        </div>
                        <div id={`toggle-group-details-${group['id']}`}>
                            <span className={"material-icons toggle-group-details"} id={`group-toggle-button-${group['id']}`} onClick={() => toggleGroupView(group['id'])}>visibility</span>
                        </div>
                        <h2 className={'group-name-short'}>{group['shortName']}</h2>
                    </div>
                    <h3 className={'group-name-long'}>{group['longName']}</h3>
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
                                    <div className="tableCell">{user['firstName']} {user['lastName']}</div>
                                    <div className="tableCell">{user['username']}</div>
                                    <div className="tableCell">{user['nickName']}</div>
                                    <div className="tableCell">{formatRoles(user['roles'])}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            ))}
        </div>

    );
}