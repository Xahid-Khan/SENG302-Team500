import React, {useCallback} from "react";
import {observer} from "mobx-react-lite";
import FullCalendar, {EventChangeArg} from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import {useMonthlyPlannerPageStore} from "../store/MonthlyPlannerPageStoreProvider";
import {LoadingDone, LoadingPending} from "../../../util/network/loading_status";
import {LoadingErrorPresenter} from "../../../component/error/LoadingErrorPresenter";
import {ProjectStoreProvider, useProjectStore} from "../store/ProjectStoreProvider";
import {DatetimeUtils} from "../../../util/DatetimeUtils";
import {useToasterStore} from "../../../component/toast/internal/ToasterStoreProvider";
import {Toast} from "../../../component/toast/Toast";
import {ToastBase} from "../../../component/toast/ToastBase";
import defaultToastTheme from "../../../component/toast/DefaultToast.module.css";

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
    const toaster = useToasterStore()

    const onSaveDatesCallback = useCallback((evt: EventChangeArg) => {
        const toastId = toaster.show(() => (
            <Toast title="Saving Sprint..." dismissable={false}/>
        ), {
            timeout: Infinity
        })

        const sprintId = evt.event.id
        const sprint = project.sprints.find((s) => s.id === sprintId)
        if (sprint !== undefined) {
            sprint.setDates(evt.event.start, evt.event.end)
                .then(
                    () => toaster.dismiss(toastId),
                    (err) => toaster.replace(toastId, () => (
                        <ToastBase themes={[defaultToastTheme]}>
                            <LoadingErrorPresenter loadingStatus={err} onRetry={() => {
                                toaster.dismiss(toastId)
                                onSaveDatesCallback(evt)
                            }}/>
                        </ToastBase>
                    ), {timeout: Infinity})
                )

        }
    }, [project])

    const projectRange = {
        start: project.startDate,
        end: project.endDate
    }

    console.log("Re-rendering")
    console.log(project.sprints)

    const events = project.sprints.map(sprint => ({
        id: sprint.id,
        start: sprint.startDate,
        end: sprint.endDate,
        backgroundColor: "orange",
        title: `Sprint ${sprint.orderNumber}: ${sprint.name}`,
        // This hides the time on the event and must be true for drag and drop resizing to be enabled
        allDay: !DatetimeUtils.hasTimeComponent(sprint.startDate) && !DatetimeUtils.hasTimeComponent(sprint.endDate),
        editable: !(sprint.saveSprintStatus instanceof LoadingPending)  // We shouldn't allow sprints to be updated while we're still trying to save the last update
    }))

    return (
        <FullCalendar
            plugins={[ dayGridPlugin, interactionPlugin ]}
            initialView="dayGridMonth"
            events={events}

            /* Drag and drop config */
            editable
            eventResizableFromStart
            eventStartEditable
            eventDurationEditable
            eventOverlap={false}
            eventConstraint={projectRange}
            eventChange={onSaveDatesCallback}

            /* Calendar config */
            validRange={projectRange}
            height='100vh'
        />
    )
})