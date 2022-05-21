class Event {
    constructor(containerElement, data, project, deleteCallback, eventUpdateSavedCallback) {
        this.containerElement = containerElement;
        this.project = project;
        this.event = data;
        this.eventUpdateSavedCallback = eventUpdateSavedCallback;
        this.deleteCallback = deleteCallback;
        this.updateEventLoadingStatus = LoadingStatus.NotYetAttempted;

        this.milestonesContainer = null;
        this.deadlinesContainer = null;
        this.showingMilestones = false;
        this.showingDeadlines = false;

        this.currentView = null;
        this.showViewer();
    }

    /**
     * Updates event according to newValue attributes.
     * @param newValue
     */
    async updateEvent(newValue) {
        if (this.updateEventLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        if (this.showingMilestones) {
            this.milestonesContainer.style.display = "block";
        }
        if (this.showingDeadlines) {
            this.deadlinesContainer.style.display = "block";
        }

        this.updateEventLoadingStatus = LoadingStatus.Pending;

        try {
            const response = await fetch(`api/v1/events/${this.event.eventId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newValue)
            })

            if (!response.ok) {
                await ErrorHandlerUtils.handleNetworkError(response, "update event");
            }

            const newEvent = await response.json();
            this.eventUpdateSavedCallback({
                ...newEvent,
                startDate: DatetimeUtils.networkStringToLocalDate(newEvent.startDate),
                endDate: DatetimeUtils.networkStringToLocalDate(newEvent.endDate)
            });
        }
        catch (ex) {
            this.updateEventLoadingStatus = LoadingStatus.Error;

            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }

            ErrorHandlerUtils.handleUnknownNetworkError(ex, "update event");
        }
    }

    /**
     * Shows event editing view.
     */
    showEditor() {
        this.milestonesContainer = this.currentView.containerElement.parentElement.parentElement.getElementsByClassName("milestones").item(0);
        this.deadlinesContainer = this.currentView.containerElement.parentElement.parentElement.getElementsByClassName("deadlines").item(0);
        if (this.milestonesContainer.style.display === "block") {
            this.showingMilestones = true;
            this.milestonesContainer.style.display = "none";
        }
        if (this.deadlinesContainer.style.display === "block") {
            this.showingDeadlines = true;
            this.deadlinesContainer.style.display = "none";
        }
        this.currentView?.dispose();
        this.currentView = new Editor(
            this.containerElement,
            "Edit event details:",
            this.event,
            this.showViewer.bind(this),
            this.updateEvent.bind(this),
            Editor.makeProjectEventDatesValidator(this.project),
            true
        );
    }

    /**
     * Refreshes view, disposing of the previous view and reloading it.
     */
    showViewer() {
        if (this.showingMilestones) {
            this.milestonesContainer.style.display = "block";
        }
        if (this.showingDeadlines) {
            this.deadlinesContainer.style.display = "block";
        }
        this.currentView?.dispose();
        this.currentView = new EventView(this.containerElement, this.project.sprints, this.event, this.deleteEvent.bind(this), this.showEditor.bind(this));
    }

    /**
     * Gets the sprint to explicitly destroy itself prior
     */
    dispose() {
        this.currentView.dispose();
    }

    /**
     * Handles deletion of sprint when making DELETE request.
     */
    async deleteEvent() {
        if (this.deleteLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.deleteLoadingStatus = LoadingStatus.Pending;

        try {
            const response = await fetch(`api/v1/events/${this.event.eventId}`, {
                method: 'DELETE'
            })
            if (!response.ok) {
                await ErrorHandlerUtils.handleNetworkError(response, "delete event");
            }

            this.deleteLoadingStatus = LoadingStatus.Done;
            this.deleteCallback(this.event.eventId);
        } catch (ex) {
            this.deleteLoadingStatus = LoadingStatus.Error;

            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }

            ErrorHandlerUtils.handleUnknownNetworkError(ex, "delete event");
        }
    }
}