import React, {useEffect, useRef} from "react";
import {DatetimeUtils} from "../../util/DatetimeUtils";

const getSubscriptions = async () => {
  const userId = localStorage.getItem("userId")
  const subscriptionResponse = await fetch(`api/v1/subscribe/${userId}`)
  return subscriptionResponse.json()

}

export function ShowHomeFeed() {

  const [offset, setOffset] = React.useState(-1);
  const [newComment, setNewComment] = React.useState("");

  const loadOptions: any = {
    root: null,
    rootMargin: "0px",
    threshold: 1.0
  }

  const username = localStorage.getItem("username")

  const userId = localStorage.getItem("userId")

  const getPosts = async () => {
    console.log("New posts")
    setOffset(offset + 1)
    const currentGroupResponse = await fetch(`api/v1/posts?offset=` + offset);
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
      "groupId": -1,
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
  });

  const [subscriptions, setSubscriptions] = React.useState([])

  useEffect(() => {
    getPosts().then((result) => {
      setGroupPosts(result)
      console.log(result)
    })
    getSubscriptions().then((result) => {
      setSubscriptions(result)
    })
  }, [])

  const loadRef = useRef(null)

  // With regards to https://dev.to/producthackers/intersection-observer-using-react-49ko
  useEffect(() => {
    const observer = new IntersectionObserver((entries) => {
      const [ entry ] = entries
      if(entry.isIntersecting) {
        getPosts().then((result) => {
          setGroupPosts(result)
          console.log(result)
        })
      }
    }, loadOptions)
    if (loadRef.current) observer.observe(loadRef.current)

    return () => {
      if (loadRef.current) observer.unobserve(loadRef.current)
    }

  }, [loadRef, loadOptions])

  const isTeacher = localStorage.getItem("isTeacher") === "true";

  const clickHighFive = async (id: number) => {
    const button = document.getElementById(`high-five-${id}`)
    button.style.backgroundSize = button.style.backgroundSize === "100% 100%" ? "0 100%" : "100% 100%"

    await fetch('group_feed/post_high_five', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        "postId": id
      })
    });
    // TODO: ??
    await getPosts().then((result) => {
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
      await fetch(`group_feed/add_comment`, {
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
      // TODO ??
      await getPosts().then((result) => {
        setGroupPosts(result)
      })
      document.getElementById(`post-comments-${id}`).scrollTop = document.getElementById(`post-comments-${id}`).scrollHeight;
    }
  }

  const unsubscribeUserToGroup = async (groupId: number) => {
    await fetch(`api/v1/unsubscribe`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        "userId": userId,
        "groupId": groupId
      })
    });
    getSubscriptions().then((result) => {
      setSubscriptions(result);
    })
  }
console.log(subscriptions)
  return (
      <div>
        {groupPosts.groupId != -1 ?
            <>
              <div className={"group-feed-name"}>Welcome!</div>
              {groupPosts.posts.filter((post) => subscriptions.includes(post.groupId)).length === 0 ?
                  <div><h2>Looks like there are no posts! Subscribe to more groups to see there posts
                    here!</h2></div> :
                  <div>{groupPosts.posts.filter((post) => subscriptions.includes(post.groupId)).map((post: any) => (
                      <div className={"raised-card group-post"} key={post.postId}>
                        <div className={"post-header"} key={"postHeader" + post.postId}>
                          <div className={"post-info"} key={"postInfo" + post.postId}>
                            <div>{post.username}</div>
                            <div
                                className={"post-time"}>{DatetimeUtils.timeStringToTimeSince(post.time)}</div>
                          </div>
                          <div className={"post-unsubscribe"}>
                            <button className={"button subscribe-button"}
                                    onClick={() => unsubscribeUserToGroup(post.groupId)}>Unsubscribe
                            </button>
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
                            if (newComment.length > 0) {
                              makeComment(post.postId);
                              e.currentTarget.reset();
                            }
                          }}>
                            <div className={"input-comment"}>
                              <input type={"text"} className={"input-comment-text"}
                                     id={`comment-content-${post.postId}`}
                                     minLength={1}
                                     maxLength={4095}
                                     onChange={(e) => setNewComment(e.target.value.trim())}
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
                  }
                  </div>}
              <div ref={loadRef}/>
            </>
            :
            <div>
              <h1>Loading...</h1>
            </div>
        }
      </div>
  )
}