import React from "react";
import {DatetimeUtils} from "../../util/DatetimeUtils";

export function ShowAllPosts() {

    // const getCurrentGroup = async ()  => {
    //     // const currentGroupResponse = await fetch(`api/v1/groups/posts/${viewGroupId}`)
    //     return currentGroupResponse.json()
    // // }
    //
    // const [group, setGroup] = React.useState({
    //     "id": -1,
    //     "longName": "",
    //     "shortName": "",
    //     "users": [],
    //     "posts":
    // })
    //
    // useEffect(() => {
    //     getCurrentGroup().then((result) => {
    //         setGroup(result)
    //     })
    // }, [])

    const groupPosts = {
        "groupId": 1,
        "shortName": "A new Group",
        "posts": [{
            "id": 1,
            "name": "John Snow",
            "time": "2022-08-20T10:00:00",
            "content": "This is my first post",
            "highFives": 5,
            "comments": [{
                "name": "James Potter",
                "time": "2022-08-20T10:30:00",
                "content": "Wow you posted before me"
                },
                {
                    "name": "Richie Mccaw",
                    "time": "2022-08-22T18:30:00",
                    "content": "Welcome to the club."
                }]
            },
            {
                "id": 2,
                "name": "James Potter",
                "time": "2022-08-20T12:00:00",
                "content": "What a wonderful day it is",
                "highFives": 5,
                "comments": [{
                    "name": "John Snow",
                    "time": "2022-08-20T11:30:00",
                    "content": "Cool post"
                }]
            }
        ]
    }

    const groupShortName = document.getElementById("group-feed-title").innerText;

    const isStudent = localStorage.getItem("isStudent") === "true";

    const username = "Cody Larsen"

    const clickHighFive = (id: number) => {
        const button = document.getElementById(`high-five-${id}`)
        button.style.backgroundSize = button.style.backgroundSize === "100% 100%" ? "0 100%" : "100% 100%"
    }

    const toggleCommentDisplay = (id: number) => {
        const commentsContainer = document.getElementById(`comments-container-${id}`)
        console.log(commentsContainer.style.display)
        commentsContainer.style.display = commentsContainer.style.display === "block" ? "none" : "block"
    }

    const makeComment = (id: number) => {
        const comment = document.getElementById(`comment-content-${id}`).getAttribute('value')
        groupPosts['posts'].forEach((post) => {
            if (post.id === id) {
                post.comments.push({"name": username,
                "time": DatetimeUtils.localToDMYWithTime(new Date(Date.now())),
                "content": comment})
            }
        })
        document.getElementById(`comments-container-${id}`).style.display = "block"
    }

    return(
        <div>
            <div className={"group-feed-name"}>{groupShortName} Feed</div>
            {groupPosts.posts.map((post) => (
                <div className={"raised-card group-post"} key={post.id}>
                    <div className={"post-header"}>
                        <div className={"post-info"}>
                            <div>{post.name}</div>
                            <div className={"post-time"}>{post.time}</div>
                        </div>
                        {isStudent ?
                        <div className={"post-delete"}><span className={"material-icons"}>clear</span></div> : ""}
                    </div>
                    <div className={"post-body"}>{post.content}</div>
                    <div className={"border-line"}/>
                    <div className={"post-footer"}>
                        <div className={"high-five-container"}>
                            <div className={"high-five-overlay"}>
                                <span className={"high-five-text"}>High Five!</span> <span className={"material-icons"}>sign_language</span>
                            </div>
                            <div className={"high-five"} id={`high-five-${post.id}`} onClick={() => clickHighFive(post.id)}>
                                <span className={"high-five-text"}>High Five!</span> <span className={"material-icons"}>sign_language</span>
                            </div>
                        </div>
                        <div className={"comments-icon-container"}>
                            <div className={"comments-select"} onClick={() => toggleCommentDisplay(post.id)}>
                                <span className={"comments-select-text"}>Comments</span> <span className={"material-icons"}>mode_comment</span>
                            </div>
                        </div>
                    </div>
                    <div className={"comments-container"} id={`comments-container-${post.id}`}>
                        <div className={"border-line"}/>
                        <div className={"post-comments"}>
                            {post.comments.map((comment) => (
                                <div className={"post-comment-container"}>
                                <div className={"comment-name"}>{comment.name}</div>
                                <div className={"post-comment"}>{comment.content}</div>
                                </div>
                                )
                            )}
                        </div>
                        <form className={"make-comment-container"} onSubmit={(e) => {e.preventDefault(); makeComment(post.id)}}>
                            <div className={"input-comment"}>
                                <input type={"text"} className={"input-comment-text"} id={`comment-content-${post.id}`} onChange={(e) => document.getElementById(`comment-content-${post.id}`).setAttribute('value', e.target.value)} placeholder={"Comment on post..."}/>
                            </div>
                            <div className={"submit-comment"}>
                                <button className={"button submit-comment-button"} type={"submit"}>Add comment</button>
                            </div>
                        </form>
                    </div>
                </div>
            ))
            }
        </div>
    )
}