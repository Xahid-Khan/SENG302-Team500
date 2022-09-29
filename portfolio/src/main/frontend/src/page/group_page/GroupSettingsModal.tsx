import * as React from "react";
import {EditGroupSettings} from "./EditGroupSettings";
import {useEffect} from "react";

export function GroupSettingsModal({viewGroupId}: any) {

    const handleCancel = () => {
        document.getElementById("group-settings-modal-open").style.display = "none"
    }

    return (
        <div className={"modal-container"} id={"group-settings-modal-open"}>
            <div className={"modal-group-settings"}>
                <div className={"modal-header"}>
                    <div className={"modal-title"}>
                        Edit group settings
                    </div>
                    <div onClick={() => handleCancel()}  className={"modal-close-button"} id={"group-settings-x"}>&times;</div>
                </div>
                <div className={"border-line"}/>
                <div className="modal-body group-settings-body">
                    <EditGroupSettings viewGroupId={viewGroupId}/>
                </div>
            </div>
        </div>
    );

}