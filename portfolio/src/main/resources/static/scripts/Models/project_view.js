/**
 * Handles view of the Projects and populating the HTML.
 *
 * Note: This class is very messy and unnecessarily complicated due to poor code being built on
 * and expanded causing the code to be worse and worse, without a properly set architecture. As
 * advised by Fabian, we are going to leave it but this, and its related files, such as event view
 * etc, could be very much improved
 */
class ProjectView {
    showingSprints = false;
    showingEvents = false;
    showingDeadlines = false;
    showingMilestones = false;

    addEventForm = null;
    addEventLoadingStatus = LoadingStatus.NotYetAttempted

    addSprintForm = null;
    addSprintLoadingStatus = LoadingStatus.NotYetAttempted;

    addDeadlineForm = null
    addDeadlineLoadingStatus = LoadingStatus.NotYetAttempted;

    addMilestoneForm = null
    addMilestoneLoadingStatus = LoadingStatus.NotYetAttempted;

    constructor(containerElement, project, editCallback, deleteCallback, sprintDeleteCallback, sprintUpdateCallback, eventDeleteCallback, eventUpdateCallback, deadlineDeleteCallback, deadlineUpdateCallback, milestoneDeleteCallback, milestoneUpdateCallback, eventEditCallback, deadlineEditCallback, milestoneEditCallback) {
        console.log("project", project)
        this.containerElement = containerElement;
        this.project = project;
        this.sprintContainer = null;
        this.sprints = new Map();
        this.eventContainer = null;
        this.events = new Map();
        this.milestoneContainer = null;
        this.milestones = new Map();
        this.deadlineContainer = null;
        this.deadlines = new Map();
        this.milestoneContainer = null;
        this.milestones = new Map();
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
        this.sprintDeleteCallback = sprintDeleteCallback;
        this.sprintUpdateCallback = sprintUpdateCallback;
        this.eventDeleteCallback = eventDeleteCallback;
        this.eventUpdateCallback = eventUpdateCallback;
        this.deadlineDeleteCallback = deadlineDeleteCallback;
        this.deadlineUpdateCallback = deadlineUpdateCallback
        this.milestoneDeleteCallback = milestoneDeleteCallback;
        this.milestoneUpdateCallback = milestoneUpdateCallback;
        this.eventEditCallback = eventEditCallback;
        this.deadlineEditCallback = deadlineEditCallback;
        this.milestoneEditCallback = milestoneEditCallback;

        this.constructAndPopulateView();
        this.wireView();
    }

    /**
     * Append a sprint element to the sprintElement and instantiate and store a sprint with the given data.
     */
    appendSprint(sprintData) {
        const sprintElement = document.createElement("div");
        sprintElement.classList.add("events-view", "raised-card");
        sprintElement.id = `sprint-view-${sprintElement.id}`;

        this.sprintContainer.appendChild(sprintElement);

        console.log("Binding sprint");

        console.log(sprintData.startDate);
        this.sprints.set(sprintData.sprintId, new Sprint(sprintElement, sprintData, this.project, this.sprintDeleteCallback, this.sprintUpdateCallback));

        console.log("Sprint bound");

    }

    appendEvent(eventData) {
        const eventElement = document.createElement("div")
        eventElement.classList.add("events-view", "raised-card");
        eventElement.id = `event-view-${eventElement.id}`;
        this.eventContainer.appendChild(eventElement);

        console.log("Binding event");

        this.events.set(eventData.eventId, new Event(eventElement, eventData, this.project, this.eventDeleteCallback, this.eventUpdateCallback, this.eventEditCallback, this.loadEvents.bind(this)));

        console.log("Event bound");
    }

    appendMilestone(milestoneData) {
        const milestoneElement = document.createElement("div")
        milestoneElement.classList.add("events-view", "raised-card");
        milestoneElement.id = `milestone-view-${milestoneElement.id}`;
        this.milestoneContainer.appendChild(milestoneElement);

        console.log("Binding milestone");

        this.milestones.set(milestoneData.milestoneId, new Milestone(milestoneElement, milestoneData, this.project, this.milestoneDeleteCallback, this.milestoneUpdateCallback, this.milestoneEditCallback, this.loadEvents.bind(this)));

        console.log("Milestone bound");
    }

    appendDeadline(deadlineData) {
        const deadlineElement = document.createElement("div")
        deadlineElement.classList.add("events-view", "raised-card");
        deadlineElement.id = `deadline-view-${deadlineElement.id}`;
        this.deadlineContainer.appendChild(deadlineElement);

        console.log("Binding deadline");

        this.deadlines.set(deadlineData.deadlineId, new Deadline(deadlineElement, deadlineData, this.project, this.deadlineDeleteCallback, this.deadlineUpdateCallback, this.deadlineEditCallback, this.loadEvents.bind(this)));

        console.log("Deadline bound");
    }

    /**
     * Adds HTML in to the project container, with the main attributes of projects and sprints.
     */
    constructAndPopulateView() {
        this.containerElement.innerHTML = `
      <div class="project-title">
          <span class="project-title-text">
            <span id="project-title-text-${this.project.id}"></span> | <span id="project-startDate-${this.project.id}"></span> - <span id="project-endDate-${this.project.id}"></span>
          </span>   
          <span class="monthly-planner-redirect">
              <button class="button monthly-planner-redirect-button" id="monthly-planner-redirect-button-${this.project.id}">View Monthly Planner</button>
          </span>
          <span class="crud">
              <button class="button edit-project" id="project-edit-button-${this.project.id}" data-privilege="teacher">Edit</button>
              <button class="button" id="project-delete-button-${this.project.id}" data-privilege="teacher">Delete</button>
          </span>
      </div>
      <div>
          <div class="project-description" id="project-description-${this.project.id}"></div>
          <div class="toggle-view-controls">
              <button class="button toggle-events" id="toggle-event-button-${this.project.id}"> Show Events</button>
              <button class="button toggle-deadlines" id="toggle-deadline-button-${this.project.id}"> Show Deadlines</button>
              <button class="button toggle-milestones" id="toggle-milestone-button-${this.project.id}"> Show Milestones</button>
              <button class="button toggle-sprints" id="toggle-sprint-button-${this.project.id}"> Show Sprints</button>
          </div>    
      </div>
      <div class="project-events">
          <div class="events raised-card" id="events-container-${this.project.id}">
          <div class="events-header">
             <div class="events-section-title">
                <span class="material-icons">event</span>
                Events:
             </div>
             <div class="add-events">
                <button class="button" id="add-event-button-${this.project.id}" data-privilege="teacher"> Add Event</button>
             </div>
          </div>
          </div>
          <div class="events raised-card" id="deadlines-container-${this.project.id}">
            <div class="events-header">
                <div class="events-section-title">
                    <span class="material-icons">timer</span>
                    Deadlines:
                </div>
                <div class="add-events">
                    <button class="button" id="add-deadline-button-${this.project.id}" data-privilege="teacher"> Add Deadline</button>
                </div>
            </div>
          </div>
          <div class="events raised-card" id="milestones-container-${this.project.id}">
              <div class="events-header">
                <div class="events-section-title"> 
                    <span class="material-icons">flag</span>
                    Milestones:
                </div>
                <div class="add-events">
                    <button class="button" id="add-milestone-button-${this.project.id}" data-privilege="teacher"> Add Milestone</button>
                </div>
              </div>
          </div>
      </div>
      <div class="sprints raised-card" id="sprints-container-${this.project.id}">
        <div class="events-header">
            <div class="events-section-title">Sprints:</div>
            <div class="add-events">
                <button class="button" id="add-sprint-button-${this.project.id}" data-privilege="teacher"> Add Sprint</button>
            </div>
        </div>
      </div>
    `;

        document.getElementById(`project-title-text-${this.project.id}`).innerText = this.project.name;
        document.getElementById(`project-description-${this.project.id}`).innerText = this.project.description;
        document.getElementById(`project-startDate-${this.project.id}`).innerText = DatetimeUtils.localToUserDMY(this.project.startDate);
        const displayedDate = new Date(this.project.endDate.valueOf());
        displayedDate.setDate(displayedDate.getDate()  - 1);
        document.getElementById(`project-endDate-${this.project.id}`).innerText = DatetimeUtils.localToUserDMY(displayedDate);

        this.addSprintButton = document.getElementById(`add-sprint-button-${this.project.id}`);
        this.toggleSprintsButton = document.getElementById(`toggle-sprint-button-${this.project.id}`);
        this.sprintsContainer = document.getElementById(`sprints-container-${this.project.id}`);
        this.sprintContainer = document.getElementById(`sprints-container-${this.project.id}`);

        this.addEventButton = document.getElementById(`add-event-button-${this.project.id}`);
        this.toggleEventsButton = document.getElementById(`toggle-event-button-${this.project.id}`);
        this.eventsContainer = document.getElementById(`events-container-${this.project.id}`);
        this.eventContainer = document.getElementById(`events-container-${this.project.id}`);

        this.addMilestoneButton = document.getElementById(`add-milestone-button-${this.project.id}`);
        this.toggleMilestonesButton = document.getElementById(`toggle-milestone-button-${this.project.id}`);
        this.milestonesContainer = document.getElementById(`milestones-container-${this.project.id}`);
        this.milestoneContainer = document.getElementById(`milestones-container-${this.project.id}`);

        this.addDeadlineButton = document.getElementById(`add-deadline-button-${this.project.id}`);
        this.toggleDeadlinesButton = document.getElementById(`toggle-deadline-button-${this.project.id}`);
        this.deadlinesContainer = document.getElementById(`deadlines-container-${this.project.id}`);
        this.deadlineContainer = document.getElementById(`deadlines-container-${this.project.id}`);

        for (let i = 0; i < this.project.sprints.length; i++) {
            this.appendSprint(this.project.sprints[i]);
        }

        for (let j = 0; j < this.project.events.length; j++) {
            this.appendEvent(this.project.events[j]);
        }

        for (let k = 0; k < this.project.milestones.length; k++) {
            this.appendMilestone(this.project.milestones[k]);
        }

        for (let k = 0; k < this.project.deadlines.length; k++) {
            this.appendDeadline(this.project.deadlines[k]);
        }
    }

    /**
     * Toggles hiding and showing of the sprints.
     */
    toggleSprints() {
        if (this.showingSprints) {
            // Hide the sprints
            this.toggleSprintsButton.innerText = "Show Sprints";
            this.sprintsContainer.style.display = "none";
        }
        else {
            // Show the sprints
            this.toggleSprintsButton.innerText = "Hide Sprints";
            this.sprintsContainer.style.display = "block";
        }

        this.showingSprints = !this.showingSprints;
    }

    toggleEvents() {
        if (this.showingEvents) {
            // Hide the sprints
            this.toggleEventsButton.innerText = "Show Events";
            this.eventsContainer.style.display = "none";
        }
        else {
            // Show the events
            this.toggleEventsButton.innerText = "Hide Events";
            this.eventContainer.style.display = "block";
        }

        this.showingEvents = !this.showingEvents;
    }

    toggleMilestones() {
        if (this.showingMilestones) {
            // Hide the sprints
            this.toggleMilestonesButton.innerText = "Show Milestones";
            this.milestonesContainer.style.display = "none";
        }
        else {
            // Show the events
            this.toggleMilestonesButton.innerText = "Hide Milestones";
            this.milestonesContainer.style.display = "block";
        }

        this.showingMilestones = !this.showingMilestones;
    }

    toggleDeadlines() {
        if (this.showingDeadlines) {
            // Hide the sprints
            this.toggleDeadlinesButton.innerText = "Show Deadlines";
            this.deadlinesContainer.style.display = "none";
        }
        else {
            // Show the events
            this.toggleDeadlinesButton.innerText = "Hide Deadlines";
            this.deadlinesContainer.style.display = "block";
        }

        this.showingDeadlines = !this.showingDeadlines;
    }
    
    /**
     * Opens the add sprint form.
     */
    openAddSprintForm() {
        if (this.addSprintForm !== null) {
            return;
        }

        const formContainerElement = document.createElement("div");
        formContainerElement.classList.add("events-view", "raised-card");
        formContainerElement.id = `create-sprint-form-container-${this.project.id}`;
        this.sprintsContainer.append(this.sprintsContainer.firstChild, formContainerElement);

        let defaultName = 1;
        let defaultStartDate = new Date(this.project.startDate.valueOf());

        if (this.project.sprints.length !== 0) {
            defaultName = this.project.sprints[(this.project.sprints.length - 1)].orderNumber + 1;
            defaultStartDate = new Date(this.project.sprints[(this.project.sprints.length - 1)].endDate.valueOf());
        }
        
        const defaultEndDate = new Date(defaultStartDate.valueOf());
        defaultEndDate.setDate(defaultEndDate.getDate() + 22);

        const defaultColour = "#000000";

        const defaultSprint = {
            id: `__NEW_SPRINT_FORM_${this.project.id}`,
            name: `Sprint ${defaultName}`,
            description: null,
            startDate: defaultStartDate,
            endDate: defaultEndDate,
            colour: defaultColour
        };

        this.addSprintForm = {
            container: formContainerElement,
            controller: new Editor(
                formContainerElement,
                "New sprint details:",
                defaultSprint,
                this.closeAddSprintForm.bind(this),
                this.submitAddSprintForm.bind(this),
                Editor.makeProjectSprintDatesValidator(this.project, null)
            )
        };

        if (!this.showingSprints) {
            this.toggleSprints();
        }

    }

    /**
     * Closes the add sprint form.
     */
    closeAddSprintForm() {
        if (this.addSprintForm === null) {
            return;
        }

        this.addSprintForm.controller.dispose();
        this.sprintsContainer.removeChild(this.addSprintForm.container);
        this.addSprintForm = null;
    }

    /**
     * Submits the add sprint form, checking if this task is not being done currently (loading status).
     * @param sprint
     * @returns {Promise<void>}
     */
    async submitAddSprintForm(sprint) {
        if (this.addSprintLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.addSprintLoadingStatus = LoadingStatus.Pending;

        try {
            const res = await fetch(`api/v1/projects/${this.project.id}/sprints`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(sprint)
            });

            if (!res.ok) {
                await ErrorHandlerUtils.handleNetworkError(res, "creating project");
            }

            const newSprint = await res.json();
            this.sprintUpdateCallback({
                ...newSprint,
                startDate: DatetimeUtils.networkStringToLocalDate(newSprint.startDate),
                endDate: DatetimeUtils.networkStringToLocalDate(newSprint.endDate),
                colour: newSprint.colour
            });
        }
        catch (ex) {
            this.addSprintLoadingStatus = LoadingStatus.Error;
            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }
            ErrorHandlerUtils.handleUnknownNetworkError(ex, "creating project");
        }
    }

    openAddEventForm() {
        if (this.addEventForm !== null) {
            return;
        }

        if (this.showingMilestones) {
            this.milestonesContainer.style.display = "none";
        }
        if (this.showingDeadlines) {
            this.deadlinesContainer.style.display = "none";
        }

        const formContainerElement = document.createElement("div");
        formContainerElement.classList.add("events-view", "raised-card");
        formContainerElement.id = `create-event-form-container-${this.project.id}`;
        this.eventContainer.append(this.eventsContainer.firstChild, formContainerElement)

        const defaultEvent = {
            id: `__NEW_EVENT_FORM_${this.project.id}`,
            name: null,
            description: null,
            startDate: null,
            endDate: null
        };

        this.addEventForm = {
            container: formContainerElement,
            controller: new Editor(
                formContainerElement,
                "New event details:",
                defaultEvent,
                this.closeAddEventForm.bind(this),
                this.submitAddEventForm.bind(this),
                Editor.makeProjectEventDatesValidator(this.project),
                true
            )
        };

        if (!this.showingEvents) {
            this.toggleEvents();
        }
    }

    /**
     * Closes the add event form.
     */
    closeAddEventForm() {
        if (this.addEventForm === null) {
            return;
        }

        if (this.showingMilestones) {
            this.milestonesContainer.style.display = "block";
        }
        if (this.showingDeadlines) {
            this.deadlinesContainer.style.display = "block";
        }

        this.addEventForm.controller.dispose();
        this.eventsContainer.removeChild(this.addEventForm.container);
        this.addEventForm = null;
    }

    /**
     * Submits the add event form, checking if this task is not being done currently (loading status).
     * @param event
     * @returns {Promise<void>}
     */
    async submitAddEventForm(event) {
        if (this.addEventLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.addEventLoadingStatus = LoadingStatus.Pending;

        try {
            const res = await fetch(`api/v1/projects/${this.project.id}/events`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(event)
            });

            if (!res.ok) {
                await ErrorHandlerUtils.handleNetworkError(res, "creating project");
            }

            const newEvent = await res.json();
            this.eventUpdateCallback({
                ...newEvent,
                startDate: DatetimeUtils.networkStringToLocalDate(newEvent.startDate),
                endDate: DatetimeUtils.networkStringToLocalDate(newEvent.endDate)
            });
            if (this.showingMilestones) {
                this.milestonesContainer.style.display = "block";
            }
            if (this.showingDeadlines) {
                this.deadlinesContainer.style.display = "block";
            }
        }
        catch (ex) {
            this.addEventLoadingStatus = LoadingStatus.Error;
            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }
            ErrorHandlerUtils.handleUnknownNetworkError(ex, "creating project");
        }
    }

    openAddMilestoneForm() {
        if (this.addMilestoneForm !== null) {
            return;
        }

        if (this.showingEvents) {
            this.eventsContainer.style.display = "none";
        }
        if (this.showingDeadlines) {
            this.deadlinesContainer.style.display = "none";
        }

        const formContainerElement = document.createElement("div");
        formContainerElement.classList.add("events-view", "raised-card");
        formContainerElement.id = `create-milestone-form-container-${this.project.id}`;
        this.milestoneContainer.append(this.milestonesContainer.firstChild, formContainerElement)

        const defaultMilestone = {
            id: `__NEW_MILESTONE_FORM_${this.project.id}`,
            name: null,
            description: null,
            startDate: null,
            endDate: null
        };

        this.addMilestoneForm = {
            container: formContainerElement,
            controller: new Editor(
                formContainerElement,
                "New milestone details:",
                defaultMilestone,
                this.closeAddMilestoneForm.bind(this),
                this.submitAddMilestoneForm.bind(this),
                Editor.makeProjectEventDatesValidator(this.project)
            )
        };

        if (!this.showingMilestones) {
            this.toggleMilestones();
        }
    }

    /**
     * Closes the add milestone form.
     */
    closeAddMilestoneForm() {
        if (this.addMilestoneForm === null) {
            return;
        }

        if (this.showingEvents) {
            this.eventsContainer.style.display = "block";
        }
        if (this.showingDeadlines) {
            this.deadlinesContainer.style.display = "block";
        }

        this.addMilestoneForm.controller.dispose();
        this.milestonesContainer.removeChild(this.addMilestoneForm.container);
        this.addMilestoneForm = null;
    }

    /**
     * Submits the add milestone form, checking if this task is not being done currently (loading status).
     * @param milestone
     * @returns {Promise<void>}
     */
    async submitAddMilestoneForm(milestone) {
        if (this.addMilestoneLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.addMilestoneLoadingStatus = LoadingStatus.Pending;

        try {
            const res = await fetch(`api/v1/projects/${this.project.id}/milestones`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(milestone)
            });

            if (!res.ok) {
                await ErrorHandlerUtils.handleNetworkError(res, "creating project");
            }

            const newMilestone = await res.json();
            this.milestoneUpdateCallback({
                ...newMilestone,
                startDate: DatetimeUtils.networkStringToLocalDate(newMilestone.startDate),
                endDate: DatetimeUtils.networkStringToLocalDate(newMilestone.endDate)
            });
            if (this.showingEvents) {
                this.eventsContainer.style.display = "block";
            }
            if (this.showingDeadlines) {
                this.deadlinesContainer.style.display = "block";
            }
        }
        catch (ex) {
            this.addMilestoneLoadingStatus = LoadingStatus.Error;
            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }
            ErrorHandlerUtils.handleUnknownNetworkError(ex, "creating project");
        }
    }

    openAddDeadlineForm() {
        if (this.addDeadlineForm !== null) {
            return;
        }

        if (this.showingEvents) {
            this.eventsContainer.style.display = "none";
        }
        if (this.showingMilestones) {
            this.milestonesContainer.style.display = "none";
        }

        const formContainerElement = document.createElement("div");
        formContainerElement.classList.add("events-view", "raised-card");
        formContainerElement.id = `create-deadline-form-container-${this.project.id}`;
        this.deadlineContainer.append(this.deadlinesContainer.firstChild, formContainerElement)

        const defaultDeadline = {
            id: `__NEW_DEADLINE_FORM_${this.project.id}`,
            name: null,
            description: null,
            startDate: null,
            endDate: null
        };

        this.addDeadlineForm = {
            container: formContainerElement,
            controller: new Editor(
                formContainerElement,
                "New deadline details:",
                defaultDeadline,
                this.closeAddDeadlineForm.bind(this),
                this.submitAddDeadlineForm.bind(this),
                Editor.makeProjectEventDatesValidator(this.project)
            )
        };

        if (!this.showingDeadlines) {
            this.toggleDeadlines();
        }
    }

    /**
     * Closes the add deadline form.
     */
    closeAddDeadlineForm() {
        if (this.addDeadlineForm === null) {
            return;
        }

        if (this.showingEvents) {
            this.eventsContainer.style.display = "block";
        }
        if (this.showingMilestones) {
            this.milestonesContainer.style.display = "block";
        }

        this.addDeadlineForm.controller.dispose();
        this.deadlinesContainer.removeChild(this.addDeadlineForm.container);
        this.addDeadlineForm = null;
    }

    /**
     * Submits the add deadline form, checking if this task is not being done currently (loading status).
     * @param deadline
     * @returns {Promise<void>}
     */
    async submitAddDeadlineForm(deadline) {
        if (this.addDeadlineLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.addDeadlineLoadingStatus = LoadingStatus.Pending;

        try {
            const res = await fetch(`api/v1/projects/${this.project.id}/deadlines`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(deadline)
            });

            if (!res.ok) {
                await ErrorHandlerUtils.handleNetworkError(res, "creating project");
            }

            const newDeadline = await res.json();
            this.deadlineUpdateCallback({
                ...newDeadline,
                startDate: DatetimeUtils.networkStringToLocalDate(newDeadline.startDate),
                endDate: DatetimeUtils.networkStringToLocalDate(newDeadline.endDate)
            });
            if (this.showingEvents) {
                this.eventsContainer.style.display = "block";
            }
            if (this.showingMilestones) {
                this.milestonesContainer.style.display = "block";
            }
        }
        catch (ex) {
            this.addDeadlineLoadingStatus = LoadingStatus.Error;
            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }
            ErrorHandlerUtils.handleUnknownNetworkError(ex, "creating project");
        }
    }

    monthlyPlannerRedirect(projectId) {
        window.location.href = `monthly-planner/${projectId}`
    }

    wireView() {
        document.getElementById(`project-edit-button-${this.project.id}`).addEventListener("click", () => this.editCallback());
        document.getElementById(`project-delete-button-${this.project.id}`).addEventListener("click", () => this.deleteCallback());
        document.getElementById(`monthly-planner-redirect-button-${this.project.id}`).addEventListener("click", () => this.monthlyPlannerRedirect(this.project.id));
        this.toggleSprintsButton.addEventListener('click', this.toggleSprints.bind(this));
        this.addSprintButton.addEventListener('click', this.openAddSprintForm.bind(this));
        this.toggleEventsButton.addEventListener('click', this.toggleEvents.bind(this));
        this.addEventButton.addEventListener('click', this.openAddEventForm.bind(this));
        this.toggleMilestonesButton.addEventListener('click', this.toggleMilestones.bind(this));
        this.addMilestoneButton.addEventListener('click', this.openAddMilestoneForm.bind(this));
        this.toggleDeadlinesButton.addEventListener('click', this.toggleDeadlines.bind(this));
        this.addDeadlineButton.addEventListener('click', this.openAddDeadlineForm.bind(this));
    }

    loadEvents() {
        if (this.showingEvents) {
            this.eventsContainer.style.display = "block";
        }
        if (this.showingDeadlines) {
            this.deadlinesContainer.style.display = "block";
        }
        if (this.showingMilestones) {
            this.milestoneContainer.style.display = "block";
        }
    }

    dispose() {

    }
}