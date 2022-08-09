import * as React from 'react';

import {ToasterRoot} from "../../component/toast/ToasterRoot";
import {ShowAllGroups} from "./ShowAllGroups";

/**
 * The root of the GroupPage. This does a few jobs:
 * 1. Construct the GroupPage and wrap the whole page in a Provider so it can be used by children.
 * 2. Wrap the entire page in a PageLayout.
 * 3. Place the PageContent component inside that Layout component.
 */
export const GroupPage = () => {
    return (

            <ToasterRoot>
                <div className="add-group">
                    <div>
                        <button className="button add-group-button" id="add-group" data-privilege="teacher"> Add
                            Group
                        </button>
                    </div>
                </div>
                <div className={"raised-card groups"}>
                    <h1>Groups</h1>
                    <ShowAllGroups/>
                </div>


            </ToasterRoot>

    )
}