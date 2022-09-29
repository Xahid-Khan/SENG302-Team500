import * as React from "react";
import {FormEvent, useEffect} from "react";

const getAllGroups = async ()  => {
    const allGroupsResponse = await fetch('api/v1/groups/all')
    return allGroupsResponse.json()
}

export function EditGroupSettings( {viewGroupId}: any ) {

    const [groups, setGroups] = React.useState([])

    useEffect(() => {
        getAllGroups().then((result) => {
            setGroups(result)
        })
    }, [])

    const userId = parseInt(window.localStorage.getItem("userId"))
    const isTeacher = window.localStorage.getItem("isTeacher")

    const [myGroup, setMyGroup] = React.useState({
        "id": -1,
        "longName": "",
        "shortName": "",
        "users": []
    })

    useEffect(() => {
        setMyGroup(groups.filter((item) => item.id === viewGroupId)[0])
    }, [viewGroupId])

    const [longName, setLongName] = React.useState('')
    const [alias, setAlias] = React.useState('')
    const[repositoryID, setRepositoryID] = React.useState('')
    const[repositoryName, setRepositoryName] = React.useState('')
    const [longCharCount, setLongCharCount] = React.useState(0)

    const handleCancel = () => {
        document.getElementById("group-settings-modal-open").style.display = "none"
    }

    const validateEditForm = (e: FormEvent) => {
        let errors = false
        let errorMessage

        if (longCharCount === 0) {
            errors = true
            errorMessage = "Long name cannot be empty."
        }

        if (errors) {
            e.preventDefault()
            document.getElementById("edit-group-error").innerText = errorMessage;
        } else {
            document.getElementById("group-settings-modal-open").style.display = "none"
            window.location.reload()
        }
    }
    const canEdit = (myGroup !== undefined ? myGroup.users.filter((user) => user.id === userId).length > 0 : false) || isTeacher === "true"

    return (
        <div>{myGroup ?
            <div className={"edit-group-settings-container"}>
                <div className={"edit-group-settings"}>
                    <div className={"edit-group-form raised-card"}>
                        <h3>Group Settings</h3>
                        <div>
                            <label className={"settings-title"}>Short Name:</label>
                            <label>  {myGroup.shortName}</label>
                        </div>
                        <br/>
                        <div className={"settings-long-name"}>
                            <label className={"settings-title"}>Long Name:</label>
                            {canEdit ? <input type="text" name="long-name" className="input-name" id={"long-name"} placeholder={myGroup.longName} maxLength={64} onChange={(e) => {setLongName(e.target.value); setLongCharCount(e.target.value.length)}}/>
                                : <label>  {myGroup.longName}</label> }
                            {canEdit ? <span className="input-length" id="long-name-length">{longCharCount} / 64</span> : ""}
                        </div>
                        <h3>Repo Settings</h3>
                        <label className={"settings-title"}>Alias:</label>
                        {canEdit ? <input type="text" name="alias" className="input-name" id={"alias"} maxLength={64} onChange={(e) => {setAlias(e.target.value)}}/>
                            : <label> Default alias</label>}
                        <div>
                            <label>Repository ID:</label>
                            {canEdit ? <input type="text" name="alias" className="input-name" id={"alias"} maxLength={64} onChange={(e) => {setRepositoryID(e.target.value)}}/>
                                : <label> Default alias</label>}
                        </div>
                        <div>
                            <label>Token:</label>
                            {canEdit ? <input type="text" name="alias" className="input-name" id={"alias"} maxLength={64} onChange={(e) => {setRepositoryName(e.target.value)}}/>
                            : <label> Default alias</label>}

                        </div>
                        <div className="form-error" id="edit-group-error"/>
                        { canEdit ?
                            <div className="modal-buttons">
                                <button onClick={(e) => validateEditForm(e)} className="button" id="group-settings-confirm">Save</button>
                                <button onClick={() => handleCancel()} className="button" id="group-settings-cancel">Cancel</button>
                            </div>
                            : ""}
                    </div>
                </div>

                <div className={"current-group"}>
                    <div className={'raised-card group'} id={`current-group-members-${myGroup['id']}`}>
                        <div className={"group-header"}>
                            <h2 className={'group-name-short'}>{myGroup['shortName']}</h2>
                        </div>
                        <h3 className={'group-name-long'}>{myGroup['longName']}</h3>
                        <div className={"table groups-table"} id={"current-group-users-list"}>
                            <div className={"groups-header"}>
                                <div className="tableCell"><b>Name</b></div>
                                <div className="tableCell"><b>Username</b></div>
                                <div className="tableCell"><b>Alias</b></div>
                                <div className="tableCell"><b>Roles</b></div>
                            </div>
                            {myGroup['users'].map((user: any) => (
                                <div className="groups-row" id={`current-group-users-${user.id}`} key={user.id} >
                                    <div className="tableCell">{user['firstName'] + " " + user['lastName']}</div>
                                    <div className="tableCell">{user['username']}</div>
                                    <div className="tableCell">{user['nickName']}</div>
                                    <div className="tableCell">{user['roles'].toString()}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
            : ""}</div>
    );
}