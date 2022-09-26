import * as React from "react";
import {FormEvent} from "react";

export function EditGroupSettings(props: any) {
  if (props.editGroup.length == 0) {
    return (<></>);
  }
  const [editGroup, setEditGroup] = React.useState(props.editGroup);
  const [longName, setLongName] = React.useState(editGroup.longName);
  const [repositoryID, setRepositoryID] = React.useState(editGroup.repositoryId);
  const [repositoryToken, setRepositoryToken] = React.useState(editGroup.token);
  const [alias, setAlias] = React.useState('');
  const [longCharCount, setLongCharCount] = React.useState(0);

  const handleCancel = () => {
    document.getElementById("group-settings-modal-open").style.display = "none"
    setEditGroup(null);
    setLongCharCount(0);

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

    if (!repositoryToken.match("[0-9a-zA-Z-]{20}")) {
      errors = true;
      errorMessage = "Please enter a valid Token"
    }

    if (errors) {
      e.preventDefault()
      document.getElementById("edit-group-error").innerText = errorMessage;
    } else {

      await fetch(`/groups/update_repository/`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          "groupId": props.viewGroupId,
          "repositoryId": repositoryID,
          "token": repositoryToken
        })
      }).then((res) => {
        if (res.ok === true) {
          // window.location.reload()
        } else {
          document.getElementById("create-post-error").innerText = "Something went wrong, Please check if you've permission to edit this repository"
        }
      }).catch((e) => {
        console.log("error ", e)
      })

    }
  }

  console.log("EDIT GROUP SETTINGS")
  console.log(editGroup);
  console.log(editGroup.canEdit);
  return (
      <div>{editGroup.canEdit ?
          <div className={"edit-group-settings-container"}>
            <div className={"edit-group-settings"} style={{width: "100%"}}>
              <div className={"edit-group-form raised-card"}>
                <h3>Group Settings</h3>
                <div>
                  <label className={"settings-title"}>Short Name:</label>
                  <label>{editGroup.shortName}</label>
                </div>
                <br/>
                <div className={"settings-long-name"}>
                  <label className={"settings-title"}>Long Name:</label>
                  {editGroup.canEdit ?
                      <input type="text" name="long-name" className="input-name" id={"long-name"}
                             defaultValue={longName}
                             placeholder={editGroup.longName} maxLength={64} onChange={(e) => {
                        setLongName(e.target.value);
                        setLongCharCount(e.target.value.length)
                      }}/>
                      : <label>  {editGroup.longName}</label>}
                  {editGroup.canEdit ? <span className="input-length"
                                             id="long-name-length">{longCharCount} / 64</span> : ""}
                </div>
                <h3>Repo Settings</h3>
                <form onSubmit={(e) => validateRepositoryInfo(e)}>
                  <table>
                    <tr>
                      <td>
                        <label className={"settings-title"}>Alias:</label>
                      </td>
                      <td>
                        {editGroup.canEdit ?
                            <input type="text" name="alias" className="input-name" required
                                   id={"alias"}
                                   defaultValue={alias}
                                   maxLength={64} onChange={(e) => {
                              setAlias(e.target.value)
                            }}/>
                            : <label>Default alias</label>}
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label>Repository ID:</label>
                      </td>
                      <td>
                        {editGroup.canEdit ?
                            <input type="text" name="repository-id" className="input-name" required
                                   defaultValue={repositoryID}
                                   id={"repository-id"} maxLength={64} onChange={(e) => {
                              setRepositoryID(e.target.value)
                            }}/>
                            : <label>Default alias</label>}
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label>Token:</label>
                      </td>
                      <td>
                        {editGroup.canEdit ?
                            <input type="text" name="repository-token" className="input-name"
                                   required defaultValue={repositoryToken}
                                   id={"repository-token"} maxLength={64} onChange={(e) => {
                              setRepositoryToken(e.target.value)
                            }}/>
                            : <label>Default alias</label>}
                      </td>
                    </tr>
                  </table>
                  <div className="form-error" id="edit-group-error"/>
                  {editGroup.canEdit ?
                      <div className="modal-buttons">
                        <button className="button" id="group-repository-save" type={"submit"}>Save
                        </button>
                        <button onClick={() => handleCancel()} className="button"
                                id="group-settings-cancel">Cancel
                        </button>
                      </div>
                      : ""}
                </form>
              </div>
            </div>
          </div>
          : ""}</div>
  );
}