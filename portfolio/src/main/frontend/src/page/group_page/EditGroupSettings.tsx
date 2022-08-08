import * as React from "react";
import {FormEvent, useEffect} from "react";

export function EditGroupSettings( {viewGroupId}: any ) {

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

    const [myGroup, setMyGroup] = React.useState({
        "id": -1,
        "longName": "",
        "shortName": "",
        "users": []
    })

    useEffect(() => {
        if (myGroup === undefined || myGroup.id === -1) {
            setMyGroup(groups.filter((item) => item.id === viewGroupId)[0])
        }
    }, [viewGroupId])

    const [longName, setLongName] = React.useState('')
    const [alias, setAlias] = React.useState('')
    const [longCharCount, setLongCharCount] = React.useState(0)

    const handleCancel = () => {
        document.getElementById("group-settings-modal-open").style.display = "none"
        window.location.reload()
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


    return (
        <div>{myGroup ?
            <div className={"edit-group-settings-container"}>
                <div className={"edit-settings"}>
                    <div className={"edit-form raised-card"}>
                        <h3>Group Settings</h3>
                        <div>
                            <label>Long Name:</label>
                            <input type="text" name="long-name" className="input-name" id={"long-name"} maxLength={64} onChange={(e) => {setLongName(e.target.value); setLongCharCount(e.target.value.length)}}/>
                            <span className="input-length" id="long-name-length">{longCharCount} / 64</span>
                        </div>
                        <h3>Repo Settings</h3>
                        <div>
                            <label>Alias:</label>
                            <input type="text" name="alias" className="input-name" id={"alias"} maxLength={64} onChange={(e) => {setAlias(e.target.value)}}/>
                        </div>
                        <div className="form-error" id="edit-group-error"/>
                        <div className="modal-buttons">
                            <button onClick={(e) => validateEditForm(e)} className="button" id="group-settings-confirm">Save</button>
                            <button onClick={() => handleCancel()} className="button" id="group-settings-cancel">Cancel</button>
                        </div>
                    </div>
                </div>

            <div className={"current-group"}>
                <div className={'raised-card group'} id={`current-group-members-${myGroup['id']}`}>
                    <div className={"group-header"}>
                        <h2 className={'group-name-long'}>{myGroup['longName']}</h2>
                    </div>
                    <h3 className={'group-name-short'}>{myGroup['shortName']}</h3>
                    <div className={"table groups-table"} id={"current-group-users-list"}>
                        <div className={"groups-header"}>
                            <div className="tableCell"><b>Name</b></div>
                            <div className="tableCell"><b>Username</b></div>
                            <div className="tableCell"><b>Alias</b></div>
                            <div className="tableCell"><b>Roles</b></div>
                        </div>
                        {myGroup['users'].map((user: any) => (
                            <div className="groups-row" id={`current-group-users-${user.id}`} key={user.id} >
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
            : ""}</div>
    );
}