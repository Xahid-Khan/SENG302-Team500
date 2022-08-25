import React from "react";

export function ShowHomeFeed() {
    const groupPosts = {
        "posts": [{
            "id": 1,
            "post_userId": 4,
            "name": "Exam Announcement",
            "groupName": "A New Group",
            "time": "2022-08-20T10:00:00",
            "content": "This is my first post",
            "highFives": 5,
            "comments": [{
                "name": "James Potter",
                "time": "2022-08-20T10:30:00",
                "content": "Wow you posted before me"
            }]
        },
            {
                "id": 2,
                "post_userId": 3,
                "name": "Deadline Reminder",
                "groupName": "An Old Group",
                "time": "2022-08-20T12:00:00",
                "content": "What a wonderful day it is",
                "highFives": 3,
                "comments": [{
                    "name": "John Snow",
                    "time": "2022-08-20T11:30:00",
                    "content": "Cool post"
                }]
            }
        ]
    }

    return(
        <div>
            {
                groupPosts.posts.map((post) => (
                    <div className={"raised-card home-feed-post"} key={post.id}>
                        <div className={"post-user-image"}>
                            <img src={"//" + window.localStorage.getItem("imagePath") + post.post_userId.toString()}
                                 onError={({currentTarget}) => {currentTarget.onerror = null;
                                            currentTarget.src="https://humanimals.co.nz/wp-content/uploads/2019/11/blank-profile-picture-973460_640.png"}}
                                 alt="Profile Photo" height={50} width={50}/>
                        </div>
                        <div className={"post"}>
                            <div className={"post-header"}>
                                <div className={"post-info"}>
                                    <div>{post.name}</div>
                                    <div className={"post-time"}>{post.time} by {post.groupName}</div>
                                </div>
                                <div className={"post-unsubscribe"}><button>Unsubscribe</button></div>
                            </div>
                            <div className={"post-body"}>{post.content}</div>
                        </div>
                    </div>
                ))
            }
        </div>
    )
}