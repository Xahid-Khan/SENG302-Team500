import React, {useEffect} from "react";
import {EditPostModal} from "./EditPostModal";

export function ShowAllPosts() {

  const urlData = document.URL.split("/");
  const viewGroupId = urlData[urlData.length - 1];

  const getCurrentGroup = async () => {
    const currentGroupResponse = await fetch(`feed_content/${viewGroupId}`);
    return currentGroupResponse.json()
  }

  const [groupPosts, setGroupPosts] = React.useState({
    "groupId": -1,
    "shortName": "",
    "posts": []
  })

  useEffect(() => {
    if (!isNaN(Number(viewGroupId))) {
      getCurrentGroup().then((result) => {
        setGroupPosts(result)
      })
    }
  }, [])

  const groupShortName = document.getElementById("group-feed-title").innerText;
  const isTeacher = localStorage.getItem("isTeacher") === "true";

  const updateEditPostData = (postId: any, postContent: any) => {

    document.getElementById("edit-post-content").setAttribute("value", postContent)
    // document.getElementById("edit-post-modal-open")
    document.getElementById("edit-post-modal-open").style.display='block'
  }

  return (
      <div>
        <div className={"group-feed-name"}>{groupShortName} Feed</div>
        {groupPosts.groupId != -1 ?
            groupPosts.posts.map((post) => (
                <div className={"raised-card group-post"} key={post.postId}>
                  <div className={"post-header"}>
                    <div className={"post-info"}>
                      <div>{post.name}</div>
                      <div className={"post-time"}>{post.time}</div>
                    </div>
                    {post.userId == localStorage.getItem("userId") ?
                        <>
                          <div className={"post-edit"}>
                            <span className={"material-icons"}
                                  onClick={() => updateEditPostData(post.postId, post.content)}
                                  id={`post-edit-${post.postId}`}>edit</span>
                          </div>
                          <div className={"post-delete"}>
                            <span className={"material-icons"}
                                  id={`post-delete-${post.postId}`}>clear</span>
                          </div>
                          <EditPostModal postData={post}/>
                        </>
                        :
                        ""}
                  </div>
                  <div className={"post-body"}>{post.content}</div>
                </div>
            ))
            :
            <div className={"raised-card group-post"} key={"-1"}>
              <h3>There are no Posts</h3>
            </div>
        }
      </div>
  )
}