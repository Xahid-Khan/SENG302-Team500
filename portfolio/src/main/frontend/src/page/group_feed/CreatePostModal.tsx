import * as React from "react";
import {FormEvent, useEffect} from "react";
import {Simulate} from "react-dom/test-utils";
import submit = Simulate.submit;

export function CreatePostModal( {viewGroupId}: any ) {

    const [title, setTitle]= React.useState('')
    const [post, setPost] = React.useState('')
    const [shortCharacterCount, setShortCharacterCount] = React.useState(0)
    const [longCharacterCount,setLongCharacterCount] = React.useState(0)
    const userId = parseInt(window.localStorage.getItem("userId"))
    const isStudent = window.localStorage.getItem("isStudent")

    const validateCreateForm = async (formEvent: FormEvent) => {
        formEvent.preventDefault()
        let errors = false
        let errorMessage
        const urlData = document.URL.split("/");
        const viewGroupId =+urlData[urlData.length-1];


        if (errors) {
            document.getElementById("create-post-error").innerText = errorMessage;
        } else {

            await fetch(`/group_feed/new_post`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({"groupId": viewGroupId, "postContent": post})
            }).then((res) => {
                if (res.ok === true) {
                    window.location.reload()
                } else {
                    document.getElementById("create-post-error").innerText = "You do not have permission to post in this group."
                }
            }).catch((e) => {
                console.log("error ", e)
            })
        }
    }

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


                <form onSubmit={(e) => validateCreateForm(e)}>
                <div className="modal-body modal-edit-group-members-body">
                        <label className={"post-title"}>Title:</label>
                    <br/>
                        <input type="text" name="title-name" className="input-name post-title" required id={"title"} placeholder={title} maxLength={64} onChange={(e) => {setTitle(e.target.value); setShortCharacterCount(e.target.value.length)}}/>
                        <span className="title-length" id="title-length">{shortCharacterCount} / 64</span>
                    <br/>
                    </div>

                    <div className={"post-description"}>
                        <label className={"settings-description"}>Content:</label>
                        <br/>
                        <textarea className={"text-area"} id={"long-name"} placeholder={post} required cols={50} rows={10} maxLength={4096} onChange={(e) => {setPost(e.target.value); setLongCharacterCount(e.target.value.length)}}/>
                        <br/>
                    </div>
                    <div className="form-error" id="create-post-error"/>


            <div className="modal-buttons">
                <button className="button" id="create-post-save" type={"submit"}>Save</button>
                <button className="button" id="create-post-cancel" onClick={handleCancel}>Cancel</button>
            </div>
                </form>
            </div>
        </div>
);
}