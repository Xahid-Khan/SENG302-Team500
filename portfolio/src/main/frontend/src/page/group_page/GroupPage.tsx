import React from "react"
import {PageLayout} from "../../component/layout/PageLayout"

import {ToasterRoot} from "../../component/toast/ToasterRoot";

/**
 * The root of the MonthlyPlannerPage. This does a few jobs:
 * 1. Construct the GroupPage and wrap the whole page in a Provider so it can be used by children.
 * 2. Wrap the entire page in a PageLayout.
 * 3. Place the PageContent component inside that Layout component.
 */
export const GroupPage = () => {
    return (

            <ToasterRoot>
                <PageLayout>
                    <div>
                        <h1>Test</h1>
                    </div>
                </PageLayout>
            </ToasterRoot>

    )
}