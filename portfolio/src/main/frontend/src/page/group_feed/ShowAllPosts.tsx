import React, {FormEvent, useEffect} from "react";
import {DatetimeUtils} from "../../util/DatetimeUtils";

export function ShowAllPosts() {

  const urlData = document.URL.split("?")[0].split("/");
  const viewGroupId = urlData[urlData.length - 1];
  const [newComment, setNewComment] = React.useState("");
  const username = localStorage.getItem("username");
  const [title, setTitle] = React.useState("");
  const [content, setContent] = React.useState('');
  const [editPostId, setEditPostId] = React.useState(-1);
  const [longCharacterCount, setLongCharacterCount] = React.useState(0);
  const isTeacher = localStorage.getItem("isTeacher") === "true";
  const [groupPosts, setGroupPosts] = React.useState({
        "groupId": -1,
        "shortName": "",
        "posts": [{
          "reactions": [],
          "comments": []
        }]
      }
  )

  useEffect(() => {
    if (!isNaN(Number(viewGroupId))) {
      getCurrentGroup().then((result: any) => {
        console.log(result);
        setGroupPosts(result)
      }).catch((error) => {
        console.log(error);
      })
    }
  }, [])

  const getCurrentGroup = async () => {
    const currentGroupResponse = await fetch(`feed_content/${viewGroupId}`, {
      method: "GET",
      "headers": {
        'Content-Type': 'application/json'
      }
    })
    return currentGroupResponse.json()
  }

  const handleCancelEditPost = () => {
    setContent("");
    setTitle("");
    setEditPostId(-1);
    document.getElementById("edit-post-modal-open").style.display = "none";
  }

  const validateCreateForm = async (formEvent: FormEvent) => {
    formEvent.preventDefault()
    let errors = false
    let errorMessage

    if (errors) {
      errorMessage = "Please fill all the fields"
      document.getElementById("create-post-error").innerText = errorMessage;
    } else {
      await fetch(`update_feed/${editPostId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({"groupId": viewGroupId, "postContent": content})
      }).then((res) => {
        if (res.ok === true) {
          window.location.reload();
        } else {
          document.getElementById("create-post-error").innerText = "You do not have permission to post in this group."
        }
      }).catch((e) => {
        console.log("error ", e)
      })
    }
  }

  const openConfirmationModal = (postId: any) => {
    document.getElementById(`modal-delete-open`).style.display = 'block';
    document.getElementById(`modal-delete-x`).addEventListener("click", () => cancelDeleteModal(postId));
    document.getElementById(`modal-delete-cancel`).addEventListener("click", () => cancelDeleteModal(postId));
    document.getElementById(`modal-delete-confirm`).addEventListener("click", () => confirmDeleteModal(postId));
  }

  const cancelDeleteModal = (postId: any) => {
    document.getElementById(`modal-delete-open`).style.display = 'none';
    document.getElementById(`modal-delete-x`).removeEventListener("click", () => cancelDeleteModal(postId));
    document.getElementById(`modal-delete-cancel`).removeEventListener("click", () => cancelDeleteModal(postId));
    document.getElementById(`modal-delete-confirm`).removeEventListener("click", () => confirmDeleteModal(postId));
  }

  const confirmDeleteModal = async (postId: any) => {
    document.getElementById(`modal-delete-x`).removeEventListener("click", () => cancelDeleteModal(postId));
    document.getElementById(`modal-delete-cancel`).removeEventListener("click", () => cancelDeleteModal(postId));
    document.getElementById(`modal-delete-confirm`).removeEventListener("click", () => confirmDeleteModal(postId));

    await fetch(`delete_feed/${postId}`, {
      method: 'DELETE'
    })
    window.location.reload();
  }

  const clickHighFive = async (id: number) => {
    const button = document.getElementById(`high-five-${id}`)
    button.style.backgroundSize = button.style.backgroundSize === "100% 100%" ? "0 100%" : "100% 100%"

    await fetch('post_high_five', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        "postId": id
      })
    });
    await getCurrentGroup().then((result: any) => {
      setGroupPosts(result)
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
      await getCurrentGroup().then((result: any) => {
        setGroupPosts(result)
      })
      document.getElementById(`post-comments-${id}`).scrollTop = document.getElementById(`post-comments-${id}`).scrollHeight;
    }
  }

  const getEditModalData = () => {
    return (<div className={"modal-container"} id={"edit-post-modal-open"}>
      <div className={"modal-edit-post"}>
        <div className={"modal-header"}>
          <div className={"modal-title"}>
            Edit Post
          </div>
          <div className={"modal-close-button"} id={"edit-post-cancel-x"}
               onClick={handleCancelEditPost}>&times;</div>
        </div>
        <div className={"border-line"}/>

        <form onSubmit={(e) => validateCreateForm(e)}>
          <div className="modal-body modal-edit-post-body">
            <label className={"post-title"}>{title}</label>
            <br/>
            <br/>
            <span id={"edit-modal-error"}></span>
          </div>

          <div className={"post-description"}>
            <label className={"settings-description"}>Content:</label>
            <br/>
            <textarea className={"text-area"} id={`edit-post-content`} required
                      defaultValue={content}
                      cols={50} rows={10} maxLength={4096} onChange={(e) => {
              setContent(e.target.value);
              setLongCharacterCount(e.target.value.length)
            }}/>
            <span className="title-length" id="title-length">{longCharacterCount} / 4096</span>
            <br/>
          </div>
          <div className="form-error" id="create-post-error"/>


          <div className="modal-buttons">
            <button className="button" id="edit-post-save" type="submit">Save</button>
            <button className="button" type="reset" id="create-post-cancel"
                    onClick={handleCancelEditPost}>Cancel
            </button>
          </div>
        </form>
      </div>
    </div>)
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
                      <div
                          className={"post-time"}>{DatetimeUtils.timeStringToTimeSince(post.time)}</div>
                    </div>
                    {post.userId == parseInt(localStorage.getItem("userId")) || isTeacher ?
                        <>
                          <div className={"post-edit"}>
                            <span className={"material-icons"}
                                  onClick={() => {
                                    setContent(post.content);
                                    setTitle(post.name);
                                    setEditPostId(post.postId);
                                    document.getElementById("edit-post-modal-open").style.display = 'block';
                                  }}
                                  id={`post-edit-${post.postId}`}>edit</span>
                          </div>
                          <div className={"post-delete"}>
                            <span className={"material-icons"}
                                  onClick={() => {
                                    openConfirmationModal(post.postId);
                                  }}
                                  id={`post-delete-${post.postId}`}>clear</span>
                          </div>
                        </>
                        :
                        ""}
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
                             style={{backgroundSize: post.reactions.includes(username) ? "100% 100%" : "0% 100%"}}
                             onClick={() => clickHighFive(post.postId)}>
                          <span className={"high-five-text"}>High Five!</span> <span
                            className={"material-icons"}>sign_language</span>
                        </div>
                        <div className={"high-five-list"}>
                          <div className={"high-five-count"}><span className={"material-icons"}
                                                                   style={{fontSize: 15}}>sign_language</span>{post.reactions.length}
                          </div>
                          <div className={"border-line high-five-separator"}/>
                          {post.reactions.map((highFiveName: string) => (
                              <div className={"high-five-names"}
                                   key={highFiveName}>{highFiveName}</div>
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
                    <div className={"post-comments"} id={`post-comments-${post.postId}`}>
                      {post.comments.map((comment: any) => (
                              <div className={"post-comment-container"} key={comment.commentId}>
                                <div
                                    className={"comment-name"}>{comment.username} ({DatetimeUtils.timeStringToTimeSince(comment.time)})
                                </div>
                                <div className={"post-comment"}>{comment.content}</div>

                              </div>
                          )
                      )}
                    </div>
                    <form className={"make-comment-container"} onSubmit={(e) => {
                      e.preventDefault();
                      makeComment(post.postId);
                      e.currentTarget.reset();
                    }}>
                      <div className={"input-comment"}>
                        <input type={"text"} className={"input-comment-text"}
                               id={`comment-content-${post.postId}`}
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
        {getEditModalData()}
      </div>
  )
}