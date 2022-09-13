import React, {useEffect} from "react";

export function ShowAllPosts() {

  const urlData = document.URL.split("?")[0].split("/");
  const viewGroupId = urlData[urlData.length - 1];

  const [newComment, setNewComment] = React.useState("");

  const userId = localStorage.getItem("userId")

  const getCurrentGroup = async () => {
    const currentGroupResponse = await fetch(`feed_content/${viewGroupId}`);
    return currentGroupResponse.json()
  }

  const [groupPosts, setGroupPosts] = React.useState({
        "groupId": -1,
        "shortName": "",
        "posts": [{
          "postId": -1,
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
  }, [groupPosts.groupId])

  const isTeacher = localStorage.getItem("isTeacher") === "true";

  const clickHighFive = (id: number) => {
    const button = document.getElementById(`high-five-${id}`)
    button.style.backgroundSize = button.style.backgroundSize === "100% 100%" ? "0 100%" : "100% 100%"

    groupPosts.posts.forEach((post) => {
      if (post.postId === id) {
        post.reactions.push(parseInt(userId))
      }
    })
  }

  const toggleCommentDisplay = (id: number) => {
    const commentsContainer = document.getElementById(`comments-container-${id}`)
    commentsContainer.style.display = commentsContainer.style.display === "block" ? "none" : "block"
  }

  const makeComment = async (id: number) => {
    setNewComment(document.getElementById(`comment-content-${id}`).getAttribute('value'));

    if (newComment.length != 0) {
      await fetch(`add_comment`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          "userId": localStorage.getItem("userId"),
          "postId": id,
          "comment": newComment
        })
      });
      setNewComment("");
      await getCurrentGroup().then((result) => {
        setGroupPosts(result)
      })
    }
  }


  return (
      <div>
        <div className={"group-feed-name"}>{groupPosts.shortName} Feed</div>
        {groupPosts.groupId != -1 ?
            groupPosts.posts.map((post: any) => (
                <div className={"raised-card group-post"} key={post.postId}>
                  <div className={"post-header"} key={"postHeader" + post.postId}>
                    <div className={"post-info"} key={"postInfo" + post.postId}>
                      <div>{post.username}</div>
                      <div className={"post-time"}>{post.time}</div>
                    </div>
                    {isTeacher ?
                        <div className={"post-delete"}><span
                            className={"material-icons"}>clear</span></div> : ""}
                  </div>
                  <div className={"post-body"} key={"postBody" + post.postId}>{post.content}</div>
                  <div className={"border-line"}/>
                  <div className={"post-footer"}>
                    <div className={"high-five-container"}>
                      <div className={"high-fives"}>
                      <div className={"high-five-overlay"}>
                        <span className={"high-five-text"}>High Five!</span> <span
                          className={"material-icons"}>sign_language</span>
                      </div>
                      <div className={"high-five"} id={`high-five-${post.postId}`}
                           onClick={() => clickHighFive(post.postId)}>
                        <span className={"high-five-text"}>High Five!</span> <span
                          className={"material-icons"}>sign_language</span>
                      </div>
                        <div className={"high-five-list"}>
                          <div className={"high-five-count"}><span className={"material-icons"} style={{fontSize: 15}}>sign_language</span>{post.reactions.length}</div>
                          <div className={"border-line high-five-separator"}/>
                          {post.reactions.map((highFiveName: string) => (
                              <div className={"high-five-names"}>{highFiveName}</div>
                          ))}
                        </div>
                      </div>
                    </div>
                    <div className={"comments-icon-container"}>
                      <div className={"comments-select"}
                           onClick={() => toggleCommentDisplay(post.postId)}>
                        <span className={"comments-select-text"}>Comments</span> <span
                          className={"material-icons"}>mode_comment</span>
                      </div>
                    </div>
                  </div>
                  <div className={"comments-container"} id={`comments-container-${post.postId}`}>
                    <div className={"border-line"}/>
                    <div className={"post-comments"}>
                      {post.comments.map((comment: any) => (
                              <div className={"post-comment-container"} key={comment.commentId}>
                                <div className={"comment-name"}>{comment.username} ({comment.time})
                                </div>
                                <div className={"post-comment"}>{comment.content}</div>

                              </div>
                          )
                      )}
                    </div>
                    <form className={"make-comment-container"} onSubmit={(e) => {
                      e.preventDefault();
                      makeComment(post.postId)
                    }}>
                      <div className={"input-comment"}>
                        <input type={"text"} className={"input-comment-text"}
                               id={`comment-content-${post.postId}`}
                               value={newComment}
                               onChange={(e) => setNewComment(e.target.value)}
                               placeholder={"Comment on post..."}/>
                      </div>
                      <div className={"submit-comment"}>
                        <button className={"button submit-comment-button"} type={"submit"}
                                id={`comment-submit-${post.postId}`}>
                          Add comment
                        </button>
                      </div>
                    </form>
                  </div>
                </div>

            ))
            :
            <div className={"raised-card group-post"} key={"-1"}>
              <h3>There are no posts</h3>
            </div>
        }
      </div>
  )
}