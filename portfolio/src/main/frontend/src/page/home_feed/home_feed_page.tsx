import React from "react";
import {ToasterRoot} from "../../component/toast/ToasterRoot";
import {ShowHomeFeed} from "./show_home_feed";

export function HomeFeedPage() {

    // const isStudent = localStorage.getItem("isStudent") === "true"

    return (
        <ToasterRoot>
            {/*<div className="create-post">*/}
            {/*    <div>*/}
            {/*        {isStudent ? "" :*/}
            {/*            <button className="button add-group-button" id="add-group"*/}
            {/*                    onClick={() => document.getElementById("modal-create-group-open").style.display = "block"}> Create*/}
            {/*                Post*/}
            {/*            </button>*/}
            {/*        }*/}
            {/*    </div>*/}
            {/*</div>*/}
            <div className={"raised-card groups-feed"}>
                <ShowHomeFeed/>
            </div>
        </ToasterRoot>

    )
}