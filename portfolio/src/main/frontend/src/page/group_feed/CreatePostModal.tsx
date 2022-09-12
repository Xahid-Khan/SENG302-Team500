import * as React from "react";
import {FormEvent, useEffect} from "react";
import {EditGroupMembers} from "../group_page/EditGroupMembers";

export function CreatePostModal( {viewGroupId}: any ) {
    const [title, setTitle]= React.useState('')
    const [post, setPost] = React.useState('')
    const [longCharCount, setLongCharCount] = React.useState(0)
    const userId = parseInt(window.localStorage.getItem("userId"))
    const isStudent = window.localStorage.getItem("isStudent")

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
    const handleSubmit
    const handleCancel = () => {
        document.getElementById("create-post-modal-open").style.display = "none"
        window.location.reload()
    }

return (
        <div className={"modal-container"} id={"create-post-modal-open"}>
            <div className={"modal-edit-group-members"}>
                <div className={"modal-header"}>
                    <div className={"modal-title"}>
                        Create Post
                    </div>
                    <div className={"modal-close-button"} id={"create-post-cancel-x"} onClick={handleCancel}>&times;</div>
                </div>
                    <div className={"border-line"}/>
                <div className="modal-body modal-edit-group-members-body">
                        <label className={"post-title"}>Title:</label>
                    <br/>
                        <input type="text" name="long-name" className="input-name post-title" id={"title"} placeholder={post} maxLength={64} onChange={(e) => {setTitle(e.target.value); setLongCharCount(e.target.value.length)}}/>
                        <span className="input-length" id="long-name-length">{longCharCount} / 64</span>
                    <br/>
                    </div>
                    <div className="form-error" id="edit-group-error"/>
                    <div className={"post-description"}>
                        <label className={"settings-description"}>Description:</label>
                        <br/>
                        <textarea className={"text-area"} id={"long-name"} placeholder={post} cols={50} rows={10} maxLength={1024} onChange={(e) => {setPost(e.target.value)}}/>
                        <br/>
{/*                        <textarea cols={50} rows={10}>*/}
{/*  Hello there, this is some text in a text area*/}
{/*</textarea>*/}

                    </div>


            <div className="modal-buttons">
                <button className="button" id="create-post-save">Save</button>
                <button className="button" id="create-post-cancel" onClick={handleCancel}>Cancel</button>
            </div>



                {/*<div className="modal-body group-settings-body">*/}
                {/*    <EditGroupSettings viewGroupId={viewGroupId}/>*/}
                {/*</div>*/}
            </div>
        </div>
    // <div className={"modal-container"} id={"create-post-modal-open"}>
    //     <div className={"modal-edit-group-members"}>
    //         <div className={"modal-header"}>
    //             <div className={"modal-title"}>
    //                 Manage group members
    //             </div>
    //             <div className={"modal-close-button"} id={"group-edit-members-x"}>&times;</div>
    //         </div>
    //         <div className={"border-line"}/>
    //         <div className="modal-body modal-edit-group-members-body">
    //
    //         </div>
    //         <div className="modal-buttons">
    //             <button className="button" id="group-edit-members-confirm">Save</button>
    //             <button className="button" id="group-edit-members-cancel">Cancel</button>
    //         </div>
    //     </div>
    // </div>
);
}