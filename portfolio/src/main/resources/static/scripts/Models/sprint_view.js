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
    <div class="colour-block" id="sprint-colour-block-${this.sprint.sprintId}"></div>
    <div id = "${this.sprint.sprintId}" class = "raised-card colour-block-card">
        <div class="sprints" id="sprints-container-${this.sprint.sprintId}"></div>
        <div class="events-title">
            <span id="sprint-order-text-${this.sprint.sprintId}"></span>: <span id="sprint-title-text-${this.sprint.sprintId}" style="font-style: italic;"></span> | <span id="start-date-${this.sprint.sprintId}"></span> - <span id="end-date-${this.sprint.sprintId}"></span>
    
            <span class="crud">
                <button class="icon-button sprint-controls" id="sprint-button-edit-${this.sprint.sprintId}" data-privilege="teacher"><span class="material-icons md-11">edit</span></button>
                <button class="icon-button sprint-controls" id="sprint-button-delete-${this.sprint.sprintId}" data-privilege="teacher"><span class="material-icons md-11">clear</span></button>
                <button class="button visibility-button toggle-sprint-details" id="toggle-sprint-details-${this.sprint.sprintId}"><span class='material-icons'>visibility_off</span></button>
            </span>
        </div>
    
        <div class="events-details" id="sprint-details-${this.sprint.sprintId}">
            <label class="event-description-label" id="event-description-label-${this.sprint.sprintId}"></label>
            <div  id="sprint-description-${this.sprint.sprintId}" class="event-description"></div>
            <label>Occurences during this sprint:</label>
            <div>
                <div class="sprint-events" id="sprint-events-${this.sprint.sprintId}"></div>
                <div class="sprint-deadlines" id="sprint-deadlines-${this.sprint.sprintId}"></div>
                <div class="sprint-milestones" id="sprint-milestones-${this.sprint.sprintId}"></div>
            </div>
        </div>
    </div>
    `;

        this.toggleButton = document.getElementById(`toggle-sprint-details-${this.sprint.sprintId}`);
        this.sprintDetails = document.getElementById(`sprint-details-${this.sprint.sprintId}`);
        this.descriptionLabel = document.getElementById(`event-description-label-${this.sprint.sprintId}`);
        this.description = document.getElementById(`sprint-description-${this.sprint.sprintId}`);
        this.colourBlock = document.getElementById(`sprint-colour-block-${this.sprint.sprintId}`);
        this.details = document.getElementById(`sprint-details-${this.sprint.sprintId}`);
        this.sprintEvents = document.getElementById(`sprint-events-${this.sprint.sprintId}`);
        this.sprintDeadlines = document.getElementById(`sprint-deadlines-${this.sprint.sprintId}`);
        this.sprintMilestones = document.getElementById(`sprint-milestones-${this.sprint.sprintId}`);
        document.getElementById(`sprint-order-text-${this.sprint.sprintId}`).innerText = `Sprint ${this.sprint.orderNumber}`;
        document.getElementById(`sprint-title-text-${this.sprint.sprintId}`).innerText = this.sprint.name;
        if(this.sprint.description !== ''){
            this.descriptionLabel.innerText = "Description:\n";
        }
        this.description.innerText = this.sprint.description;

        this.sprintEvents.innerHTML = this.getEvents();
        this.events.forEach((event) => {
            if (document.getElementById(`sprint-event-name-${this.sprint.sprintId}-${event.eventId}`)) {
                document.getElementById(`sprint-event-name-${this.sprint.sprintId}-${event.eventId}`).innerText = event.name + ':'
            }
        })
        this.sprintDeadlines.innerHTML = this.getDeadlines();
        this.deadlines.forEach((deadline) => {
            if (document.getElementById(`sprint-deadline-name-${this.sprint.sprintId}-${deadline.deadlineId}`)) {
                document.getElementById(`sprint-deadline-name-${this.sprint.sprintId}-${deadline.deadlineId}`).innerText = deadline.name + ':'
            }
        })
        this.sprintMilestones.innerHTML = this.getMilestones();
        this.milestones.forEach((milestone) => {
            if (document.getElementById(`sprint-milestone-name-${this.sprint.sprintId}-${milestone.milestoneId}`)) {
                document.getElementById(`sprint-milestone-name-${this.sprint.sprintId}-${milestone.milestoneId}`).innerText = milestone.name + ':'
            }
        })
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
            this.toggleButton.innerHTML = "<span class='material-icons'>visibility_off</span>";
        }
        else {
            this.details.style.display = "block";
            this.toggleButton.innerHTML = "<span class='material-icons'>visibility</span>";
        }

        this.expandedView = !this.expandedView;
    }

    wireView() {
        document.getElementById(`sprint-button-edit-${this.sprint.sprintId}`).addEventListener('click', () => this.editCallback());
        document.getElementById(`sprint-button-delete-${this.sprint.sprintId}`).addEventListener("click", () => this.deleteCallback());

        this.toggleButton.addEventListener('click', this.toggleExpandedView.bind(this));
    }

    getEvents() {
        let html = "";
        this.events.forEach(event => {
            if (event.startDate >= this.sprint.startDate && event.startDate <= this.sprint.endDate || event.endDate >= this.sprint.startDate && event.endDate <= this.sprint.endDate) {


                let gradient = "linear-gradient(45deg,"
                this.sprints.forEach(sprint => {
                    if (event.startDate >= sprint.startDate && event.startDate <= sprint.endDate
                        || event.endDate >= sprint.startDate && event.endDate <= sprint.endDate
                        || event.startDate <= sprint.startDate && event.endDate >= sprint.endDate) {
                        //Done twice to handle cases of single sprint. Displays block if a sprint contains the event
                        gradient+=sprint.colour+","
                        gradient+=sprint.colour+","
                    }
                });
                //Splices the last comma out of the linear gradient so it compiles. Sets the line colour
                gradient=gradient.slice(0, -1) + ')';
                gradient = "background: " + gradient

                html += `
                <div class="event-sprint-container">
                    <div class="event-sprint-details">
                        <span class="material-icons" style="font-size: 14px">event</span>
                        <span id="sprint-event-name-${this.sprint.sprintId}-${event.eventId}" class="sprint-event-name"></span>
                        <span> </span>`

                if (event.startDate >= this.sprint.startDate && event.startDate <= this.sprint.endDate) {
                    html += `<span>${DatetimeUtils.localToUserDMY(event.startDate)}</span> - `;
                } else {
                    let found = false;
                    this.sprints.forEach(sprint => {
                        if (event.startDate >= sprint.startDate && event.startDate <= sprint.endDate) {
                            html += `<span>${DatetimeUtils.localToUserDMY(event.startDate)}</span> - `;
                            found = true;
                        }
                    });
                    if (!found) {
                        html += `<span">${DatetimeUtils.localToUserDMY(event.startDate)}</span> - `;
                    }
                }
                if (event.endDate >= this.sprint.startDate && event.endDate <= this.sprint.endDate) {
                    html += `<span>${DatetimeUtils.localToUserDMY(event.endDate)}</span><br>`;
                } else {
                    let found = false;
                    this.sprints.forEach(sprint => {
                        if (event.endDate >= sprint.startDate && event.endDate <= sprint.endDate) {
                            html += `<span>${DatetimeUtils.localToUserDMY(event.endDate)}</span><br>`;
                            found = true;
                        }
                    });
                    if (!found) {
                        html += `<span>${DatetimeUtils.localToUserDMY(event.endDate)}</span>`;
                    }
                }
                html += `
                    </div>
                    <div style="${gradient}" class="event-sprint-colour-block"></div>
                </div>`;

            }
        });

        return html;
    }

    getDeadlines() {
        let html = "";
        this.deadlines.forEach(deadline => {
            if (deadline.startDate >= this.sprint.startDate && deadline.startDate <= this.sprint.endDate) {

                let gradient = "linear-gradient(45deg,"
                this.sprints.forEach(sprint => {
                    if (deadline.startDate >= sprint.startDate && deadline.startDate <= sprint.endDate) {
                        //Done twice to handle cases of single sprint. Displays block if a sprint contains the event
                        gradient+=sprint.colour+","
                        gradient+=sprint.colour+","
                    }
                });
                //Splices the last comma out of the linear gradient so it compiles. Sets the line colour
                gradient=gradient.slice(0, -1) + ')';
                gradient = "background: " + gradient

                html += `
                <div class="event-sprint-container">
                    <div class="event-sprint-details">
                        <span class="material-icons" style="font-size: 14px">timer</span>
                        <span id="sprint-deadline-name-${this.sprint.sprintId}-${deadline.deadlineId}" class="sprint-event-name"></span>
                        <span>${DatetimeUtils.localToUserDMY(deadline.startDate)}</span><br>
                    </div>
                    <div style="${gradient}" class="event-sprint-colour-block"></div>
                </div>`;
            }
        });

        return html;
    }

    getMilestones() {
        let html = "";
        this.milestones.forEach(milestone => {
            if (milestone.startDate >= this.sprint.startDate && milestone.startDate <= this.sprint.endDate) {

                let gradient = "linear-gradient(45deg,"
                this.sprints.forEach(sprint => {
                    if (milestone.startDate >= sprint.startDate && milestone.startDate <= sprint.endDate) {
                        //Done twice to handle cases of single sprint. Displays block if a sprint contains the event
                        gradient+=sprint.colour+","
                        gradient+=sprint.colour+","
                    }
                });
                //Splices the last comma out of the linear gradient so it compiles. Sets the line colour
                gradient=gradient.slice(0, -1) + ')';
                gradient = "background: " + gradient

                html += `
                <div class="event-sprint-container">
                    <div class="event-sprint-details">
                        <span class="material-icons" style="font-size: 14px">flag</span>
                        <span id="sprint-milestone-name-${this.sprint.sprintId}-${milestone.milestoneId}" class="sprint-event-name"></span>
                        <span>${DatetimeUtils.localToUserDMY(milestone.startDate)}</span><br>
                    </div>
                    <div style="${gradient}" class="event-sprint-colour-block"></div>
                </div>`;
            }
        });

        return html;
    }

    dispose() {

    }
}