import React, {FormEvent, useEffect} from "react";
import {PostAndCommentContainer} from "./PostAndCommentContainer";
import {EditPostDataModal} from "./EditPostDataModal";
import {Tooltip} from "@mui/material";

export function ShowAllPosts() {

  const urlData = document.URL.split("?")[0].split("/");
  const viewGroupId = urlData[urlData.length - 1];
  const userId = parseInt(localStorage.getItem("userId"));
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
        "isSubscribed": false,
        "isMember": false,
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

    if (newComment.length != 0 && newComment.length < 4096) {
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

  const subscribeUserToGroup = async (groupId: number) => {
    await fetch(`../api/v1/subscribe`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        "userId": userId,
        "groupId": groupId
      })
    });
    getCurrentGroup().then((response) => {
      setGroupPosts(response);
    })
  }

  const unsubscribeUserToGroup = async (groupId: number) => {
    await fetch(`../api/v1/unsubscribe`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        "userId": userId,
        "groupId": groupId
      })
    });
    getCurrentGroup().then((response) => {
      setGroupPosts(response);
    })
  }


  return (
      <div>
        {
          groupPosts.groupId !== -1 ?
              <>
                <div className={"group-feed-name"}>{groupPosts.shortName} Feed</div>
                {!groupPosts.isMember ?
                    <>
                      {
                        groupPosts.isSubscribed ?
                            <button className={"feed-Sub-Button"}
                                    onClick={() => unsubscribeUserToGroup(groupPosts.groupId)}>Unsubscribe</button>
                            :
                            <button className={"feed-Sub-Button"}
                                    onClick={() => subscribeUserToGroup(groupPosts.groupId)}>Subscribe</button>
                      }
                    </>
                    :
                    <>
                      <Tooltip title={"You cannot unsubscribe if you're a member of the group."}>
                        <span className={"feed-Sub-Button"}
                              style={{padding: "5px", marginTop: "-35px"}}>
                          <button disabled={true}>Unsubscribe</button>
                        </span>
                      </Tooltip>
                    </>
                }
                {
                  groupPosts.posts.length > 0 ?
                      groupPosts.posts.map((post: any) => (
                          <PostAndCommentContainer post={post} isTeacher={isTeacher}
                                                   setContent={setContent}
                                                   setLongCharacterCount={setLongCharacterCount}
                                                   setTitle={setTitle}
                                                   setEditPostId={setEditPostId}
                                                   clickHighFive={clickHighFive}
                                                   openConfirmationModal={openConfirmationModal}
                                                   toggleCommentDisplay={toggleCommentDisplay}
                                                   makeComment={makeComment}
                                                   setNewComment={setNewComment}
                                                   username={username}
                          />))
                      :
                      <div className={"raised-card group-post"} key={"-1"}>
                        <h3>There are no posts</h3>
                      </div>
                }
                <EditPostDataModal handleCancelEditPost={handleCancelEditPost}
                                   longCharacterCount={longCharacterCount}
                                   validateCreateForm={validateCreateForm}
                                   title={title}
                                   content={content}
                                   setContent={setContent}
                                   setLongCharacterCount={setLongCharacterCount}

                />
              </>
              :
              <div>
                <h1>Loading...</h1>
              </div>
        }
      </div>
  )
}