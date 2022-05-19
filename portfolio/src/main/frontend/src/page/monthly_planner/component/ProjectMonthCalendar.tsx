import React, {useCallback} from "react";
import {observer} from "mobx-react-lite";
import {useProjectStore} from "../store/ProjectStoreProvider";
import {useToasterStore} from "../../../component/toast/internal/ToasterStoreProvider";
import FullCalendar, {EventChangeArg} from "@fullcalendar/react";
import {Toast} from "../../../component/toast/Toast";
import {ToastBase} from "../../../component/toast/ToastBase";
import defaultToastTheme from "../../../component/toast/DefaultToast.module.css";
import {LoadingErrorPresenter} from "../../../component/error/LoadingErrorPresenter";
import {DatetimeUtils} from "../../../util/DatetimeUtils";
import {LoadingPending} from "../../../util/network/loading_status";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";

/**
 * Component that displays a month calendar for the current project and its sprints.
 *
 * The user can drag and drop to move and resize sprints.
 */
export const ProjectMonthCalendar: React.FC = observer(() => {
    const project = useProjectStore()
    const toaster = useToasterStore()

    /**
     * Callback that is triggered when a calendar event is updated by the user. Saves the change to the store, managing
     * toasts to display the status of the operation to the user.
     */
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
        else {
            toaster.dismiss(toastId)
        }
    }, [project])

    const projectRange = {
        start: project.startDate,
        end: project.endDate
    }

    const getEventData = () => {
        let mapOfEvents = new Map();
        let listOfEvents : any[] = [];
        let allEvents = project.events;

        allEvents.forEach((event) =>
            {
                let startDate = new Date(event.startDate);
                while (startDate <= new Date(event.endDate)) {
                    listOfEvents.push({
                        id: event.id,
                        start: JSON.parse(JSON.stringify(startDate)),
                        // allDay: !DatetimeUtils.hasTimeComponent(startDate),
                    })
                    if (mapOfEvents.has(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10))))) {
                        const currentCount = (mapOfEvents.get(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10)))));
                        mapOfEvents.set(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10))), currentCount + 1)
                    } else {
                        mapOfEvents.set(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10))), 1);
                    }
                    startDate.setDate(startDate.getDate() + 1);
                }
            }
        )
        console.log(mapOfEvents);
        mapOfEvents.forEach((val: any, key: any) => events.push({
            id: key,
            start: key,
            end: key,
            backgroundColor: "rgba(52, 52, 52, 0.0)",
            // backgroundColor: "",
            textColor: "black",
            extendedProps: {
                img: "https://pinngle.me/img/logo.png"
            },
            title: val,
            allDay: true,
        }))
    }

    const events = project.sprints.map(sprint => ({
        id: sprint.id,
        start: sprint.startDate,
        end: sprint.endDate,
        backgroundColor: sprint.colour,
        textColor: "white",
        extendedProps: {
            img: "https://pinngle.me/img/logo.png"
        },
        title: `Sprint ${sprint.orderNumber}: ${sprint.name}`,
        // This hides the time on the event and must be true for drag and drop resizing to be enabled
        allDay: !DatetimeUtils.hasTimeComponent(sprint.startDate) && !DatetimeUtils.hasTimeComponent(sprint.endDate),
    }))

    getEventData();
    console.log(events);
    return (
        <>
            <h3>{project.name}</h3>
            <FullCalendar
                plugins={[dayGridPlugin, interactionPlugin]}
                initialView="dayGridMonth"
                events={events}

                /* Drag and drop config */
                //The origin of window comes from the Thymeleaf template of "monthly_planner.html".
                editable={!project.sprintsSaving && (window as any) != null ? (window as any).userCanEdit : false} // We shouldn't allow sprints to be updated while we're still trying to save an earlier update, since this could lead to overlapping sprints.
                eventResizableFromStart
                eventOverlap={false}
                eventConstraint={projectRange}
                eventChange={onSaveDatesCallback}

                /* Calendar config */
                validRange={projectRange}
                height='100vh'
            />
        </>
    )
})