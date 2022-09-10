import React from "react";
import {CreatePost} from "./CreatePost";

export function CreatePostModal() {

  const handleCancel = () => {
    document.getElementById("modal-create-post-open").style.display = "none"
    window.location.reload()
  }

  return (
      <div className={"modal-container"} id={"modal-create-post-open"}>
        <div className={"modal-create-post"}>
          <div className={"modal-header"}>
            <div className={"modal-title"}>
              Create post
            </div>
            <div onClick={() => handleCancel()} className={"modal-close-button"}
                 id={"create-post-x"}>&times;</div>
          </div>
          <div className={"border-line"}/>
          <div className="modal-body create-post-body">
            <CreatePost/>
          </div>
        </div>
      </div>
  );

}