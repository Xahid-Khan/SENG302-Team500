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

/**
 * Get the contrasting color for any hex color
 * (c) 2021 Chris Ferdinandi, MIT License, https://gomakethings.com
 * Derived from work by Brian Suda, https://24ways.org/2010/calculating-color-contrast/
 * @param  {String} A hexcolor value
 * @return {String} The contrasting color (black or white)
 */
function getContrast (hexcolor: string): string{
    // If a leading # is provided, remove it
    if (hexcolor.slice(0, 1) === '#') {
        hexcolor = hexcolor.slice(1);
    }

    // If a three-character hexcode, make six-character
    if (hexcolor.length === 3) {
        hexcolor = hexcolor.split('').map(function (hex) {
            return hex + hex;
        }).join('');
    }

    // Convert to RGB value
    let r = parseInt(hexcolor.substring(0,2),16);
    let g = parseInt(hexcolor.substring(2,4),16);
    let b = parseInt(hexcolor.substring(4,6),16);

    // Get YIQ ratio
    let yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;

    // Check contrast
    return (yiq >= 128) ? 'black' : 'white';
}