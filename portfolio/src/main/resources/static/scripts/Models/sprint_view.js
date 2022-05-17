/**
 * Handles the Sprint view, adding HTML in the Sprint Container.
 */
class SprintView {
    expandedView = false;

    constructor(containerElement, events, deadlines, milestones, sprints, sprint, deleteCallback, editCallback) {
        this.containerElement = containerElement;
        this.sprint = sprint;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
        this.events = events;
        this.sprints = sprints;
        this.deadlines = deadlines;
        this.milestones = milestones;

        this.constructView();
        this.wireView();
    }

    /**
     * Adds populated HTML to SprintView.
     */
    constructView() {
        this.containerElement.innerHTML = `
    <div class="sprints" id="sprints-container-${this.sprint.sprintId}"></div>
    <div class="sprint-title">
        <span id="sprint-order-text-${this.sprint.sprintId}"></span>: <span id="sprint-title-text-${this.sprint.sprintId}" style="font-style: italic;"></span> | <span id="start-date-${this.sprint.sprintId}"></span> - <span id="end-date-${this.sprint.sprintId}"></span>

        <span class="crud">
            <button class="button sprint-controls" id="sprint-button-edit-${this.sprint.sprintId}" data-privilege="teacher">Edit</button>
            <button class="button sprint-controls" id="sprint-button-delete-${this.sprint.sprintId}" data-privilege="teacher">Delete</button>
            <button class="button toggle-sprint-details" id="toggle-sprint-details-${this.sprint.sprintId}">+</button>
        </span>
    </div>

    <div class="sprint-details" id="sprint-details-${this.sprint.sprintId}">
        <div class="sprint-description" id="sprint-description-${this.sprint.sprintId}"></div>
        <div class="sprint-events" id="sprint-events-${this.sprint.sprintId}"></div>
        <div class="sprint-deadlines" id="sprint-deadlines-${this.sprint.sprintId}"></div>
        <div class="sprint-milestones" id="sprint-milestones-${this.sprint.sprintId}"></div>
    </div>
    <div class="colour-block" id="sprint-colour-block-${this.sprint.sprintId}"></div>
    `;

        this.toggleButton = document.getElementById(`toggle-sprint-details-${this.sprint.sprintId}`);
        this.sprintDetails = document.getElementById(`sprint-details-${this.sprint.sprintId}`);
        this.description = document.getElementById(`sprint-description-${this.sprint.sprintId}`);
        this.colourBlock = document.getElementById(`sprint-colour-block-${this.sprint.sprintId}`);
        this.details = document.getElementById(`sprint-details-${this.sprint.sprintId}`);
        this.sprintEvents = document.getElementById(`sprint-events-${this.sprint.sprintId}`);
        this.sprintDeadlines = document.getElementById(`sprint-deadlines-${this.sprint.sprintId}`);
        this.sprintMilestones = document.getElementById(`sprint-milestones-${this.sprint.sprintId}`);
        document.getElementById(`sprint-order-text-${this.sprint.sprintId}`).innerText = `Sprint ${this.sprint.orderNumber}`;
        document.getElementById(`sprint-title-text-${this.sprint.sprintId}`).innerText = this.sprint.name;
        this.description.innerText = "Description: " + this.sprint.description;
        this.sprintEvents.innerHTML = this.getEvents();
        this.sprintDeadlines.innerHTML = this.getDeadlines();
        this.sprintMilestones.innerHTML = this.getMilestones();
        this.colourBlock.style.background = this.sprint.colour;
        document.getElementById(`start-date-${this.sprint.sprintId}`).innerText = DatetimeUtils.localToUserDMY(this.sprint.startDate);
        const displayedDate = new Date(this.sprint.endDate.valueOf());
        if (DatetimeUtils.getTimeStringIfNonZeroLocally(this.sprint.endDate) === null) {
            displayedDate.setDate(displayedDate.getDate() - 1);
        }

        document.getElementById(`end-date-${this.sprint.sprintId}`).innerText = DatetimeUtils.localToUserDMY(displayedDate);
    }

    /**
     * Toggles expanded view and button for sprints.
     */
    toggleExpandedView() {
        if (this.expandedView) {
            this.details.style.display = "none";
            this.toggleButton.innerText = "+";
        }
        else {
            this.details.style.display = "block";
            this.toggleButton.innerText = "-";
        }

        this.expandedView = !this.expandedView;
    }

    wireView() {
        document.getElementById(`sprint-button-edit-${this.sprint.sprintId}`).addEventListener('click', () => this.editCallback());
        document.getElementById(`sprint-button-delete-${this.sprint.sprintId}`).addEventListener("click", () => this.deleteCallback());

        this.toggleButton.addEventListener('click', this.toggleExpandedView.bind(this));
    }

    getEvents() {
        let html = "<label>Events occurring during this sprint: </label>";
        this.events.forEach(event => {
            if (event.startDate >= this.sprint.startDate && event.startDate <= this.sprint.endDate || event.endDate >= this.sprint.startDate && event.endDate <= this.sprint.endDate) {
                html += `<div class="sprint-event-details">   - <span>${event.name}: </span>`
                if (event.startDate >= this.sprint.startDate && event.startDate <= this.sprint.endDate) {
                    html += `<span style="color: ${this.sprint.colour}">${DatetimeUtils.localToUserDMY(event.startDate)}</span> - `;
                } else {
                    let found = false;
                    this.sprints.forEach(sprint => {
                        if (event.startDate >= sprint.startDate && event.startDate <= sprint.endDate) {
                            html += `<span style="color: ${sprint.colour}">${DatetimeUtils.localToUserDMY(event.startDate)}</span> - `;
                            found = true;
                        }
                    });
                    if (!found) {
                        html += `<span">${DatetimeUtils.localToUserDMY(event.startDate)}</span> - `;
                    }
                }
                if (event.endDate >= this.sprint.startDate && event.endDate <= this.sprint.endDate) {
                    html += `<span style="color: ${this.sprint.colour}">${DatetimeUtils.localToUserDMY(event.endDate)}</span>`;
                } else {
                    let found = false;
                    this.sprints.forEach(sprint => {
                        if (event.endDate >= sprint.startDate && event.endDate <= sprint.endDate) {
                            html += `<span style="color: ${sprint.colour}">${DatetimeUtils.localToUserDMY(event.endDate)}</span>`;
                            found = true;
                        }
                    });
                    if (!found) {
                        html += `<span>${DatetimeUtils.localToUserDMY(event.endDate)}</span>`;
                    }
                }
            }
        });
        if (html === "<label>Events occurring during this sprint: </label>") {
            html += "<span>No events will occur during this sprint</span>"
        }
        return html;
    }

    getDeadlines() {
        let html = "<label>Deadlines occurring during this sprint: </label>";
        this.deadlines.forEach(deadline => {
            if (deadline.startDate >= this.sprint.startDate && deadline.startDate <= this.sprint.endDate) {
                html += `<div class="sprint-deadline-details">   - <span>${deadline.name}: </span> <span style="color: ${this.sprint.colour}">${DatetimeUtils.localToUserDMY(deadline.startDate)}</span>`;
            }
        });
        if (html === "<label>Deadlines occurring during this sprint: </label>") {
            html += "<span>No deadlines will occur during this sprint</span>"
        }
        return html;
    }

    getMilestones() {
        let html = "<label>Milestones occurring during this sprint: </label>";
        this.milestones.forEach(milestone => {
            if (milestone.startDate >= this.sprint.startDate && milestone.startDate <= this.sprint.endDate) {
                html += `<div class="sprint-milestone-details">   - <span>${milestone.name}: </span> <span style="color: ${this.sprint.colour}">${DatetimeUtils.localToUserDMY(milestone.startDate)}</span>`;
            }
        });
        if (html === "<label>Milestones occurring during this sprint: </label>") {
            html += "<span>No milestones will occur during this sprint</span>"
        }
        return html;
    }

    dispose() {

    }
}