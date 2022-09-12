import React, {useEffect} from "react";
import {DatetimeUtils} from "../../util/DatetimeUtils";

export function ShowAllPosts() {

    const urlData = document.URL.split("/");
    const viewGroupId = urlData[urlData.length-1];

    const getCurrentGroup = async ()  => {
        const currentGroupResponse = await fetch(`/feed_content/${encodeURIComponent(viewGroupId)}`, {})
        return currentGroupResponse.json()
    }

    const [groupPosts, setGroupPosts] = React.useState({
        "groupId": -1,
        "shortName": "",
        "posts": []
    })

    useEffect(() => {
        if (!isNaN(+viewGroupId)) {
            getCurrentGroup().then((result) => {
                setGroupPosts(result)
            })
        }
    }, [])

    const groupShortName = document.getElementById("group-feed-title").innerText;
    const isTeacher = localStorage.getItem("isTeacher") === "true";

    return(
        <div>
            <div className={"group-feed-name"}>{groupShortName} Feed</div>
            {groupPosts.posts.length > 0 ?
                groupPosts.posts.map((post) => (
                    <div className={"raised-card group-post"} key={post.id}>
                        <div className={"post-header"}>
                            <div className={"post-info"}>
                                <div>{post.name}</div>
                                <div className={"post-time"}>{post.time}</div>
                            </div>
                            {isTeacher ?
                            <div className={"post-delete"}><span className={"material-icons"}>clear</span></div> : ""}
                        </div>
                        <div className={"post-body"}>{post.content}</div>
                    </div>
                ))
                :
                <div className={"raised-card group-post"} key={"-1"}>
                    <h3>There are no Posts</h3>
                </div>
            ))
            }

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
        </div>
    )
}