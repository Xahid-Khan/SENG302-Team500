class DeadlineView {
    expandedView = false;

    constructor(containerElement, sprints, deadline, deleteCallback, editCallback) {
        this.containerElement = containerElement;
        this.deadline = deadline;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
        this.sprints = sprints;

        this.constructView();
        this.wireView();
    }

    /**
     * Adds populated HTML to deadlineView.
     */
    constructView() {
        this.containerElement.innerHTML = `
    <div class="crud">
            <button class="icon-button deadline-controls" id="deadline-button-edit-${this.deadline.deadlineId}" data-privilege="teacher"><span class="material-icons">edit</span></button>
            <button class="icon-button deadline-controls" id="deadline-button-delete-${this.deadline.deadlineId}" data-privilege="teacher"><span class="material-icons">clear</span></button>
            <button class="button visibility-button toggle-deadline-details" id="toggle-deadline-details-${this.deadline.deadlineId}"><span class='material-icons'>visibility_off</span></button>
    </div>
    <div class="editing-live-update" id="event-form-${this.deadline.deadlineId}"></div>
    <div class="events-title">
        <span id="deadline-title-text-${this.deadline.deadlineId}" style="font-style: italic;"></span> | <span id="start-date-${this.deadline.deadlineId}"></span>
    </div>
    <div class="events-details" id="deadline-details-${this.deadline.deadlineId}">
        <div class="deadline-description" id="deadline-description-${this.deadline.deadlineId}"></div>
        <div class="events-sprints" id="deadline-sprints-${this.deadline.deadlineId}"></div>
    </div>
    
    `;

        this.toggleButton = document.getElementById(`toggle-deadline-details-${this.deadline.deadlineId}`);
        this.description = document.getElementById(`deadline-description-${this.deadline.deadlineId}`);
        this.details = document.getElementById(`deadline-details-${this.deadline.deadlineId}`);
        this.deadlineSprints = document.getElementById(`deadline-sprints-${this.deadline.deadlineId}`);

        document.getElementById(`deadline-title-text-${this.deadline.deadlineId}`).innerText = this.deadline.name;
        this.description.innerText = "Description: " + this.deadline.description;
        this.deadlineSprints.innerHTML = this.getSprints();
        this.sprints.forEach((sprint) => {
            if (document.getElementById(`deadline-sprint-name-${this.deadline.deadlineId}-${sprint.sprintId}`)) {
                document.getElementById(`deadline-sprint-name-${this.deadline.deadlineId}-${sprint.sprintId}`).innerText = sprint.name + ':'
            }
        })
        document.getElementById(`start-date-${this.deadline.deadlineId}`).innerText = DatetimeUtils.localToUserDMY(this.deadline.startDate);
        const displayedDate = new Date(this.deadline.endDate.valueOf());
        displayedDate.setDate(displayedDate.getDate() - 1);
    }

    /**
     * Toggles expanded view and button for deadlines.
     */
    toggleExpandedView() {
        if (this.expandedView) {
            this.details.style.display = "none";
            this.toggleButton.innerHTML = "<span class='material-icons'>visibility_off</span>";
        }
        else {
            this.details.style.display = "block";
            this.toggleButton.innerHTML = "<span class='material-icons'>visibility</span>";
        }

        this.expandedView = !this.expandedView;
    }

    wireView() {
        document.getElementById(`deadline-button-edit-${this.deadline.deadlineId}`).addEventListener('click', () => this.editCallback());
        document.getElementById(`deadline-button-delete-${this.deadline.deadlineId}`).addEventListener("click", () => {this.deleteCallback(); Socket.saveEdit(this.deadline.deadlineId)});

        this.toggleButton.addEventListener('click', this.toggleExpandedView.bind(this));
    }

    getSprints() {
        let html = "<label>Sprints in progress during this deadline: </label>";
        let foundSprints = false
        this.sprints.forEach(sprint => {
            if (this.deadline.startDate >= sprint.startDate && this.deadline.startDate <= sprint.endDate || this.deadline.endDate >= sprint.startDate && this.deadline.endDate <= sprint.endDate) {
                html += `
                <div class="event-sprint-details">
                    <span> • </span>
                    <span id="deadline-sprint-name-${this.deadline.deadlineId}-${sprint.sprintId}" style="background-color: ${sprint.colour}; border: 1px solid ${sprint.colour};" class="event-sprint-name"></span>
                    <span>${DatetimeUtils.localToUserDMY(sprint.startDate)}</span>
                    <span> - </span>
                    <span>${DatetimeUtils.localToUserDMY(sprint.endDate)}</span>
                </div>`;
                foundSprints = true
            }
        });
        if (!foundSprints) {
            html = "<label>No sprints are overlapping with this deadline</label>"
        }
        return html;
    }

    dispose() {

    }
}

