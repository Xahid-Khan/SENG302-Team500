import React, {FormEvent} from "react";


export const EditPostModal = ({postData}: any) => {
  const [title, setTitle] = React.useState("");
  const [content, setContent] = React.useState('')
  const [shortCharacterCount, setShortCharacterCount] = React.useState(0)
  const [longCharacterCount, setLongCharacterCount] = React.useState(0)
  const userId = parseInt(window.localStorage.getItem("userId"))
  const isTeacher = window.localStorage.getItem("isTeacher")

  // const getCurrentPostData = async () => {
  //   const dataRequest = await fetch(`get_post/${postId}`, {
  //     method: "GET",
  //     headers: {
  //       'Content-Type': 'application/json'
  //     }
  //   });
  //   return dataRequest.json();
  // }
  //
  // React.useEffect(() => {
  //   getCurrentPostData().then(response => {
  //     // document.getElementById("").setAttribute("defaultValue", response.name);
  //     document.getElementById("").setAttribute("defaultValue", response.content);
  //   })
  // })

  const validateCreateForm = async (formEvent: FormEvent) => {
    formEvent.preventDefault()
    let errors = false
    let errorMessage
    const urlData = document.URL.split("/");
    const viewGroupId = +urlData[urlData.length - 1];

    if (errors) {
      document.getElementById("create-post-error").innerText = errorMessage;
    } else {
      //TODO
      // await fetch(`/group_feed/new_post`, {
      //   method: 'POST',
      //   headers: {
      //     'Content-Type': 'application/json'
      //   },
      //   body: JSON.stringify({"groupId": viewGroupId, "postContent": postData.content})
      // }).then((res) => {
      //   if (res.ok === true) {
      //     window.location.reload()
      //   } else {
      //     document.getElementById("create-post-error").innerText = "You do not have permission to post in this group."
      //   }
      // }).catch((e) => {
      //   console.log("error ", e)
      // })
    }
  }

  const handleCancel = () => {
    document.getElementById("edit-post-modal-open").style.display = "none"
    window.location.reload()
  }

  return (
      <div className={"modal-container"} id={"edit-post-modal-open"}>
        <div className={"modal-edit-post"}>
          <div className={"modal-header"}>
            <div className={"modal-title"}>
              Edit Post
            </div>
            <div className={"modal-close-button"} id={"edit-post-cancel-x"}
                 onClick={handleCancel}>&times;</div>
          </div>
          <div className={"border-line"}/>


          <form onSubmit={(e) => validateCreateForm(e)}>
            <div className="modal-body modal-edit-post-body">
              <label className={"post-title"}>Title:</label>
              <br/>
              <input type="text" name="title-name" className="input-name post-title"
                     required id={`title`} placeholder={title} maxLength={64}
                     // defaultValue={postData.name}
                     onChange={(e) => {
                       setTitle(e.target.value);
                       setShortCharacterCount(e.target.value.length)
                     }}/>
              <span className="title-length" id="title-length">{shortCharacterCount} / 64</span>
              <br/>
            </div>

            <div className={"post-description"}>
              <label className={"settings-description"}>Content:</label>
              <br/>
              <textarea className={"text-area"} id={`edit-post-content`} required
                        cols={50} rows={10} maxLength={4096} onChange={(e) => {
                setContent(e.target.value);
                setLongCharacterCount(e.target.value.length)
              }}/>
              <br/>
            </div>
            <div className="form-error" id="create-post-error"/>


            <div className="modal-buttons">
              <button className="button" id="create-post-save" type={"submit"}>Save</button>
              <button className="button" id="create-post-cancel" onClick={handleCancel}>Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
  );

}