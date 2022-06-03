import React, {useCallback} from "react";
import {observer} from "mobx-react-lite";
import {useProjectStore} from "../store/ProjectStoreProvider";
import {useToasterStore} from "../../../component/toast/internal/ToasterStoreProvider";
import FullCalendar, {EventChangeArg, EventSourceInput} from "@fullcalendar/react";
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

    /**
     * This is an array of events
     */
    const events: EventSourceInput = project.sprints.map(sprint => ({
        id: sprint.id,
        start: sprint.startDate,
        end: sprint.endDate,
        backgroundColor: sprint.colour,
        textColor: getContrast(sprint.colour),
        title: `Sprint ${sprint.orderNumber}: ${sprint.name}`,
        // This hides the time on the event and must be true for drag and drop resizing to be enabled
        allDay: !DatetimeUtils.hasTimeComponent(sprint.startDate) && !DatetimeUtils.hasTimeComponent(sprint.endDate),
    }))


    let deadlineDictionary = new Map();
    let eventDictionary = new Map();
    let milestoneDictionary = new Map();
    let allEventDates = new Set();
    let allDeadlineDate = new Set();
    let allMilestoneDate = new Set();

    /**
     * this method reads all the events and convert them into dictionary where key is the date and value is a list
     * of events
     */
    const eventToDictionary = () => {
        project.events.map((event) => {
            let startDate = new Date(event.startDate);
            while (startDate <= new Date(event.endDate)) {
                startDate.setDate(startDate.getDate() + 1);
                if (eventDictionary.has(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10))))) {
                    const currentEvents = (eventDictionary.get(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10)))));
                    eventDictionary.set(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10))), currentEvents.concat([event]))
                } else {
                    eventDictionary.set(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10))), [event]);
                }
                allEventDates.add(JSON.parse(JSON.stringify(startDate.toISOString().slice(0,10))))
            }

        })
        allEventDates.forEach((eventDate: any) => events.push({
                    id: eventDate + "_ES",
                    start: eventDate,
                    end: eventDate,
                    backgroundColor: "rgba(52, 52, 52, 0.0)",
                    textColor: "black",
                    title: "",
                    allDay: true,
                }))

    }

    /**
     * this method reads all the milestones and convert them into dictionary where key is the date and value is a list
     * of milestones
     */
    const mileStonesToDictionary = () => {
        project.milesStones.map((milestone) => {
            if (milestoneDictionary.has(JSON.parse(JSON.stringify(milestone.endDate.toISOString().slice(0,10))))) {
                const currentMS = (milestoneDictionary.get(JSON.parse(JSON.stringify(milestone.endDate.toISOString().slice(0,10)))));
                milestoneDictionary.set(JSON.parse(JSON.stringify(milestone.endDate.toISOString().slice(0,10))), currentMS.concat([milestone]))
            } else {
                milestoneDictionary.set(JSON.parse(JSON.stringify(milestone.endDate.toISOString().slice(0,10))), [milestone]);
            }
            allMilestoneDate.add(JSON.parse(JSON.stringify(milestone.endDate.toISOString().slice(0,10))))
        })

        allMilestoneDate.forEach((eventDate: any) => events.push({
            id: eventDate + "_MS",
            start: eventDate.toLocaleString(),
            end: eventDate.toLocaleString(),
            backgroundColor: "rgba(52, 52, 52, 0.0)",
            textColor: "black",
            title: "",
            allDay: true,
        }))

    }

    /**
     * this method reads all the deadlines and convert them into dictionary where key is the date and value is a list
     * of deadlines
     */
    const deadLinesToDictionary = () => {
        project.deadlines.map((deadline) => {
            if (deadlineDictionary.has(JSON.parse(JSON.stringify(deadline.endDate.toISOString().slice(0, 10))))) {
                const currentEvents = (deadlineDictionary.get(JSON.parse(JSON.stringify(deadline.endDate.toISOString().slice(0, 10)))));
                deadlineDictionary.set(JSON.parse(JSON.stringify(deadline.endDate.toISOString().slice(0, 10))), currentEvents.concat([deadline]))
            } else {
                deadlineDictionary.set(JSON.parse(JSON.stringify(deadline.endDate.toISOString().slice(0, 10))), [deadline]);
            }
            allDeadlineDate.add(JSON.parse(JSON.stringify(deadline.endDate.toISOString().slice(0,10))))
        })
        console.log(deadlineDictionary)
        allDeadlineDate.forEach((eventDate: any) => events.push({
            id: eventDate + "_DL",
            start: eventDate,
            end: eventDate,
            backgroundColor: "rgba(52, 52, 52, 0.0)",
            textColor: "black",
            title: "",
            allDay: true,
        }))
    }

    mileStonesToDictionary();
    eventToDictionary();
    deadLinesToDictionary()

    /**
     * this method will get the event ID which is the date and see if we have any events / deadlines / milestones for that day.
     * there could be more than one kind of even on the same day to so distinguish between events each event id will have
     * "ES" / "DL" / "MS" suffix at the end of the id separated by "_".
     * @param eventInfo id of the event can be retrieved from evetnInfo of the day.
     */
    function renderEventIcons(eventInfo: any) {
        if (eventInfo.event.title.includes("Sprint")) {
            return (
                <div>
                    <p>{eventInfo.event.title}</p>
                </div>
            )
        } else {
            return (
                <div style={{display:"grid"}}>
                    {
                        eventInfo.event.id.split("_")[1] === "ES"?
                        <div>
                            <span className="material-icons" style={{float: "left"}}>event</span>
                            <p style={{
                                float: "left",
                                margin: "3px 0 0 15px"
                            }}>{eventDictionary.get(eventInfo.event.id.split("_")[0]).length}</p>
                        </div>
                            :
                            ""
                    }
                    {
                        eventInfo.event.id.split("_")[1] === "MS"?
                        <div>
                            <span className="material-icons" style={{float: "left"}}>timer</span>
                            <p style={{float: "left", margin: "3px 0 0 15px"}}>
                                {milestoneDictionary.get(eventInfo.event.id.split("_")[0]).length}
                            </p>
                        </div>
                            :
                            ""
                    }
                    {
                        eventInfo.event.id.split("_")[1] === "DL"?
                        <div>
                            <span className="material-icons" style={{float: "left"}}>flag</span>
                            <p style={{float: "left", margin: "3px 0 0 15px"}}>
                                {deadlineDictionary.get(eventInfo.event.id.split("_")[0]).length}
                            </p>
                        </div>
                            :
                            ""
                    }
                </div>
            )
        }
    }

    return (
        <>
            <h3>{project.name}</h3>
            <FullCalendar
                plugins={[dayGridPlugin, interactionPlugin]}
                initialView="dayGridMonth"
                events={events}
                eventContent={renderEventIcons}
                /* Drag and drop config */
                //The origin of window comes from the Thymeleaf template of "monthly_planner.html".
                editable={!project.sprintsSaving && (window as any) != null ? (window as any).userCanEdit : false} // We shouldn't allow sprints to be updated while we're still trying to save an earlier update, since this could lead to overlapping sprints.
                eventResizableFromStart
                eventOverlap={false}
                eventBorderColor={"transparent"}
                eventConstraint={projectRange}
                eventChange={onSaveDatesCallback}
                /* Calendar config */
                validRange={projectRange}
                height='100vh'
            />
        </>
    )
})