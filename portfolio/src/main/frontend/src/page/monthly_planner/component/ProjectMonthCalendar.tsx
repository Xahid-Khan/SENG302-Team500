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
import ReactTooltip from "react-tooltip";
import {SprintStore} from "../store/SprintStore";

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

    let deadlineDictionary = new Map();
    let eventDictionary = new Map();
    let milestoneDictionary = new Map();
    let allEventDates = new Set();

    /**
     * This method reads all the events (Events, Milestones, and Deadlines) and create a dictionary where key is the date
     * and value is a list of events on that day.
     * @param iconEvents Events that we would like to count
     * @param dict an empty dictionary where the results will be stored and updated.
     * @param code code represents the type of events.
     */
    const iconDataToDictionary = (iconEvents: any[], dict: Map<String, any>, code: String) => {
        iconEvents.map((event) => {
            let startDate = new Date(event.startDate);
            let endDate = new Date();
            code == "_ES" ? endDate = new Date(event.endDate) : endDate = new Date(event.startDate)

            while (startDate <= endDate) {
                const tempDate = startDate.toLocaleDateString().split("/")
                let calendarDate = tempDate[2] +"-"+ tempDate[1] +"-"+ tempDate[0];

                if (dict.has(JSON.parse(JSON.stringify(calendarDate)))) {
                    const currentEvents = (dict.get(JSON.parse(JSON.stringify(calendarDate))));
                    dict.set(JSON.parse(JSON.stringify(calendarDate)), currentEvents.concat([event]))
                } else {
                    dict.set(JSON.parse(JSON.stringify(calendarDate)), [event]);
                }
                allEventDates.add(JSON.parse(JSON.stringify(calendarDate)))
                startDate.setDate(startDate.getDate() + 1);
            }
        })
    }

    iconDataToDictionary(project.milesStones, milestoneDictionary, "_MS");
    iconDataToDictionary(project.events, eventDictionary,"_ES");
    iconDataToDictionary(project.deadlines, deadlineDictionary, "_DL");



    function arrayOfEvents(id: string){
        let events: any = []
        if(id !== null){
            events = project.sprints.map(sprint => ({
                id: sprint.id,
                start: sprint.startDate,
                end: sprint.endDate,
                backgroundColor: sprint.colour,
                textColor: getContrast(sprint.colour),
                borderColor: sprint.id === id ? 'black' : 'white',
                title: `Sprint ${sprint.orderNumber}: ${sprint.name}`,
                // This hides the time on the event and must be true for drag and drop resizing to be enabled
                allDay: !DatetimeUtils.hasTimeComponent(sprint.startDate) && !DatetimeUtils.hasTimeComponent(sprint.endDate),
                editable: sprint.id === id,

            }))
        } else {
            events = project.sprints.map(sprint => ({
                id: sprint.id,
                start: sprint.startDate,
                end: sprint.endDate,
                backgroundColor: sprint.colour,
                textColor: getContrast(sprint.colour),
                borderColor: 'white',
                title: `Sprint ${sprint.orderNumber}: ${sprint.name}`,
                // This hides the time on the event and must be true for drag and drop resizing to be enabled
                allDay: !DatetimeUtils.hasTimeComponent(sprint.startDate) && !DatetimeUtils.hasTimeComponent(sprint.endDate),
                editable: false,

            }))
        }
        allEventDates.forEach((eventDate: any) => events.push({
            id: eventDate,
            start: eventDate,
            end: eventDate,
            backgroundColor: "rgba(52, 52, 52, 0.0)",
            textColor: "black",
            title: "",
            editable:false,
            allDay: true,
            borderColor: "transparent",
        }))
        return events
    }

    const [events, setEvents] = React.useState(arrayOfEvents(null));

    const eventClick = (info : any) => {
        const sprintId = info.event.id;
        setEvents(arrayOfEvents(sprintId))
    }


    /**
     * this method will generate a string representation of all the events of on a single day.
     * @param date the date we need the events for
     * @param eventType this is a string reflecting type of event needed for that day - options are events, milestones, and deadlines
     */
    function getEventHoverData(date: any, eventType: string) {
        let stringResult: any[] = [];
        let dictionary = new Map();
        if (eventType == "events") {
            dictionary = eventDictionary;
            stringResult.push("<p style='margin:0px; padding:0px; height: fit-content; width: fit-content'>Events:-</p>")
        } else if (eventType == "milestones") {
            dictionary = milestoneDictionary;
            stringResult.push("<p style='margin:0px; padding:0px; height: fit-content; width: fit-content'>Milestones:-</p>")
        } else {
            dictionary = deadlineDictionary;
            stringResult.push("<p style='margin:0px; padding:0px; height: fit-content; width: fit-content'>Deadlines:-</p>")
        }

        dictionary.get(date).map((subEvent: any) => {
            stringResult.push("<p style='margin:0px; padding:0px; height: fit-content; width: fit-content'>" +
                subEvent.name + "<br />" +
                "<h5 style='margin:0px; padding:0px'>" + subEvent.startDate.toLocaleString() + "&emsp;&emsp;" + subEvent.endDate.toLocaleString() + "</h5></p>");
        })

        return (
            stringResult.join("<br />")
        )
    }

    /**
     * this method will get the event ID which is the date and see if we have any events / deadlines / milestones for that day.
     * there could be more than one kind of even on the same day to so distinguish between events each event id will have
     * "ES" / "DL" / "MS" suffix at the end of the id separated by "_".
     * @param eventInfo id of the event can be retrieved from eventInfo of the day.
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
                        eventDictionary.has(eventInfo.event.id)?
                            <>
                                <div style={{margin:"3px 0 3px 0", width:"30%"}} data-tip data-for = {"events" + eventInfo.event.id.toString()}>
                                        <span className="material-icons" style={{float: "left"}} >event</span>
                                        <p style={{
                                            float: "left",
                                            margin: "3px 0 0 15px"
                                        }}>{eventDictionary.get(eventInfo.event.id).length}</p>
                                </div>

                                <ReactTooltip id={"events" + eventInfo.event.id.toString()} place="right" effect="float" html={true} multiline={true}
                                              getContent={() => getEventHoverData(eventInfo.event.id, "events")}
                                >
                                </ReactTooltip>
                            </>
                            :
                            <div style={{height:"25px", width:"20px", border:"none"}}></div>
                    }
                    {
                        milestoneDictionary.has(eventInfo.event.id)?
                            <>
                                <div style={{margin:"3px 0 3px 0", width:"30%"}} data-tip data-for = {"milestones" + eventInfo.event.id.toString()}>
                                    <span className="material-icons" style={{float: "left"}}>flag</span>
                                    <p style={{float: "left", margin: "3px 0 0 15px"}}>
                                        {milestoneDictionary.get(eventInfo.event.id).length}
                                    </p>
                                </div>

                                <ReactTooltip id={"milestones" + eventInfo.event.id.toString()} place="right" effect="float" html={true} multiline={true}
                                              getContent={() => getEventHoverData(eventInfo.event.id, "milestones")}
                                >
                                </ReactTooltip>
                            </>
                            :
                            <div style={{height:"25px", width:"20px", border:"none"}}></div>
                    }
                    {
                        deadlineDictionary.has(eventInfo.event.id)?
                            <>
                                <div style={{margin:"3px 0 3px 0", width:"30%"}} data-tip data-for = {"deadlines" + eventInfo.event.id.toString()}>
                                    <span className="material-icons" style={{float: "left"}}>timer</span>
                                    <p style={{float: "left", margin: "3px 0 0 15px"}}>
                                        {deadlineDictionary.get(eventInfo.event.id).length}
                                    </p>
                                </div>

                                <ReactTooltip id={"deadlines" + eventInfo.event.id.toString()} place="right" effect="float" html={true} multiline={true}
                                              getContent={() => getEventHoverData(eventInfo.event.id, "deadlines")}
                                >
                                </ReactTooltip>
                            </>
                            :
                            <div style={{height:"25px", width:"20px", border:"none"}}></div>
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
                eventDurationEditable
                eventOverlap={false}
                eventConstraint={projectRange}
                eventChange={onSaveDatesCallback}
                eventClick={eventClick}

                /* Calendar config */
                validRange={projectRange}
                height='100vh'
                nextDayThreshold={"00:00:01"}
            />
        </>
    )
})