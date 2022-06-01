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
    <div class="crud">
            <button class="icon-button event-controls" id="event-button-edit-${this.event.eventId}" data-privilege="teacher"><span class="material-icons" >edit</span></button>
            <button class="icon-button event-controls" id="event-button-delete-${this.event.eventId}" data-privilege="teacher"><span class="material-icons">clear</span></button>
            <button class="button visibility-button toggle-event-details" id="toggle-event-details-${this.event.eventId}"><span class='material-icons'>visibility_off</span></button>
    </div>
    <div class="events-title">
        <span id="event-title-text-${this.event.eventId}" style="font-style: italic;"></span> | <span id="start-date-${this.event.eventId}"></span> - <span id="end-date-${this.event.eventId}"></span>

    </div>
    <div class="events-details" id="event-details-${this.event.eventId}">
        <div class="event-description" id="event-description-${this.event.eventId}"></div>
        <div class="events-sprints" id="event-sprints-${this.event.eventId}"></div>
    </div>
    
    `;

        this.toggleButton = document.getElementById(`toggle-event-details-${this.event.eventId}`);
        this.description = document.getElementById(`event-description-${this.event.eventId}`);
        this.details = document.getElementById(`event-details-${this.event.eventId}`);
        this.eventSprints = document.getElementById(`event-sprints-${this.event.eventId}`);

        document.getElementById(`event-title-text-${this.event.eventId}`).innerText = this.event.name;
        this.description.innerText = "Description: " + this.event.description;
        this.eventSprints.innerHTML = this.getSprints();
        this.sprints.forEach((sprint) => {
            if (document.getElementById(`event-sprint-name-${sprint.id}`)) {
                document.getElementById(`event-sprint-name-${sprint.id}`).innerText = sprint.name + ': '
            }
        })
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
            this.toggleButton.innerHTML = "<span class='material-icons'>visibility_off</span>";
        }
        else {
            this.details.style.display = "block";
            this.toggleButton.innerHTML = "<span class='material-icons'>visibility</span>";
        }

        this.expandedView = !this.expandedView;
    }

    wireView() {
        document.getElementById(`event-button-edit-${this.event.eventId}`).addEventListener('click', () => this.editCallback());
        document.getElementById(`event-button-delete-${this.event.eventId}`).addEventListener("click", () => this.deleteCallback());

        this.toggleButton.addEventListener('click', this.toggleExpandedView.bind(this));
    }

    getSprints() {
        let html = "";
        this.sprints.forEach(sprint => {
            if (this.event.startDate >= sprint.startDate && this.event.startDate <= sprint.endDate || this.event.endDate >= sprint.startDate && this.event.endDate <= sprint.endDate) {
                html += `<div class="event-sprint-details" style="color: ${sprint.colour}">   - <span id="event-sprint-name-${sprint.id}"></span><span>${DatetimeUtils.localToUserDMY(sprint.startDate)}</span> - <span>${DatetimeUtils.localToUserDMY(sprint.endDate)}</span>`;
            }
        });
        if (html === "") {
            html += "<label>No sprints are overlapping with this event</label>"
        } else {
            html = '<label>Sprints in progress during this event: </label>' + html
        }
        return html;
    }

    dispose() {

    }

}