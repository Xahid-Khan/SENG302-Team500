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
import {getContrast} from "../../../util/TextColorUtil";
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

    const [selectedSprint, setSelectedSprint] = React.useState<string>(undefined)
    const [editable, setEditable] = React.useState<boolean>(true)


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

    const events = project.sprints.map(sprint => ({
        id: sprint.id,
        start: sprint.startDate,
        end: sprint.endDate,
        backgroundColor: sprint.colour,
        textColor: getContrast(sprint.colour),
        title: `Sprint ${sprint.orderNumber}: ${sprint.name}`,
        // This hides the time on the event and must be true for drag and drop resizing to be enabled
        allDay: !DatetimeUtils.hasTimeComponent(sprint.startDate) && !DatetimeUtils.hasTimeComponent(sprint.endDate),
    }))

    const eventClick = (eventClickInfo : any) => {
        // alert('Event: ' + eventClickInfo .event.title);
        // alert('Coordinates: ' + eventClickInfo .jsEvent.pageX + ',' + eventClickInfo .jsEvent.pageY);
        // alert('View: ' + eventClickInfo .view.type);
        // console.log(eventClickInfo.event.start)

        // const sprintId = eventClickInfo.event.id
        // const sprint = project.sprints.find((s) => s.id === sprintId)
        // if (sprint !== undefined) {
        //     console.log("click" + sprint.name)
        //     setEditable(eventClickInfo.event.id as string === selectedSprint)
        //     setSelectedSprint(sprint.id);
        // }
        //
        // // change the border color just for fun
        // eventClickInfo.el.style.borderColor = 'red';
    }

    const f = (info: any) => {
        // const sprint = project.sprints.find((s) => s.id === info.event.id)
        // console.log("drag" + sprint.name)
    }

    return (
        <>
            <h3>{project.name + ";" + selectedSprint}</h3>
            <FullCalendar
                plugins={[dayGridPlugin, interactionPlugin]}
                initialView="dayGridMonth"
                events={events}

                /* Drag and drop config */
                //The origin of window comes from the Thymeleaf template of "monthly_planner.html".
                editable={editable && (!project.sprintsSaving && (window as any) != null ? (window as any).userCanEdit : false)} // We shouldn't allow sprints to be updated while we're still trying to save an earlier update, since this could lead to overlapping sprints.
                eventResizableFromStart
                eventOverlap={false}
                eventConstraint={projectRange}
                eventChange={onSaveDatesCallback}
                eventClick={eventClick}
                eventDragStart={f}

                /* Calendar config */
                validRange={projectRange}
                height='100vh'
            />
        </>
    )
})