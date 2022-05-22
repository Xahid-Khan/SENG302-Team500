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
            <button class="icon-button toggle-deadline-details" id="toggle-deadline-details-${this.deadline.deadlineId}">+</button>
    </div>
    <div class="events-title">
        <span id="deadline-title-text-${this.deadline.deadlineId}" style="font-style: italic;"></span> | <span id="start-date-${this.deadline.deadlineId}"></span> - <span id="end-date-${this.deadline.deadlineId}"></span>

        
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
        document.getElementById(`start-date-${this.deadline.deadlineId}`).innerText = DatetimeUtils.localToUserDMY(this.deadline.startDate);
        const displayedDate = new Date(this.deadline.endDate.valueOf());
        displayedDate.setDate(displayedDate.getDate() - 1);
        document.getElementById(`end-date-${this.deadline.deadlineId}`).innerText = DatetimeUtils.localToUserDMY(displayedDate);
    }

    /**
     * Toggles expanded view and button for deadlines.
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
        document.getElementById(`deadline-button-edit-${this.deadline.deadlineId}`).addEventListener('click', () => this.editCallback());
        document.getElementById(`deadline-button-delete-${this.deadline.deadlineId}`).addEventListener("click", () => this.deleteCallback());

        this.toggleButton.addEventListener('click', this.toggleExpandedView.bind(this));
    }

    getSprints() {
        let html = "<label>Sprints in progress during this deadline: </label>";
        this.sprints.forEach(sprint => {
            if (this.deadline.startDate >= sprint.startDate && this.deadline.startDate <= sprint.endDate || this.deadline.endDate >= sprint.startDate && this.deadline.endDate <= sprint.endDate) {
                html += `<div class="deadline-sprint-details"  style="color: ${sprint.colour}">   - <span>${sprint.name}: </span><span>${DatetimeUtils.localToUserDMY(sprint.startDate)}</span> - <span>${DatetimeUtils.localToUserDMY(sprint.endDate)}</span>`;
            }
        });
        if (html === "<label>Sprints in progress during this deadline: </label>") {
            html += "<span>No sprints are overlapping with this deadline</span>"
        }
        return html;
    }

    dispose() {

    }
}

