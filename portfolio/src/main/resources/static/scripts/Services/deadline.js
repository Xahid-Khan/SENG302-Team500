class Deadline {
    constructor(containerElement, data, project, deleteCallback, deadlineUpdateSavedCallback, deadlineEditCallback, loadEventsCallback) {
        this.containerElement = containerElement;
        this.project = project;
        this.deadline = data;
        this.deadlineUpdateSavedCallback = deadlineUpdateSavedCallback;
        this.deleteCallback = deleteCallback;
        this.updatedeadlineLoadingStatus = LoadingStatus.NotYetAttempted;
        this.deadlineEditCallback = deadlineEditCallback;
        this.loadEventsCallback = loadEventsCallback;

        this.currentView = null;
        this.showViewer();
    }

    /**
     * Updates deadline according to newValue attributes.
     * @param newValue
     */
    async updateDeadline(newValue) {
        if (this.updatedeadlineLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.updatedeadlineLoadingStatus = LoadingStatus.Pending;

        try {
            const response = await fetch(`api/v1/deadlines/${this.deadline.deadlineId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newValue)
            })

            if (!response.ok) {
                await ErrorHandlerUtils.handleNetworkError(response, "update deadline");
            }

            const newDeadline = await response.json();
            this.deadlineUpdateSavedCallback({
                ...newDeadline,
                startDate: DatetimeUtils.networkStringToLocalDate(newDeadline.startDate),
                endDate: DatetimeUtils.networkStringToLocalDate(newDeadline.endDate)
            });
        }
        catch (ex) {
            this.updatedeadlineLoadingStatus = LoadingStatus.Error;

            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }

            ErrorHandlerUtils.handleUnknownNetworkError(ex, "update deadline");
        }
    }

    /**
     * Shows deadline editing view.
     */
    showEditor() {
        this.deadlineEditCallback();
        this.currentView?.dispose();
        this.currentView = new Editor(
            this.containerElement,
            "Edit deadline details:",
            this.deadline,
            this.showViewer.bind(this),
            this.updateDeadline.bind(this),
            Editor.makeProjectDeadlineDatesValidator(this.project)
        );
    }

    /**
     * Refreshes view, disposing of the previous view and reloading it.
     */
    showViewer() {
        this.loadEventsCallback();
        this.currentView?.dispose();
        this.currentView = new DeadlineView(this.containerElement, this.project.sprints, this.deadline, this.deleteDeadline.bind(this), this.showEditor.bind(this));
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
    async deleteDeadline() {
        if (this.deleteLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.deleteLoadingStatus = LoadingStatus.Pending;

        try {
            const response = await fetch(`api/v1/deadlines/${this.deadline.deadlineId}`, {
                method: 'DELETE'
            })
            if (!response.ok) {
                await ErrorHandlerUtils.handleNetworkError(response, "delete deadline");
            }

            this.deleteLoadingStatus = LoadingStatus.Done;
            this.deleteCallback(this.deadline.deadlineId);
        } catch (ex) {
            this.deleteLoadingStatus = LoadingStatus.Error;

            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }

            ErrorHandlerUtils.handleUnknownNetworkError(ex, "delete deadline");
        }
    }
}