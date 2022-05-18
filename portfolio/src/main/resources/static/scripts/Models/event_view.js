class EventView {
    expandedView = false;

    constructor(containerElement, sprints, event, deleteCallback, editCallback) {
        this.containerElement = containerElement;
        this.event = event;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
        this.sprints = sprints;

        this.constructView();
        this.wireView();
    }

    /**
     * Adds populated HTML to EventView.
     */
    constructView() {
        this.containerElement.innerHTML = `
    <div class="event-title">
        <span id="event-title-text-${this.event.eventId}" style="font-style: italic;"></span> | <span id="start-date-${this.event.eventId}"></span> - <span id="end-date-${this.event.eventId}"></span>

        <span class="crud">
            <button class="button event-controls" id="event-button-edit-${this.event.eventId}" data-privilege="teacher">Edit</button>
            <button class="button event-controls" id="event-button-delete-${this.event.eventId}" data-privilege="teacher">Delete</button>
            <button class="button toggle-event-details" id="toggle-event-details-${this.event.eventId}">+</button>
        </span>
    </div>
    <div class="event-details" id="event-details-${this.event.eventId}">
        <div class="event-description" id="event-description-${this.event.eventId}"></div>
        <div class="event-sprints" id="event-sprints-${this.event.eventId}"></div>
    </div>
    
    `;

        this.toggleButton = document.getElementById(`toggle-event-details-${this.event.eventId}`);
        this.description = document.getElementById(`event-description-${this.event.eventId}`);
        this.details = document.getElementById(`event-details-${this.event.eventId}`);
        this.eventSprints = document.getElementById(`event-sprints-${this.event.eventId}`);

        document.getElementById(`event-title-text-${this.event.eventId}`).innerText = this.event.name;
        this.description.innerText = "Description: " + this.event.description;
        this.eventSprints.innerHTML = this.getSprints();
        document.getElementById(`start-date-${this.event.eventId}`).innerText = DatetimeUtils.localToUserDMY(this.event.startDate);
        const displayedDate = new Date(this.event.endDate.valueOf());
        if (DatetimeUtils.getTimeStringIfNonZeroLocally(this.event.endDate) === null) {
            displayedDate.setDate(displayedDate.getDate() - 1);
        }
        document.getElementById(`end-date-${this.event.eventId}`).innerText = DatetimeUtils.localToUserDMY(displayedDate);
    }

    /**
     * Toggles expanded view and button for events.
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
        document.getElementById(`event-button-edit-${this.event.eventId}`).addEventListener('click', () => this.editCallback());
        document.getElementById(`event-button-delete-${this.event.eventId}`).addEventListener("click", () => this.deleteCallback());

        this.toggleButton.addEventListener('click', this.toggleExpandedView.bind(this));
    }

    getSprints() {
        let html = "<label>Sprints in progress during this event: </label>";
        this.sprints.forEach(sprint => {
            if (this.event.startDate >= sprint.startDate && this.event.startDate <= sprint.endDate || this.event.endDate >= sprint.startDate && this.event.endDate <= sprint.endDate) {
                html += `<div class="event-sprint-details">   - <span>${sprint.name}: </span><span>${DatetimeUtils.localToUserDMY(sprint.startDate)}</span> - <span>${DatetimeUtils.localToUserDMY(sprint.endDate)}</span>`;
            }
        });
        if (html === "<label>Sprints in progress during this event: </label>") {
            html += "<span>No sprints are overlapping with this event</span>"
        }
        return html;
    }

    dispose() {

    }

}