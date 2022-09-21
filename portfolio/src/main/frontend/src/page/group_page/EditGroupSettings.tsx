import * as React from "react";
import {FormEvent, useEffect} from "react";

const getAllGroups = async ()  => {
    const allGroupsResponse = await fetch('api/v1/groups/all')
    return allGroupsResponse.json()
}

export function EditGroupSettings( {viewGroupId}: any ) {

    const[commits, setCommits] = React.useState([])
    const[branches, setBranches] = React.useState([])

    const commitRequest = new Request('https://eng-git.canterbury.ac.nz/api/v4/projects/13845/repository/commits', {
        method: 'GET',
        headers: new Headers({
            'PRIVATE-TOKEN': 'ysewGuxG33Mzy4fixgjW',
        })
    });
    const branchRequest = new Request('https://eng-git.canterbury.ac.nz/api/v4/projects/13845/repository/branches', {
        method: 'GET',
        headers: new Headers({
            'PRIVATE-TOKEN': 'ysewGuxG33Mzy4fixgjW',
        })
    });

    const getCommits = async () => {
        const getCommitsResponse = await fetch(commitRequest)
        return getCommitsResponse.json()
    }
    const getBranches = async () => {
        const getBranchesResponse = await fetch(branchRequest)
        return getBranchesResponse.json()
    }

    const [groups, setGroups] = React.useState([])

    useEffect(() => {
        getAllGroups().then((result) => {
            setGroups(result)
        })

        getCommits().then((result) => {
            setCommits(result)
        })
        getBranches().then((result) => {
            setBranches(result)
        })
    }, [])

    const userId = parseInt(window.localStorage.getItem("userId"))
    const isStudent = window.localStorage.getItem("isStudent")

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
    const[repositoryID, setRepositoryID] = React.useState('')
    const[repositoryName, setRepositoryName] = React.useState('')
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
    const canEdit = (myGroup !== undefined ? myGroup.users.filter((user) => user.id === userId).length > 0 : false) || isStudent === "false"

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
                                    <div className="tableCell">{user['firstName'] + " " + user['lastName']}</div>
                                    <div className="tableCell">{user['username']}</div>
                                    <div className="tableCell">{user['nickName']}</div>
                                    <div className="tableCell">{user['roles'].toString()}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>


                <div className={"groups-page-repository"} id={`groups-repository`}>
                    <h3 className={'group-repository-title'}>Branches</h3>
                    <div className={"table"} id={"group-list-branches"}>

                        {branches.map((branch: any) => (
                            <div className="tableRow">
                                <div className="tableCell">
                                    <a href={branch['web_url']} target="_blank">{branch['name']} ({Object.keys(branch['commit']).length} commits)</a> <br></br>
                                </div>
                            </div>
                        ))}
                    </div>
                    <h3 className={'group-repository-title'}>Commits</h3>
                    <div className={"table"} id={"group-list-commits"}>

                        {commits.map((commit: any) => (
                            <div className="tableRow">
                                <div className="tableCell">
                                    <strong>Name:</strong>{commit['author_name']} <br></br>
                                    <strong>Message:</strong> {commit['message']} <br></br>
                                    <strong>ID:</strong><a href={commit['web_url']} target="_blank">{commit['id']}</a> <br></br>
                                </div>
                            </div>
                        ))}


                    </div>
                </div>

            </div>
            : ""}</div>
    );
}