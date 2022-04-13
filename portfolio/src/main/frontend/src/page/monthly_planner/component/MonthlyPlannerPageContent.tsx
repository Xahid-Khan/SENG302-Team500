import React from "react";
import {observer} from "mobx-react-lite";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import {useMonthlyPlannerPageStore} from "../store/MonthlyPlannerPageStoreProvider";
import {LoadingDone, LoadingPending} from "../../../util/network/loading_status";
import {LoadingErrorPresenter} from "../../../component/error/LoadingErrorPresenter";
import {ProjectStoreProvider, useProjectStore} from "../store/ProjectStoreProvider";
import {DatetimeUtils} from "../../../util/DatetimeUtils";

export const MonthlyPlannerPageContent: React.FC = observer(() => {
    const pageStore = useMonthlyPlannerPageStore()

    if (pageStore.projectLoadingStatus instanceof LoadingDone) {
        return (
            <ProjectStoreProvider value={pageStore.project}>
                <ProjectCalendar/>
            </ProjectStoreProvider>
        )
    }
    else if (pageStore.projectLoadingStatus instanceof LoadingPending) {
        return (
            <p>Loading...</p>
        )
    }
    else {
        return <LoadingErrorPresenter loadingStatus={pageStore.projectLoadingStatus} onRetry={() => pageStore.fetchProject()}/>
    }
})

const ProjectCalendar: React.FC = observer(() => {
    const project = useProjectStore()

    const projectRange = {
        start: project.startDate,
        end: project.endDate
    }

    return (
        <FullCalendar
            plugins={[ dayGridPlugin, interactionPlugin ]}
            initialView="dayGridMonth"
            events={project.sprints.map(sprint => ({
                id: sprint.sprintId,
                start: sprint.startDate,
                end: sprint.endDate,
                backgroundColor: "orange",
                title: `Sprint ${sprint.orderNumber}: ${sprint.name}`,
                // This hides the time on the event and must be true for drag and drop resizing to be enabled
                allDay: !DatetimeUtils.hasTimeComponent(sprint.startDate) && !DatetimeUtils.hasTimeComponent(sprint.endDate)
            }))}

            /* Drag and drop config */
            editable
            eventResizableFromStart
            eventStartEditable
            eventDurationEditable
            eventOverlap={false}
            eventConstraint={projectRange}
            eventChange={(...args) => console.log(args)}  // TODO: Saving the changes!

            /* Calendar config */
            validRange={projectRange}
            height='100vh'
        />
    )
})