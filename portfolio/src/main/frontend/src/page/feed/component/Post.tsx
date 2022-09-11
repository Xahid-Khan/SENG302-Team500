import React from "react";
import {observer} from "mobx-react-lite";
import {PostStore} from "../store/PostStore";

export const Post = observer(({postStore}) => {
  return (
      <div className={"raised-card home-feed-post"} key={postStore.postId}>
        <div className={"post-user-image"}>
          <img src={"//" + window.localStorage.getItem("imagePath") + postStore.userId.toString()}
               onError={({currentTarget}) => {
                 currentTarget.onerror = null;
                 currentTarget.src = "https://humanimals.co.nz/wp-content/uploads/2019/11/blank-profile-picture-973460_640.png"
               }}
               alt="Profile Photo" height={50} width={50}/>
        </div>
        <div className={"post"}>
          <div className={"post-header"}>
            <div className={"post-info"}>
              <div>{postStore.postTitle}</div>
              {/*TODO: Get Username from UserID*/}
              <div className={"post-time"}>{postStore.postCreated} by {postStore.userId}</div>
            </div>
            <div className={"post-unsubscribe"}>
              <button>Unsubscribe</button>
            </div>
          </div>
          <div className={"post-body"}>{postStore.postContent}</div>
        </div>
      </div>
  )
})