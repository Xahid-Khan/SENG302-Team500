import * as React from "react";
import {FormEvent, useEffect} from "react";

const getAllGroups = async () => {
  const allGroupsResponse = await fetch('api/v1/groups/all')
  return allGroupsResponse.json()
}

export function EditGroupSettings({viewGroupId}: any) {

  const [commits, setCommits] = React.useState([])
  const [branches, setBranches] = React.useState([])
  const [token, setToken] = React.useState("")
  const [repoId, setRepoId] = React.useState(-1)
  const [alias, setAlias] = React.useState("")

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
  const getGroupRepoInfo = async (groupId: any) => {
    await fetch('/groups/repository/' + groupId + '/').then((result: any) => {
      setToken(result.token)
      setRepoId(result.repositoryId)
      console.log(result)
    }).catch((error: any) => {
      console.log(error)
    })
  }

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
    getGroupRepoInfo(viewGroupId)
  }, [viewGroupId])

  const [longName, setLongName] = React.useState('')
  const [repositoryID, setRepositoryID] = React.useState('')
  const [repositoryName, setRepositoryName] = React.useState('')
  const [repositoryToken, setRepositoryToken] = React.useState('')
  const [longCharCount, setLongCharCount] = React.useState(0)

  const handleCancel = () => {
    document.getElementById("group-settings-modal-open").style.display = "none"
    window.location.reload()
  }
  const validateLongName = (e: FormEvent) => {
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
      // window.location.reload()
    }
  }


  const validateRepositoryInfo = async (e: FormEvent) => {
    let errors = false
    let errorMessage

    if (isNaN(parseInt(repositoryID))) {
      errors = true
      errorMessage = "Repository ID must be a number."
    }


    if (errors) {
      e.preventDefault()
      document.getElementById("edit-group-error").innerText = errorMessage;
    } else {
      var repositoryIDNumber = +repositoryID
      await fetch(`/groups/update_repository/`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          "groupId": viewGroupId,
          "repositoryId": repositoryIDNumber,
          "token": repositoryToken
        })
      }).then((res) => {
        if (res.ok === true) {
          // window.location.reload()
        } else {
          document.getElementById("create-post-error").innerText = "You do not have permission to post in this group."
        }
      }).catch((e) => {
        console.log("error ", e)
      })

    }
  }

  const canEdit = (myGroup !== undefined ? myGroup.users.filter((user) => user.id === userId).length > 0 : false) || isStudent === "false"

  return (
      <div>{myGroup ?
          <div className={"edit-group-settings-container"}>
            <div className={"edit-group-settings"} style={{width:"100%"}}>
              <div className={"edit-group-form raised-card"}>
                <h3>Group Settings</h3>
                <div>
                  <label className={"settings-title"}>Short Name:</label>
                  <label>  {myGroup.shortName}</label>
                </div>
                <br/>
                <div className={"settings-long-name"}>
                  <label className={"settings-title"}>Long Name:</label>
                  {canEdit ?
                      <input type="text" name="long-name" className="input-name" id={"long-name"}
                             placeholder={myGroup.longName} maxLength={64} onChange={(e) => {
                        setLongName(e.target.value);
                        setLongCharCount(e.target.value.length)
                      }}/>
                      : <label>  {myGroup.longName}</label>}
                  {canEdit ? <span className="input-length" id="long-name-length">{longCharCount} / 64</span> : ""}
                </div>
                <h3>Repo Settings</h3>
                <form onSubmit={(e) => validateRepositoryInfo(e)}>
                  <table>
                    <tr>
                      <td>
                        <label className={"settings-title"}>Alias:</label>
                      </td>
                      <td>
                        {canEdit ?
                            <input type="text" name="alias" className="input-name" required id={"alias"}
                                   maxLength={64} onChange={(e) => {
                              setAlias(e.target.value)
                            }}/>
                            : <label> Default alias</label>}
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label>Repository ID:</label>
                      </td>
                      <td>
                        {canEdit ?
                            <input type="text" name="repository-id" className="input-name" required
                                   id={"repository-id"} maxLength={64} onChange={(e) => {
                              setRepositoryID(e.target.value)
                            }}/>
                            : <label> Default alias</label>}
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label>Token:</label>
                      </td>
                      <td>
                        {canEdit ?
                            <input type="text" name="repository-token" className="input-name" required
                                   id={"repository-token"} maxLength={64} onChange={(e) => {
                              setRepositoryToken(e.target.value)
                            }}/>
                            : <label> Default alias</label>}
                      </td>
                    </tr>

                    <div className="form-error" id="edit-group-error"/>
                    {canEdit ?
                        <div className="modal-buttons">
                          <button className="button" id="group-repository-save" type={"submit"}>Save
                          </button>
                          <button onClick={() => handleCancel()} className="button"
                                  id="group-settings-cancel">Cancel
                          </button>
                        </div>
                        : ""}
                  </table>
                </form>
              </div>
            </div>
          </div>
          : ""}</div>
  );
}