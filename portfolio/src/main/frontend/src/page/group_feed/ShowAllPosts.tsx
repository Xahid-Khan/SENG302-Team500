import React, {useEffect} from "react";
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
            "highFives": ["James Potter", "Thomas Jefferson", "Lewis Capaldi"],
            "comments": [{
                "name": "James Potter",
                "time": "2022-08-20T10:30:00",
                "content": "Wow you posted before me"
            }]
            },
            {
                "id": 2,
                "name": "James Potter",
                "time": "2022-08-20T12:00:00",
                "content": "What a wonderful day it is",
                "highFives": ["James Potter", "Jim Bean", "Jeff Winger", "Troy Savant"],
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

    const clickHighFive = (id: number) => {
        const button = document.getElementById(`high-five-${id}`)
        button.style.backgroundSize = button.style.backgroundSize === "100% 100%" ? "0 100%" : "100% 100%";
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
                        <div className={"high-fives"}>
                            <div className={"high-five-overlay"}>
                                High Five! <span className={"material-icons"}>sign_language</span>
                            </div>
                            <div className={"high-five"} id={`high-five-${post.id}`} onClick={() => clickHighFive(post.id)}>
                                High Five! <span className={"material-icons"}>sign_language</span>
                            </div>
                            <div className={"high-five-list"}>
                                <div className={"high-five-count"}><span className={"material-icons"} style={{fontSize: 15}}>sign_language</span>{post['highFives'].length}</div>
                                <div className={"border-line high-five-separator"}/>
                                {post['highFives'].map((highFiveName) => (
                                    <div className={"high-five-names"}>{highFiveName}</div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            ))
            }
        </div>
    )
}