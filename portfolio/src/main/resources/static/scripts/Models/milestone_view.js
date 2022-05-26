class MilestoneView {
    expandedView = false;

    constructor(containerElement, sprints, milestone, deleteCallback, editCallback) {
        this.containerElement = containerElement;
        this.milestone = milestone;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
        this.sprints = sprints;

        this.constructView();
        this.wireView();
    }

    /**
     * Adds populated HTML to milestoneView.
     */
    constructView() {
        this.containerElement.innerHTML = `
    <div class="crud">
        <button class="icon-button milestone-controls" id="milestone-button-edit-${this.milestone.milestoneId}" data-privilege="teacher"><span class="material-icons">edit</span></button>
        <button class="icon-button milestone-controls" id="milestone-button-delete-${this.milestone.milestoneId}" data-privilege="teacher"><span class="material-icons">clear</span></button>
        <button class="icon-button toggle-milestone-details" id="toggle-milestone-details-${this.milestone.milestoneId}">+</i></button>
    </div>
    <div class="events-title">
        <span id="milestone-title-text-${this.milestone.milestoneId}" style="font-style: italic;"></span> | <span id="start-date-${this.milestone.milestoneId}"></span> - <span id="end-date-${this.milestone.milestoneId}"></span>

    </div>
    <div class="events-details" id="milestone-details-${this.milestone.milestoneId}">
        <div class="milestone-description" id="milestone-description-${this.milestone.milestoneId}"></div>
        <div class="events-sprints" id="milestone-sprints-${this.milestone.milestoneId}"></div>
    </div>
    
    `;

        this.toggleButton = document.getElementById(`toggle-milestone-details-${this.milestone.milestoneId}`);
        this.description = document.getElementById(`milestone-description-${this.milestone.milestoneId}`);
        this.details = document.getElementById(`milestone-details-${this.milestone.milestoneId}`);
        this.milestoneSprints = document.getElementById(`milestone-sprints-${this.milestone.milestoneId}`);

        document.getElementById(`milestone-title-text-${this.milestone.milestoneId}`).innerText = this.milestone.name;
        this.description.innerText = "Description: " + this.milestone.description;
        this.milestoneSprints.innerHTML = this.getSprints();
        document.getElementById(`start-date-${this.milestone.milestoneId}`).innerText = DatetimeUtils.localToUserDMY(this.milestone.startDate);
        const displayedDate = new Date(this.milestone.endDate.valueOf());
        displayedDate.setDate(displayedDate.getDate() - 1);
        document.getElementById(`end-date-${this.milestone.milestoneId}`).innerText = DatetimeUtils.localToUserDMY(displayedDate);
    }

    /**
     * Toggles expanded view and button for milestones.
     */
    toggleExpandedView() {
        if (this.expandedView) {
            this.details.style.display = "none";
            this.toggleButton.innerText = "+";
        } else {
            this.details.style.display = "block";
            this.toggleButton.innerText = "-";
        }

        this.expandedView = !this.expandedView;
    }

    wireView() {
        document.getElementById(`milestone-button-edit-${this.milestone.milestoneId}`).addEventListener('click', () => this.editCallback());
        document.getElementById(`milestone-button-delete-${this.milestone.milestoneId}`).addEventListener("click", () => this.deleteCallback());

        this.toggleButton.addEventListener('click', this.toggleExpandedView.bind(this));
    }

    getSprints() {
        let html = "<label>Sprints in progress during this milestone: </label>";
        this.sprints.forEach(sprint => {
            if (this.milestone.startDate >= sprint.startDate && this.milestone.startDate <= sprint.endDate || this.milestone.endDate >= sprint.startDate && this.milestone.endDate <= sprint.endDate) {
                html += `<div class="milestone-sprint-details"  style="color: ${sprint.colour}">   - <span th:text="${sprint.name}: "></span><span>${DatetimeUtils.localToUserDMY(sprint.startDate)}</span> - <span>${DatetimeUtils.localToUserDMY(sprint.endDate)}</span>`;
            }
        });
        if (html === "<label>Sprints in progress during this milestone: </label>") {
            html += "<span>No sprints are overlapping with this milestone</span>"
        }
        return html;
    }

    dispose() {

    }
}