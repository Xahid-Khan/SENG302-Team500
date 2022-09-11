import React from "react";
import {ToasterRoot} from "../../component/toast/ToasterRoot";
import {ShowAllPosts} from "./ShowAllPosts";

export function GroupFeedPage() {

  const isTeacher = localStorage.getItem("isTeacher") === "true"

  return (
      <ToasterRoot>
        <div className="create-post">
          <div>
            {isTeacher
                ? <button className="button add-group-button" id="add-group"
                          onClick={() => document.getElementById("modal-create-group-open").style.display = "block"}> Create
                  Post
                </button>
                : ""
            }
          </div>
        </div>
        <div className={"raised-card groups-feed"}>
          <ShowAllPosts/>
        </div>
      </ToasterRoot>

  )
}