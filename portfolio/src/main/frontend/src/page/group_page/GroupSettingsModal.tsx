import * as React from "react";
import {EditGroupSettings} from "./EditGroupSettings";
import {useEffect} from "react";


const getRepository = async ()  => {
    const repositoryResponse = await fetch(`https://eng-git.canterbury.ac.nz/api/v4/projects/13845/repository/`, {
        method: 'GET',
        headers: {
            'PRIVATE-TOKEN': 'ysewGuxG33Mzy4fixgjW'
        },
    })
    return repositoryResponse.json()
}

export function GroupSettingsModal({viewGroupId}: any) {

    const handleCancel = () => {
        document.getElementById("group-settings-modal-open").style.display = "none"
        window.location.reload()
    }

    const [branches, setBranches] = React.useState([])

    useEffect(() => {
        getRepository().then((result) => {
            setBranches(result)
        })
    }, [])



    return (
        <div className={"modal-container"} id={"group-settings-modal-open"}>
            <div className={"modal-group-settings"}>
                <div className={"modal-header"}>
                    <div className={"modal-title"}>
                        Edit group settings
                    </div>
                    <div className={"repositoryInfo"}>
                        {/*// @ts-ignore*/}
                        {branches['name']}
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