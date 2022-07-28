class EventView {
    expandedView = false;

    constructor(containerElement, sprints, event, deleteCallback, editCallback) {
        this.containerElement = containerElement;
        this.event = event;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
        this.sprints = sprints;
        this.modalDeleteContainer=document.getElementById(`modal-delete-open`);
        this.modalDeleteX=document.getElementById(`modal-delete-x`);
        this.modalDeleteCancel=document.getElementById(`modal-delete-cancel`);
        this.modalDeleteConfirm=document.getElementById(`modal-delete-confirm`);

        this.constructView();
        this.wireView();
    }

    /**
     * Adds populated HTML to EventView.
     */
    constructView() {
        this.containerElement.innerHTML = `
    <div class="crud">
            <button class="icon-button event-controls" id="event-button-delete-${this.event.eventId}" data-privilege="teacher"><span class="material-icons">clear</span></button>
            <button class="icon-button event-controls" id="event-button-edit-${this.event.eventId}" data-privilege="teacher"><span class="material-icons" >edit</span></button>
            <button class="button visibility-button toggle-event-details" id="toggle-event-details-${this.event.eventId}"><span class='material-icons'>visibility_off</span></button>
    </div>
    <div class="editing-live-update" id="event-form-${this.event.eventId}"></div>
    <div class="events-title">
        <span id="event-title-text-${this.event.eventId}" style="font-style: italic;"></span> | <span id="start-date-${this.event.eventId}"></span> - <span id="end-date-${this.event.eventId}"></span>

    </div>
    <div class="events-details" id="event-details-${this.event.eventId}">
        <div class="event-description" id="event-description-${this.event.eventId}"></div>
        <div class="events-sprints" id="event-sprints-${this.event.eventId}"></div>
    </div>
    <div class="colour-block" id="event-colour-block-${this.event.eventId}"></div>
    `;

        this.toggleButton = document.getElementById(`toggle-event-details-${this.event.eventId}`);
        this.description = document.getElementById(`event-description-${this.event.eventId}`);
        this.details = document.getElementById(`event-details-${this.event.eventId}`);
        this.eventSprints = document.getElementById(`event-sprints-${this.event.eventId}`);

        document.getElementById(`event-title-text-${this.event.eventId}`).innerText = this.event.name;
        this.description.innerText = "Description: " + this.event.description;
        this.eventSprints.innerHTML = this.getSprints();
        this.sprints.forEach((sprint) => {
            if (document.getElementById(`event-sprint-name-${this.event.eventId}-${sprint.sprintId}`)) {
                document.getElementById(`event-sprint-name-${this.event.eventId}-${sprint.sprintId}`).innerText = sprint.name + ': '
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
    openDeleteModal(){
        this.modalDeleteContainer.style.display='block';
        this.modalDeleteX.addEventListener("click",()=>this.cancelDeleteModal())
        this.modalDeleteCancel.addEventListener("click",()=>this.cancelDeleteModal())
        this.modalDeleteConfirm.addEventListener("click",()=>this.confirmDeleteModal())


    }
    cancelDeleteModal(){
        this.modalDeleteContainer.style.display='none';
        this.modalDeleteX.removeEventListener("click",()=>this.cancelDeleteModal())
        this.modalDeleteCancel.removeEventListener("click",()=>this.cancelDeleteModal())
        this.modalDeleteConfirm.removeEventListener("click",()=>this.confirmDeleteModal())

    }
    confirmDeleteModal(){
        this.modalDeleteContainer.style.display='none';
        this.modalDeleteX.removeEventListener("click",()=>this.cancelDeleteModal())
        this.modalDeleteCancel.removeEventListener("click",()=>this.cancelDeleteModal())
        this.modalDeleteConfirm.removeEventListener("click",()=>this.confirmDeleteModal())
        Socket.saveEdit(this.event.eventId)
        this.deleteCallback()
    }
    wireView() {
        document.getElementById(`event-button-edit-${this.event.eventId}`).addEventListener('click', () => this.editCallback());
        document.getElementById(`event-button-delete-${this.event.eventId}`).addEventListener("click", () => this.openDeleteModal());

        this.toggleButton.addEventListener('click', this.toggleExpandedView.bind(this));
    }

    getSprints() {
        let html = "<label>Sprints in progress during this event: </label>";
        let foundSprints = false
        document.getElementById(`event-colour-block-${this.event.eventId}`).style.display="none";
        //Uses linear gradient to make the coloured line
        let gradient = "linear-gradient(45deg,"
        this.sprints.forEach(sprint => {
            if (this.event.startDate >= sprint.startDate && this.event.startDate <= sprint.endDate || this.event.endDate >= sprint.startDate && this.event.endDate <= sprint.endDate
            || this.event.startDate <= sprint.startDate && this.event.endDate >= sprint.endDate) {
                html += `<div class="event-sprint-details" style="color: ${sprint.colour}">   - <span id="event-sprint-name-${this.event.eventId}-${sprint.sprintId}"></span><span>${DatetimeUtils.localToUserDMY(sprint.startDate)}</span> - <span>${DatetimeUtils.localToUserDMY(sprint.endDate)}</span>`;
                foundSprints = true

                //Done twice to handle cases of single sprint. Displays block if a sprint contains the event
                gradient+=sprint.colour+","
                gradient+=sprint.colour+","
                document.getElementById(`event-colour-block-${this.event.eventId}`).style.display="block";
            }
        }

        );
        //Splices the last comma out of the linear gradient so it compiles. Sets the line colour
        gradient=gradient.slice(0, -1) + ')';
        document.getElementById(`event-colour-block-${this.event.eventId}`).style.background=gradient;

        if (!foundSprints) {
            html = "<label>No sprints are overlapping with this event</label>"
        }
        return html;
    }

    dispose() {

    }

}