import React, {FormEvent, useEffect,Suspense} from "react";
import {DatetimeUtils} from "../../util/DatetimeUtils";
import {GroupFeedPost} from "./GroupFeedPost";

function Loading() {
  return <div className={"raised-card group-post"}>
    <div className={"post-header"}>
      <div className={"post-info"}>
        <div>Loading</div>
      </div>
    </div>
  </div>;
}

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

        <form onSubmit={(e) => {if (longCharacterCount > 0) validateCreateForm(e)}}>
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
              setContent(e.target.value.trim());
              setLongCharacterCount(e.target.value.trim().length);
              if (longCharacterCount > 0) {
                document.getElementById("edit-post-save").removeAttribute("disabled");
              } else {
                document.getElementById("edit-post-save").setAttribute("disabled", "true");
              }
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
                <Suspense fallback={<Loading/>}>
                <GroupFeedPost key={"postBody" + post.postId} post={post} teacher={isTeacher} onClick={() => {
                  setContent(post.content);
                  setTitle(post.name);
                  setEditPostId(post.postId);
                  document.getElementById("edit-post-modal-open").style.display = 'block';
                }} onClick1={() => {
                  openConfirmationModal(post.postId);
                }} username={username} onClick2={() => clickHighFive(post.postId)} element={(highFiveName: string) => (
                    <div className={"high-five-names"}
                         key={highFiveName}>{highFiveName}</div>
                )} onClick3={() => toggleCommentDisplay(post.postId)} element1={(comment: any) => (
                    <div className={"post-comment-container"} key={comment.commentId}>
                      <div
                          className={"comment-name"}>{comment.username} ({DatetimeUtils.timeStringToTimeSince(comment.time)})
                      </div>
                      <div className={"post-comment"}>{comment.content}</div>

                    </div>
                )} onSubmit={(e) => {
                  e.preventDefault();
                  makeComment(post.postId);
                  e.currentTarget.reset();
                }} onChange={(e) => setNewComment(e.target.value.trim())}/>
                </Suspense>
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