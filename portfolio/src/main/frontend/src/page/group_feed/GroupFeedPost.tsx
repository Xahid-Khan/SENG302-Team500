import {DatetimeUtils} from "../../util/DatetimeUtils";
import React from "react";

export function GroupFeedPost(props: { post: any, teacher: boolean, onClick: () => void, onClick1: () => void, username: string, onClick2: () => Promise<void>, element: (highFiveName: string) => JSX.Element, onClick3: () => void, element1: (comment: any) => JSX.Element, onSubmit: (e: any) => void, onChange: (e: any) => void }) {
    return <div className={"raised-card group-post"}>
        <div className={"post-header"}>
            <div className={"post-info"}>
                <div>{props.post.username}</div>
                <div
                    className={"post-time"}>{DatetimeUtils.timeStringToTimeSince(props.post.time)}</div>
            </div>
            {props.post.userId == parseInt(localStorage.getItem("userId")) || props.teacher ?
                <>
                    <div className={"post-edit"}>
                            <span className={"material-icons"}
                                  onClick={props.onClick}
                                  id={`post-edit-${props.post.postId}`}>edit</span>
                    </div>
                    <div className={"post-delete"}>
                            <span className={"material-icons"}
                                  onClick={props.onClick1}
                                  id={`post-delete-${props.post.postId}`}>clear</span>
                    </div>
                </>
                :
                ""}
        </div>
        <div className={"post-body"}>{props.post.content}</div>
        <div className={"border-line"}/>
        <div className={"post-footer"}>
            <div className={"high-five-container"}>
                <div className={"high-fives"}>
                    <div className={"high-five-overlay"}>
                        <span className={"high-five-text"}>High Five!</span> <span
                        className={"material-icons"}>sign_language</span>
                    </div>
                    <div className={"high-five"} id={`high-five-${props.post.postId}`}
                         style={{backgroundSize: props.post.reactions.includes(props.username) ? "100% 100%" : "0% 100%"}}
                         onClick={props.onClick2}>
                        <span className={"high-five-text"}>High Five!</span> <span
                        className={"material-icons"}>sign_language</span>
                    </div>
                    <div className={"high-five-list"}>
                        <div className={"high-five-count"}><span className={"material-icons"}
                                                                 style={{fontSize: 15}}>sign_language</span>{props.post.reactions.length}
                        </div>
                        <div className={"border-line high-five-separator"}/>
                        {props.post.reactions.map(props.element)}
                    </div>
                </div>
            </div>
            <div className={"comments-icon-container"}>
                <div className={"comments-select"}
                     onClick={props.onClick3}>
                    <span className={"comments-select-text"}>Comments</span> <span
                    className={"material-icons"}>mode_comment</span>
                </div>
            </div>
        </div>
        <div className={"comments-container"} id={`comments-container-${props.post.postId}`}>
            <div className={"border-line"}/>
            <div className={"post-comments"} id={`post-comments-${props.post.postId}`}>
                {props.post.comments.map(props.element1
                )}
            </div>
            <form className={"make-comment-container"} onSubmit={props.onSubmit}>
                <div className={"input-comment"}>
                    <input type={"text"} className={"input-comment-text"}
                           id={`comment-content-${props.post.postId}`}
                           onChange={props.onChange}
                           placeholder={"Comment on post..."}/>
                </div>
                <div className={"submit-comment"}>
                    <button className={"button submit-comment-button"} type={"submit"}
                            id={`comment-submit-${props.post.postId}`}>
                        Add comment
                    </button>
                </div>
            </form>
        </div>
    </div>;
}