/**
 * Handles editing view for both Projects and Sprints.
 */
class Editor {
    startDateEdited = false
    endDateEdited = false

    constructor(containerElement, title, entityData, cancelCallback, submitCallback, customDatesValidator, project, allowTimeInput = false) {
        this.allowTimeInput = allowTimeInput;
        this.containerElement = containerElement;
        this.title = title;
        this.initialData = entityData;
        this.entityId = entityData.id ?? entityData.sprintId;
        this.project = project;

        this.cancelCallback = cancelCallback;
        this.submitCallback = submitCallback;
        this.customDatesValidator = customDatesValidator ?? function() {return null;};

        this.constructView();
        this.fillDefaults();
        this.wireView();
    }

    /**
     * Constructs view for the Projects, populating the HTML.
     */
    constructView() {
        this.containerElement.innerHTML = `
      <div class="edit-project-section" id="edit-project-section-${this.entityId}">
          <p class="edit-section-title" id="edit-section-form-title-${this.entityId}">Edit Details:</p>
          <form class="user-inputs" id="edit-project-section-form-${this.entityId}">

              <div class="name">
                <label>Name*:</label>
                <input type="text" name="project-name" class="input-name" id="edit-project-name-${this.entityId}" maxlength="32" oninput="displayCharactersRemaining(this, 32)" />
                <span class="input-length" id="edit-name-length">0 / 32</span>
                <br>
                <div id="edit-project-name-error-${this.entityId}" class="form-error" style="display: none;"></div><br>
              </div>
              
              <div class="description">
                  <label>Description:</label>
                  <textarea name="description" id="edit-description-${this.entityId}" cols="50" rows="10" maxlength="1024" oninput="displayCharactersRemaining(this, 1024)"></textarea>
                  <span class="input-length" id="edit-description-length">0 / 1024</span>
                  <br><br>
              </div>
              <label id="start-date-label-${this.entityId}">Start Date*:</label>
              <input type=${this.allowTimeInput ? "datetime-local" : "date"} name="start-date" class="date-input" id="edit-start-date-${this.entityId}">
                <br/>
              <label id="end-date-label-${this.entityId}">End Date*:</label>
              <input type=${this.allowTimeInput ? "datetime-local" : "date"} name="end-date" class="date-input" id="edit-end-date-${this.entityId}">
                <br/>
              <label id="color-label-${this.entityId}">Colour*:</label>
              <input type="color" name="colour" id="edit-colour-${this.entityId}"/>
              <br/>
              <div id="edit-project-date-error-${this.entityId}" class="form-error" style="display: none;"></div><br/>
              <div id="date-collisions-${this.entityId}" class="date-collisions"></div>
              
              <p>* = Required field.</p>
          </form>
          <div class="save-buttons">
              <button class="button save" id="edit-save-button-${this.entityId}">Save</button>
              <button class="button cancel" id="edit-cancel-button-${this.entityId}">Cancel</button>
          </div>
      </div>
    `
        document.getElementById(`edit-section-form-title-${this.entityId}`).innerText = this.title;

        this.nameInput = document.getElementById(`edit-project-name-${this.entityId}`);
        this.descriptionInput = document.getElementById(`edit-description-${this.entityId}`);
        this.startDateInput = document.getElementById(`edit-start-date-${this.entityId}`);
        this.endDateInput = document.getElementById(`edit-end-date-${this.entityId}`);
        this.startDateLabel  = document.getElementById(`start-date-label-${this.entityId}`);
        this.endDateLabel  = document.getElementById(`end-date-label-${this.entityId}`);
        this.dateCollisions = document.getElementById(`date-collisions-${this.entityId}`);

        this.colourInput = document.getElementById(`edit-colour-${this.entityId}`);
        if (!(this.title === "New sprint details:") && !(this.title === "Edit sprint details:")) {
            this.colourInput.outerHTML = "";
            document.getElementById(`color-label-${this.entityId}`).outerHTML = "";
        }

        if (this.title === "New milestone details:" || this.title === "Edit milestone details:" || this.title === "New deadline details:" || this.title === "Edit deadline details:") {
            this.endDateInput.outerHTML = "";
            this.endDateInput = document.getElementById(`edit-start-date-${this.entityId}`);
            this.startDateLabel.innerText = "Date*:";
            this.endDateLabel.outerHTML = "";
        }

        console.log(this.containerElement);
        console.log(this.initialData)

        this.saveButton = document.getElementById(`edit-save-button-${this.entityId}`);

        // Error fields
        this.nameErrorEl = document.getElementById(`edit-project-name-error-${this.entityId}`);
        this.dateErrorEl = document.getElementById(`edit-project-date-error-${this.entityId}`);
    }

    /**
     * Sets error message for invalid names.
     * @param message
     */
    setNameError(message) {
        if (message) {
            this.nameErrorEl.style.display = "block";
            this.nameErrorEl.innerText = message;
        } else {
            this.nameErrorEl.style.display = "none";
        }
    }

    /**
     * Sets error message for invalid dates.
     * @param message
     */
    setDateError(message) {
        if (message) {
            this.dateErrorEl.style.display = "block";
            this.dateErrorEl.innerText = message;
        } else {
            this.dateErrorEl.style.display = "none";
        }
    }


    //TODO this validation need to cover the jpa validation in the corresponding Entity class or we get
    // server error pop ups in the front end; these 2 validations should be consolidated
    /**
     * Sets the initial defaults for the inputs.
     */
    fillDefaults() {
        let displayDate
        this.nameInput.value = this.initialData.name ?? "";
        this.descriptionInput.value = this.initialData.description ?? "";
        if (this.initialData.startDate) {
            displayDate = new Date(this.initialData.startDate.valueOf());
            if (this.allowTimeInput) {
                displayDate.setHours(displayDate.getHours() + 12)
            }
        }
        this.startDateInput.value = (this.initialData.startDate) ? this.allowTimeInput ? DatetimeUtils.localToNetworkString(displayDate).slice(0, 19) : DatetimeUtils.toLocalYMD(this.initialData.startDate) : "";
        this.colourInput.value = this.initialData.colour ?? "#000000";
        if (this.initialData.endDate) {
            const displayedDate = new Date(this.initialData.endDate.valueOf());
            if (!this.allowTimeInput && DatetimeUtils.getTimeStringIfNonZeroLocally(this.initialData.endDate) === null) {
                // Only go back a day if there is no time specified and we don't allow time input
                displayedDate.setDate(displayedDate.getDate() - 1);
            } else {
                displayedDate.setHours(displayedDate.getHours() + 12)
            }
            this.endDateInput.value = this.allowTimeInput ? DatetimeUtils.localToNetworkString(displayedDate).slice(0, 19) : DatetimeUtils.toLocalYMD(displayedDate);
        } else {
            this.endDateInput.value = "";
        }
    }

    /**
     * Checks that the name field is valid and populates the error field if not.
     *
     * @return true if the fields are valid. false otherwise.
     */
    validateName() {
        if (!this.nameInput.value) {
            this.setNameError("A name is required.");
            return false;
        }

        if (this.nameInput.value.trim() === "") {
            this.setNameError("Name must not contain only whitespaces.");
            return false;
        }

        if (this.title === "New event details:" && !this.nameInput.value.match("(?<=\\s|^)[a-zA-Z\s]*(?=[.,;:]?\\s|$)")) {
            this.setNameError("Name can only contain characters");
            return false;
        }

        this.setNameError(null);
        return true;
    }

    /**
     * Gets the start date from user input, otherwise defaults to initial default value.
     */
    getStartDateInputValue() {
        if (!this.startDateEdited) {
            return this.initialData.startDate ?? null;
        }
        const rawValue = this.startDateInput.value;
        if (rawValue) {
            return new Date(Date.parse(rawValue));
        }
        return null;
    }

    /**
     * Gets the end date from user input, otherwise defaults to initial default value.
     */
    getEndDateInputValue() {
        if (!this.endDateEdited) {
            return this.initialData.endDate ?? null;
        }
        const rawValue = this.endDateInput.value;
        if (rawValue) {
            let dayAfter = new Date(Date.parse(rawValue));

            if (!this.allowTimeInput) {
                dayAfter.setDate(dayAfter.getDate() + 1);
            } else {

            }
            return dayAfter;
        }
        return null;
    }

    getColour() {
        return this.colourInput.value;
    }

    /**
     * Checks that the date fields are valid and populates error fields if not.
     *
     * @return true if the fields are valid. false otherwise.
     */
    validateDates() {
        const startDate = this.getStartDateInputValue();
        const endDate = this.getEndDateInputValue();

        if (startDate === null || endDate === null) {
            this.setDateError("The date fields are required.");
            return false;
        } else {
            if (endDate < startDate) {
                this.setDateError("The end date must be after the start date.");
                return false;
            }
        }

        const customError = this.customDatesValidator(startDate, endDate);
        if (customError !== null) {
            this.setDateError(customError);
            return false;
        }
        this.setDateError(null);
        return true;
    }

    /**
     * Validates names and dates, if valid submits the form.
     */
    async validateAndSubmit() {
        const hasErrors = [
            this.validateName(),
            this.validateDates()
        ].indexOf(false) !== -1;

        console.log(`hasErrors: ${hasErrors}`);
        if (!hasErrors) {
            try {
                this.saveButton.innerText = "loading...";
                this.saveButton.setAttribute("disabled", "true");

                await this.submitCallback({
                    name: this.nameInput.value,
                    description: this.descriptionInput.value,
                    startDate: this.getStartDateInputValue(),
                    endDate: this.getEndDateInputValue(),
                    colour: this.getColour()
                })
            } finally {
                this.saveButton.innerText = "Save";
                this.saveButton.setAttribute("disabled", "false");
            }

        }
    }

    /**
     * Attach listeners to input fields.
     */
    wireView() {
        this.saveButton.addEventListener('click', () => this.validateAndSubmit());
        document.getElementById(`edit-project-section-form-${this.entityId}`).addEventListener('submit', (evt) => {
            evt.preventDefault();
            this.validateAndSubmit();
        });
        document.getElementById(`edit-cancel-button-${this.entityId}`).addEventListener('click', () => this.cancelCallback());

        this.nameInput.addEventListener('change', this.validateName.bind(this));  // Is only called after the text field loses focus.
        this.nameInput.addEventListener('input', this.validateName.bind(this));  // Ensure that the validator is called as the user types to provide real-time feedback.
        this.startDateInput.addEventListener('change', () => {
            this.startDateEdited = true;
            this.getRelatedEvents();
            this.validateDates();
        });

        this.endDateInput.addEventListener('change', () => {
            this.endDateEdited = true;
            this.getRelatedEvents();
            this.validateDates();
        });
    }

    getRelatedEvents() {
        let found = false;
        let returnString = ""
        const startDate = new Date(this.getStartDateInputValue().getFullYear(), this.getStartDateInputValue().getMonth(), this.getStartDateInputValue().getDate());
        const endDate = new Date(this.getStartDateInputValue().getFullYear(), this.getStartDateInputValue().getMonth(), this.getStartDateInputValue().getDate());
        if (this.title.includes("event")) {
            this.project.milestones.forEach((milestone) => {
                const milestoneNoTime = new Date(milestone.startDate.getFullYear(), milestone.startDate.getMonth(), milestone.startDate.getDate()).getTime();
                if (milestoneNoTime === startDate.getTime()) {
                    found = true;
                    returnString += `<p> Milestone: ${milestone.name}</p>`
                }
            })
            this.project.deadlines.forEach((deadline) => {
                const deadlineNoTime = new Date(deadline.startDate.getFullYear(), deadline.startDate.getMonth(), deadline.startDate.getDate()).getTime();
                if (deadlineNoTime === startDate.getTime()) {
                    found = true;
                    returnString += `<p> Deadline: ${deadline.name}</p>`
                }
            })
        } else if (this.title.includes("milestone")) {
            this.project.events.forEach((event) => {
                const eventStartNoTime = new Date(event.startDate.getFullYear(), event.startDate.getMonth(), event.startDate.getDate()).getTime();
                const eventEndNoTime = new Date(event.endDate.getFullYear(), event.endDate.getMonth(), event.endDate.getDate()).getTime();
                if (eventStartNoTime === startDate.getTime()) {
                    found = true;
                    returnString += `<p> Event Start Date: ${event.name}</p>`
                }
                if (eventEndNoTime === startDate.getTime()) {
                    found = true;
                    returnString +=`<p> Event End Date: ${event.name}</p>`
                }
            })
            this.project.deadlines.forEach((deadline) => {
                const deadlineNoTime = new Date(deadline.startDate.getFullYear(), deadline.startDate.getMonth(), deadline.startDate.getDate()).getTime();
                if (deadlineNoTime === startDate.getTime()) {
                    found = true;
                    returnString += `<p> Deadline: ${deadline.name}</p>`
                }
            })
        } else if (this.title.includes("deadline")) {
            this.project.events.forEach((event) => {
                const eventStartNoTime = new Date(event.startDate.getFullYear(), event.startDate.getMonth(), event.startDate.getDate()).getTime();
                const eventEndNoTime = new Date(event.endDate.getFullYear(), event.endDate.getMonth(), event.endDate.getDate()).getTime();
                if (eventStartNoTime === startDate.getTime()) {
                    found = true;
                    returnString += `<p> Event Start Date: ${event.name}</p>`
                }
                if (eventEndNoTime === startDate.getTime()) {
                    found = true;
                    returnString +=`<p> Event End Date: ${event.name}</p>`
                }
            })
            this.project.milestones.forEach((milestone) => {
                const milestoneNoTime = new Date(milestone.startDate.getFullYear(), milestone.startDate.getMonth(), milestone.startDate.getDate()).getTime();
                if (milestoneNoTime === startDate.getTime()) {
                    found = true;
                    returnString += `<p> Milestone: ${milestone.name}</p>`
                }
            })
        }
        if (found) {
            returnString = "<label>The following will occur on the dates selected: </label>" + returnString
        }
        this.dateCollisions.innerHTML = returnString;
    }

    dispose() {

    }

    /**
     * Provides a validator function for checking that the proposed dates for a sprint are allowed for the given project.
     *
     * This is a convenience method that Editor consumers can use to pass directly in to the Editor constructor.
     *
     * @param project to check sprint dates against.
     */
    static makeProjectSprintDatesValidator(project, sprintIdUnderEdit) {
        return (startDate, endDate) => {
            if (startDate < project.startDate || project.endDate < endDate) {
                return "Sprint must fit within the project dates.";
            }
            else {
                // Find overlaps...
                for (const sprint of project.sprints.values()) {
                    if (sprint.sprintId === sprintIdUnderEdit) {
                        continue;
                    }

                    // Taken from: https://stackoverflow.com/a/325964
                    if (startDate < sprint.endDate && endDate > sprint.startDate) {
                        return `This date range overlaps with Sprint ${sprint.orderNumber}. Please choose a non-overlapping date range.`;
                    }
                }
            }

            return null;
        }
    }

    static makeProjectProjectDatesValidator(project) {
        return (startDate, endDate) => {
            let date = new Date();
            date.setFullYear( date.getFullYear() - 1 );
            if (date > startDate) {
                return "Project cannot start more than a year ago"
            }

            for (const sprint of project.sprints.values()) {
                // Taken from: https://stackoverflow.com/a/325964
                if (startDate > sprint.startDate || endDate < sprint.endDate) {
                    return `This date range overlaps with Sprint ${sprint.orderNumber}. Please choose a non-overlapping date range.`;
                }
            }
            return null;
        }
    }

    static makeProjectEventDatesValidator(project) {
        return (startDate, endDate) => {
            if (startDate < project.startDate || project.endDate < endDate) {
                return "Event must fit within the project dates.";
            }

            return null;
        }
    }

    static makeProjectMilestoneDatesValidator(project) {
        return (startDate) => {
            if (startDate < project.startDate || project.endDate < startDate) {
                return "Milestone must fit within the project dates.";
            }

            return null;
        }
    }

    static makeProjectDeadlineDatesValidator(project) {
        return (startDate) => {
            if (startDate < project.startDate || project.endDate < startDate) {
                return "Deadline must fit within the project dates.";
            }

            return null;
        }
    }
}