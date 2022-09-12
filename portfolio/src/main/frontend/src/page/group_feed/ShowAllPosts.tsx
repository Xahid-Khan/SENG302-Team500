import React, {useEffect} from "react";
import {DatetimeUtils} from "../../util/DatetimeUtils";

export function ShowAllPosts() {

  const urlData = document.URL.split("/");
  const viewGroupId = urlData[urlData.length - 1];

  const getCurrentGroup = async () => {
    const currentGroupResponse = await fetch(`/feed_content/${encodeURIComponent(viewGroupId)}`, {})
    return currentGroupResponse.json()
  }

  const [groupPosts, setGroupPosts] = React.useState({
        "groupId": -11,
        "shortName": "",
        "posts": [{
          "id": -1,
          "userId": -1,
          "username": "",
          "time": "",
          "content": "",
          "reactions": [],
          "comments": [{
            "commentId": -1,
            "userId": -1,
            "username": "",
            "name": "",
            "time": "",
            "content": "",
            "reactions": []
          }]
        }]
      }
  )

  useEffect(() => {
    if (!isNaN(Number(viewGroupId))) {
      getCurrentGroup().then((result) => {
        setGroupPosts(result)
      })
    }
  }, [])

  const groupShortName = document.getElementById("group-feed-title").innerText;
  const isTeacher = localStorage.getItem("isTeacher") === "true";

  const clickHighFive = (id: number) => {
    const button = document.getElementById(`high-five-${id}`)
    button.style.backgroundSize = button.style.backgroundSize === "100% 100%" ? "0 100%" : "100% 100%"
  }

  const toggleCommentDisplay = (id: number) => {
    const commentsContainer = document.getElementById(`comments-container-${id}`)
    console.log(commentsContainer.style.display)
    commentsContainer.style.display = commentsContainer.style.display === "block" ? "none" : "block"
  }

  // const username = "Cody Larsen";
  // const makeComment = (id: number) => {
  //     const comment = document.getElementById(`comment-content-${id}`).getAttribute('value')
  //     // groupPosts['posts'].forEach((post) => {
  //     //     if (post.id === id) {
  //     //         post.comments.push({"name": username,
  //     //             "time": DatetimeUtils.localToDMYWithTime(new Date(Date.now())),
  //     //             "content": comment})
  //     //     }
  //     // })
  //     document.getElementById(`comments-container-${id}`).style.display = "block"
  // }


  return (
      <div>
        <div className={"group-feed-name"}>{groupShortName} Feed</div>
        {groupPosts.groupId != -1 ?
            groupPosts.posts.map((post: any) => (
                <div className={"raised-card group-post"} key={post.id}>
                  <div className={"post-header"} key={"postHeader" + post.id}>
                    <div className={"post-info"} key={"postInfo" + post.id}>
                      <div>{post.username}</div>
                      <div className={"post-time"}>{post.time}</div>
                    </div>
                    {isTeacher ?
                        <div className={"post-delete"}><span
                            className={"material-icons"}>clear</span></div> : ""}
                  </div>
                  <div className={"post-body"} key={"postBody" + post.id}>{post.content}</div>
                  <div className={"border-line"}/>
                  <div className={"post-footer"}>
                    <div className={"high-five-container"}>
                      <div className={"high-five-overlay"}>
                        <span className={"high-five-text"}>High Five!</span> <span
                          className={"material-icons"}>sign_language</span>
                      </div>
                      <div className={"high-five"} id={`high-five-${post.id}`}
                           onClick={() => clickHighFive(post.id)}>
                        <span className={"high-five-text"}>High Five!</span> <span
                          className={"material-icons"}>sign_language</span>
                      </div>
                    </div>
                    <div className={"comments-icon-container"}>
                      <div className={"comments-select"}
                           onClick={() => toggleCommentDisplay(post.id)}>
                        <span className={"comments-select-text"}>Comments</span> <span
                          className={"material-icons"}>mode_comment</span>
                      </div>
                    </div>
                  </div>
                  <div className={"comments-container"} id={`comments-container-${post.id}`}>
                    <div className={"border-line"}/>
                    <div className={"post-comments"}>
                      {post.comments.map((comment: any) => (
                              <div className={"post-comment-container"}>
                                <div className={"comment-name"}>{comment.username} ({comment.time})
                                </div>
                                <div className={"post-comment"}>{comment.content}</div>

                              </div>
                          )
                      )}
                    </div>
                    <form className={"make-comment-container"}>
                      {/*onSubmit={(e) => {e.preventDefault(); makeComment(post.id)}}*/}
                      <div className={"input-comment"}>
                        <input type={"text"} className={"input-comment-text"}
                               id={`comment-content-${post.id}`}
                               onChange={(e) => document.getElementById(`comment-content-${post.id}`).setAttribute('value', e.target.value)}
                               placeholder={"Comment on post..."}/>
                      </div>
                      <div className={"submit-comment"}>
                        <button className={"button submit-comment-button"} type={"submit"}>Add
                          comment
                        </button>
                      </div>
                    </form>
                  </div>
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