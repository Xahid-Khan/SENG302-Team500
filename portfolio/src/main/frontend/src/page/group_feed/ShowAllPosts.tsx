import React, {useEffect} from "react";

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
            }
        </div>
    )
}