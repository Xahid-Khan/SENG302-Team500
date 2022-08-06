import * as React from "react";
import {EditGroupMembers} from "./EditGroupMembers";

export function EditGroupMembersModal() {
    return (
        <div className={"modal-container"} id={"modal-edit-group-members-open"}>
            <div className={"modal-edit-group-members"}>
                <div className={"modal-header"}>
                    <div className={"modal-title"}>
                        Edit group members
                    </div>
                    <div className={"modal-close-button"} id={"group-edit-members-x"}
                         onClick={() => document.getElementById("modal-edit-group-members-open").style.display = "none"}>&times;</div>
                </div>
                <div className={"border-line"}/>
                <div className="modal-body modal-edit-group-members-body">
                    <EditGroupMembers/>
                </div>
                <div className="modal-buttons">
                    <button className="button" id="group-edit-members-confirm">Save</button>
                    <button className="button" id="group-edit-members-cancel">Cancel</button>
                </div>
            </div>
        </div>
    );
}

