import React from "react";
import {PostStore} from "../store/PostStore";
import {Post} from "../component/Post";
import {PostContract} from "../../../contract/PostContract";

export function ShowHomeFeed() {

  // const getHomeFeedPosts = async () => {
  //     const homeFeedPosts = await fetch("api/v1/home_feed")
  //     return homeFeedPosts;
  // }
  //
  // const [homeFeed, setHomeFeed] = React.useState({"posts" : [{
  //         "id": 11,
  //         "post_userId": -1,
  //         "name": "",
  //         "groupName": "",
  //         "time": "",
  //         "content": "",
  //         "highFives": -1,
  //         "comments": [{
  //             "name": "",
  //             "time": "",
  //             "content": "",
  //         }]
  //     }]})
  //
  // useEffect(() => {
  //     getHomeFeedPosts().then((response) => {
  //         setHomeFeed(response);
  //     })
  // }, [])

  /*const homeFeed = {
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
  }*/

  /*const posts: PostContract[] = {
    postId: "1",
    created: "2022-08-20T10:00:00",


  };*/

  return (
      <div>
        {
          posts.map((post) => (
              <Post postStore={new PostStore(post)}/>
          ))
        }
      </div>
  )
}