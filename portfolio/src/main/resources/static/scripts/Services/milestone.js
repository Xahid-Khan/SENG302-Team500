class Milestone {
    constructor(containerElement, data, project, deleteCallback, milestoneUpdateSavedCallback) {
        this.containerElement = containerElement;
        this.project = project;
        this.milestone = data;
        this.milestoneUpdateSavedCallback = milestoneUpdateSavedCallback;
        this.deleteCallback = deleteCallback;
        this.updatemilestoneLoadingStatus = LoadingStatus.NotYetAttempted;

        this.currentView = null;
        this.showViewer();
    }

    /**
     * Updates milestone according to newValue attributes.
     * @param newValue
     */
    async updateMilestone(newValue) {
        if (this.updatemilestoneLoadingStatus === LoadingStatus.Pending) {
            return;
        }
        else if (
            newValue.name === this.milestone.name
            && newValue.description === this.milestone.description
            && DatetimeUtils.areEqual(newValue.startDate, this.milestone.startDate)
            && DatetimeUtils.areEqual(newValue.endDate, this.milestone.endDate)
        ) {
            // Nothing has changed

            const showingSprints = this.currentView.showingSprints;
            const showingEvents = this.currentView.showingEvents;
            const showingDeadlines = this.currentView.showingDeadlines;

            this.showViewer();
            this.currentView.toggleMilestones();
            if (showingSprints) {
                this.currentView.toggleSprints();
            }
            if (showingEvents) {
                this.currentView.toggleEvents();
            }
            if (showingDeadlines) {
                this.currentView.toggleDeadlines()
            }

            return;
        }

        this.updatemilestoneLoadingStatus = LoadingStatus.Pending;

        try {
            const response = await fetch(`api/v1/milestones/${this.milestone.milestoneId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newValue)
            })

            if (!response.ok) {
                await ErrorHandlerUtils.handleNetworkError(response, "update milestone");
            }

            const newMilestone = await response.json();
            this.milestoneUpdateSavedCallback({
                ...newMilestone,
                startDate: DatetimeUtils.networkStringToLocalDate(newMilestone.startDate),
                endDate: DatetimeUtils.networkStringToLocalDate(newMilestone.endDate)
            });
        }
        catch (ex) {
            this.updatemilestoneLoadingStatus = LoadingStatus.Error;

            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }

            ErrorHandlerUtils.handleUnknownNetworkError(ex, "update milestone");
        }
    }

    /**
     * Shows milestone editing view.
     */
    showEditor() {
        this.currentView?.dispose();
        this.currentView = new Editor(
            this.containerElement,
            "Edit milestone details:",
            this.milestone,
            this.showViewer.bind(this),
            this.updateMilestone.bind(this),
            Editor.makeProjectMilestoneDatesValidator(this.project)
        );
    }

    /**
     * Refreshes view, disposing of the previous view and reloading it.
     */
    showViewer() {
        this.currentView?.dispose();
        this.currentView = new MilestoneView(this.containerElement, this.project.sprints, this.milestone, this.deleteMilestone.bind(this), this.showEditor.bind(this));
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
    async deleteMilestone() {
        if (this.deleteLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.deleteLoadingStatus = LoadingStatus.Pending;

        try {
            const response = await fetch(`api/v1/milestones/${this.milestone.milestoneId}`, {
                method: 'DELETE'
            })
            if (!response.ok) {
                await ErrorHandlerUtils.handleNetworkError(response, "delete milestone");
            }

            this.deleteLoadingStatus = LoadingStatus.Done;
            this.deleteCallback(this.milestone.milestoneId);
        } catch (ex) {
            this.deleteLoadingStatus = LoadingStatus.Error;

            if (ex instanceof PortfolioNetworkError) {
                throw ex;
            }

            ErrorHandlerUtils.handleUnknownNetworkError(ex, "delete milestone");
        }
    }
}