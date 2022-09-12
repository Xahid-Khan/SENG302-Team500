import React from "react";
import {ToasterRoot} from "../../component/toast/ToasterRoot";
import {ShowAllPosts} from "./ShowAllPosts";

export function GroupFeedPage() {

    const isStudent = localStorage.getItem("isStudent") === "true"

    return (
        <ToasterRoot>
            <div className="create-post">
                <div>
                    {isStudent ? "" :
                        <button className="button add-group-button" id="add-group"
                                onClick={() => document.getElementById("modal-create-group-open").style.display = "block"}> Create
                            Post
                        </button>
                    }
                </div>
            </div>
            <div className={"raised-card groups-feed"}>
                <ShowAllPosts/>
            </div>
        </ToasterRoot>

    )
}