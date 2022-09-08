import React from "react"
import {PageLayout} from "../../../component/layout/PageLayout"
import {ToasterRoot} from "../../../component/toast/ToasterRoot";
import {ShowHomeFeed} from "./show_home_feed";

/**
 * TODO CHANGE! The root of the HomeFeedPage. This does a few jobs:
 * 1. Construct the MonthlyPlannerPageStore and wrap the whole page in a Provider so it can be used by children.
 * 2. Wrap the entire page in a PageLayout.
 * 3. Place the PageContent component inside that Layout component.
 */
export const HomeFeedPage: React.FC = () => {
  return (
      <ToasterRoot>
        <PageLayout>
          <div className={"raised-card groups-feed"}>
            <ShowHomeFeed/>
          </div>
        </PageLayout>
      </ToasterRoot>
  )
}