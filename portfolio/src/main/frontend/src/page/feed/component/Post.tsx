import React from "react";

export const Post = () => {
  return (
      <div className={"raised-card home-feed-post"} key={post.id}>
        <div className={"post-user-image"}>
          <img src={"//" + window.localStorage.getItem("imagePath") + post.post_userId.toString()}
               onError={({currentTarget}) => {currentTarget.onerror = null;
                 currentTarget.src="https://humanimals.co.nz/wp-content/uploads/2019/11/blank-profile-picture-973460_640.png"}}
               alt="Profile Photo" height={50} width={50}/>
        </div>
        <div className={"post"}>
          <div className={"post-header"}>
            <div className={"post-info"}>
              <div>{post.name}</div>
              <div className={"post-time"}>{post.time} by {post.groupName}</div>
            </div>
            <div className={"post-unsubscribe"}><button>Unsubscribe</button></div>
          </div>
          <div className={"post-body"}>{post.content}</div>
        </div>
      </div>
  )
}
