'use strict';

const LoadingStatus = {
  NotYetAttempted: "NotYetAttempted",
  Pending: "Pending",
  Done: "Done",
  Error: "Error"
};

class PortfolioNetworkError extends Error {
  constructor(message, ...args) {
    super(message, ...args);
  }
}

/**
 * Handles network errors.
 */
class ErrorHandlerUtils {
  static async handleNetworkError(response, context) {
    const body = await response.text();
    if (body) {
      alert(`An error occurred. Server responded: ${body}`);
    }
    else {
      alert(`An error occurred. Server responded: ${response.statusText} (${response.status}).`)
    }

    throw new PortfolioNetworkError(`A server error occurred when ${context}. Status: ${response.status} ${response.statusText}`);
  }

  static handleUnknownNetworkError(ex, context) {
    alert(`An unknown error occurred. Please try again.\n\n${ex}`);
    throw new PortfolioNetworkError(`An unknown error occurred when ${context}. Status: ${response.status} ${response.statusText}`);
  }
}

/**
 * Handles view of the Projects and populating the HTML.
 */
class ProjectView {
  showingSprints = false;
  showingEvents = false;

  addEventForm = null;
  addEventLoadingStatus = LoadingStatus.NotYetAttempted

  addSprintForm = null;
  addSprintLoadingStatus = LoadingStatus.NotYetAttempted;

  constructor(containerElement, project, editCallback, deleteCallback, sprintDeleteCallback, sprintUpdateCallback, eventDeleteCallback, eventUpdateCallback) {
    console.log("project", project)
    this.containerElement = containerElement;
    this.project = project;
    this.sprintContainer = null;
    this.sprints = new Map();
    this.eventContainer = null;
    this.events = new Map();
    this.editCallback = editCallback;
    this.deleteCallback = deleteCallback;
    this.sprintDeleteCallback = sprintDeleteCallback;
    this.sprintUpdateCallback = sprintUpdateCallback;
    this.eventDeleteCallback = eventDeleteCallback;
    this.eventUpdateCallback = eventUpdateCallback;

    this.constructAndPopulateView();
    this.wireView();
  }

  /**
   * Append a sprint element to the sprintElement and instantiate and store a sprint with the given data.
   */
  appendSprint(sprintData) {
    const sprintElement = document.createElement("div");
    sprintElement.classList.add("sprint-view", "raised-card");
    sprintElement.id = `sprint-view-${sprintElement.id}`;

    this.sprintContainer.appendChild(sprintElement);

    console.log("Binding sprint");

    console.log(sprintData.startDate);
    this.sprints.set(sprintData.sprintId, new Sprint(sprintElement, sprintData, this.project, this.sprintDeleteCallback, this.sprintUpdateCallback));

    console.log("Sprint bound");

  }

  appendEvent(eventData) {
    const eventElement = document.createElement("div")
    eventElement.classList.add("event-view", "raised-card");
    eventElement.id = `event-view-${eventElement.id}`;
    this.eventContainer.appendChild(eventElement);

    console.log("Binding event");

    this.events.set(eventData.eventId, new Event(eventElement, eventData, this.project, this.eventDeleteCallback, this.eventUpdateCallback));

    console.log("Event bound");
  }

  /**
   * Adds HTML in to the project container, with the main attributes of projects and sprints.
   */
  constructAndPopulateView() {
    this.containerElement.innerHTML = `
      <div class="project-title">
          <span class="project-title-text">
            <span id="project-title-text-${this.project.id}"></span> | <span id="project-startDate-${this.project.id}"></span> - <span id="project-endDate-${this.project.id}"></span>
          </span>   
          <span class="monthly-planner-redirect">
                  <button class="button monthly-planner-redirect-button" id="monthly-planner-redirect-button-${this.project.id}">View Monthly Planner</button>
          </span> 
          <span class="crud">
                  <button class="button edit-project" id="project-edit-button-${this.project.id}" data-privilege="teacher">Edit</button>
                  <button class="button" id="project-delete-button-${this.project.id}" data-privilege="teacher">Delete</button>
          </span>
      </div>
      <div>
          <div class="project-description" id="project-description-${this.project.id}"></div>
          <div class="add-view-controls">
              <button class="button add-sprint" id="add-sprint-button-${this.project.id}" data-privilege="teacher"> Add Sprint</button>
              <button class="button add-event" id="add-event-button-${this.project.id}" data-privilege="teacher"> Add Event</button>
          </div>
          <div class="toggle-view-controls">
              <button class="button toggle-sprints" id="toggle-sprint-button-${this.project.id}"> Show Sprints</button>
              <button class="button toggle-events" id="toggle-event-button-${this.project.id}"> Show Events</button>
          </div>    
      </div>
      <div class="events raised-card" id="events-container-${this.project.id}">
        <h1 class="event-section-title">Events:</h1>
      </div>
      <div class="sprints raised-card" id="sprints-container-${this.project.id}">
        <h1 class="event-section-title">Sprints:</h1>
      </div>
    `;

    document.getElementById(`project-title-text-${this.project.id}`).innerText = this.project.name;
    document.getElementById(`project-description-${this.project.id}`).innerText = this.project.description;
    document.getElementById(`project-startDate-${this.project.id}`).innerText = DatetimeUtils.localToUserDMY(this.project.startDate);
    const displayedDate = new Date(this.project.endDate.valueOf());
    displayedDate.setDate(displayedDate.getDate()  - 1);
    document.getElementById(`project-endDate-${this.project.id}`).innerText = DatetimeUtils.localToUserDMY(displayedDate);

    this.addSprintButton = document.getElementById(`add-sprint-button-${this.project.id}`);
    this.toggleSprintsButton = document.getElementById(`toggle-sprint-button-${this.project.id}`);
    this.sprintsContainer = document.getElementById(`sprints-container-${this.project.id}`);
    this.sprintContainer = document.getElementById(`sprints-container-${this.project.id}`);

    this.addEventButton = document.getElementById(`add-event-button-${this.project.id}`);
    this.toggleEventsButton = document.getElementById(`toggle-event-button-${this.project.id}`);
    this.eventsContainer = document.getElementById(`events-container-${this.project.id}`);
    this.eventContainer = document.getElementById(`events-container-${this.project.id}`);

    for (let i = 0; i < this.project.sprints.length; i++) {
      this.appendSprint(this.project.sprints[i]);
    }

    for (let j = 0; j < this.project.events.length; j++) {
      this.appendEvent(this.project.events[j]);
    }
  }

  /**
   * Toggles hiding and showing of the sprints.
   */
  toggleSprints() {
    if (this.showingSprints) {
      // Hide the sprints
      this.toggleSprintsButton.innerText = "Show Sprints";
      this.sprintsContainer.style.display = "none";
    }
    else {
      // Show the sprints
      this.toggleSprintsButton.innerText = "Hide Sprints";
      this.sprintsContainer.style.display = "block";
    }

    this.showingSprints = !this.showingSprints;
  }

  toggleEvents() {
    if (this.showingEvents) {
      // Hide the sprints
      this.toggleEventsButton.innerText = "Show Events";
      this.eventsContainer.style.display = "none";
    }
    else {
      // Show the events
      this.toggleEventsButton.innerText = "Hide Events";
      this.eventContainer.style.display = "block";
    }

    this.showingEvents = !this.showingEvents;
  }

  /**
   * Opens the add sprint form.
   */
  openAddSprintForm() {
    if (this.addSprintForm !== null) {
      return;
    }

    const formContainerElement = document.createElement("div");
    formContainerElement.classList.add("sprint-view", "raised-card");
    formContainerElement.id = `create-sprint-form-container-${this.project.id}`;
    this.sprintsContainer.append(this.sprintsContainer.firstChild, formContainerElement);

    let defaultName = 1;
    let defaultStartDate = new Date(this.project.startDate.valueOf());

    if (this.project.sprints.length !== 0) {
      defaultName = this.project.sprints[(this.project.sprints.length - 1)].orderNumber + 1;
      defaultStartDate = new Date(this.project.sprints[(this.project.sprints.length - 1)].endDate.valueOf());
    }

    const defaultEndDate = new Date(defaultStartDate.valueOf());
    defaultEndDate.setDate(defaultEndDate.getDate() + 22);

    const randomColor = Math.floor(Math.random() * 16777215).toString(16);
    const defaultColour = "#" + randomColor;

    const defaultSprint = {
      id: `__NEW_SPRINT_FORM_${this.project.id}`,
      name: `Sprint ${defaultName}`,
      description: null,
      startDate: defaultStartDate,
      endDate: defaultEndDate,
      colour: defaultColour
    };

    this.addSprintForm = {
      container: formContainerElement,
      controller: new ProjectOrSprintEditor(
          formContainerElement,
          "New sprint details:",
          defaultSprint,
          this.closeAddSprintForm.bind(this),
          this.submitAddSprintForm.bind(this),
          ProjectOrSprintEditor.makeProjectSprintDatesValidator(this.project, null)
      )
    };

    if (!this.showingSprints) {
      this.toggleSprints();
    }

  }

  /**
   * Closes the add sprint form.
   */
  closeAddSprintForm() {
    if (this.addSprintForm === null) {
      return;
    }

    this.addSprintForm.controller.dispose();
    this.sprintsContainer.removeChild(this.addSprintForm.container);
    this.addSprintForm = null;
  }

  /**
   * Submits the add sprint form, checking if this task is not being done currently (loading status).
   * @param sprint
   * @returns {Promise<void>}
   */
  async submitAddSprintForm(sprint) {
    if (this.addSprintLoadingStatus === LoadingStatus.Pending) {
      return;
    }

    this.addSprintLoadingStatus = LoadingStatus.Pending;

    try {
      const res = await fetch(`api/v1/projects/${this.project.id}/sprints`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(sprint)
      });

      if (!res.ok) {
        await ErrorHandlerUtils.handleNetworkError(res, "creating project");
      }

      const newSprint = await res.json();
      this.sprintUpdateCallback({
        ...newSprint,
        startDate: DatetimeUtils.networkStringToLocalDate(newSprint.startDate),
        endDate: DatetimeUtils.networkStringToLocalDate(newSprint.endDate),
        colour: newSprint.colour
      });
    }
    catch (ex) {
      this.addSprintLoadingStatus = LoadingStatus.Error;
      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }
      ErrorHandlerUtils.handleUnknownNetworkError(ex, "creating project");
    }
  }

  openAddEventForm() {
    if (this.addEventForm !== null) {
      return;
    }

    const formContainerElement = document.createElement("div");
    formContainerElement.classList.add("event-view", "raised-card");
    formContainerElement.id = `create-event-form-container-${this.project.id}`;
    this.eventContainer.append(this.eventsContainer.firstChild, formContainerElement)

    const defaultEvent = {
      id: `__NEW_EVENT_FORM_${this.project.id}`,
      name: null,
      description: null,
      startDate: null,
      endDate: null
    };

    this.addEventForm = {
      container: formContainerElement,
      controller: new ProjectOrSprintEditor(
          formContainerElement,
          "New event details:",
          defaultEvent,
          this.closeAddEventForm.bind(this),
          this.submitAddEventForm.bind(this),
          ProjectOrSprintEditor.makeProjectEventDatesValidator(this.project)
      )
    };

    if (!this.showingEvents) {
      this.toggleEvents();
    }
  }

  /**
   * Closes the add event form.
   */
  closeAddEventForm() {
    if (this.addEventForm === null) {
      return;
    }

    this.addEventForm.controller.dispose();
    this.eventsContainer.removeChild(this.addEventForm.container);
    this.addEventForm = null;
  }

  /**
   * Submits the add event form, checking if this task is not being done currently (loading status).
   * @param event
   * @returns {Promise<void>}
   */
  async submitAddEventForm(event) {
    if (this.addEventLoadingStatus === LoadingStatus.Pending) {
      return;
    }

    this.addEventLoadingStatus = LoadingStatus.Pending;

    try {
      const res = await fetch(`api/v1/projects/${this.project.id}/events`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(event)
      });

      if (!res.ok) {
        await ErrorHandlerUtils.handleNetworkError(res, "creating project");
      }

      const newEvent = await res.json();
      this.eventUpdateCallback({
        ...newEvent,
        startDate: DatetimeUtils.networkStringToLocalDate(newEvent.startDate),
        endDate: DatetimeUtils.networkStringToLocalDate(newEvent.endDate)
      });
    }
    catch (ex) {
      this.addEventLoadingStatus = LoadingStatus.Error;
      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }
      ErrorHandlerUtils.handleUnknownNetworkError(ex, "creating project");
    }
  }

  monthlyPlannerRedirect(projectId) {
    window.location.href = `/monthly-planner/${projectId}`
  }

  wireView() {
    document.getElementById(`project-edit-button-${this.project.id}`).addEventListener("click", () => this.editCallback());
    document.getElementById(`project-delete-button-${this.project.id}`).addEventListener("click", () => this.deleteCallback());
    document.getElementById(`monthly-planner-redirect-button-${this.project.id}`).addEventListener("click", () => this.monthlyPlannerRedirect(this.project.id));
    this.toggleSprintsButton.addEventListener('click', this.toggleSprints.bind(this));
    this.addSprintButton.addEventListener('click', this.openAddSprintForm.bind(this));
    this.toggleEventsButton.addEventListener('click', this.toggleEvents.bind(this));
    this.addEventButton.addEventListener('click', this.openAddEventForm.bind(this));
  }

  dispose() {

  }
}

/**
 * Handles editing view for both Projects and Sprints.
 */
class ProjectOrSprintEditor {
  startDateEdited = false
  endDateEdited = false

  constructor(containerElement, title, entityData, cancelCallback, submitCallback, customDatesValidator) {
    this.containerElement = containerElement;
    this.title = title;
    this.initialData = entityData;
    this.entityId = entityData.id ?? entityData.sprintId;

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
  
              <label>Name*:</label>
              <input type="text" name="project-name" id="edit-project-name-${this.entityId}"><br>
              <div id="edit-project-name-error-${this.entityId}" class="form-error" style="display: none;"></div><br>
              
              <div class="description">
                  <label>Description:</label>
                  <textarea name="description" id="edit-description-${this.entityId}" cols="50" rows="10"></textarea><br><br>
              </div>
              <label>Start Date*:</label>
              <input type="date" name="start-date" id="edit-start-date-${this.entityId}"> <span id="edit-start-date-hours-${this.entityId}"></span><br><br>
              <label>End Date*:</label>
              <input type="date" name="end-date" id="edit-end-date-${this.entityId}"> <span id="edit-end-date-hours-${this.entityId}"></span><br>
              <label id="color-label-${this.entityId}"><br>Colour*:</label>
              <input type="color" name="colour" id="edit-colour-${this.entityId}"><br></input>
              <div id="edit-project-date-error-${this.entityId}" class="form-error" style="display: none;"></div><br>
              
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

    this.colourInput = document.getElementById(`edit-colour-${this.entityId}`);
    if (!(this.title === "New sprint details:") && !(this.title === "Edit sprint details:")) {
      this.colourInput.outerHTML = "";
      document.getElementById(`color-label-${this.entityId}`).outerHTML = "";
    }
    console.log("here")
    console.log(this.containerElement);
    console.log(this.initialData)

    this.saveButton = document.getElementById(`edit-save-button-${this.entityId}`);

    this.startDateHoursField = document.getElementById(`edit-start-date-hours-${this.entityId}`);
    this.endDateHoursField = document.getElementById(`edit-end-date-hours-${this.entityId}`);

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

  /**
   * Sets the initial defaults for the inputs.
   */
  fillDefaults() {
    this.nameInput.value = this.initialData.name ?? "";
    this.descriptionInput.value = this.initialData.description ?? "";
    this.startDateInput.value = (this.initialData.startDate) ? DatetimeUtils.toLocalYMD(this.initialData.startDate) : "";
    this.colourInput.value = this.initialData.colour ?? "#000000";
    if (this.initialData.endDate) {
      const displayedDate = new Date(this.initialData.endDate.valueOf());
      displayedDate.setDate(displayedDate.getDate() - 1);
      this.endDateInput.value = DatetimeUtils.toLocalYMD(displayedDate);
    } else {
      this.endDateInput.value = "";
    }

    if (this.initialData.startDate) {
      const startDateHours = DatetimeUtils.getTimeStringIfNonZeroLocally(this.initialData.startDate);
      if (startDateHours !== null) {
        this.startDateHoursField.style.display = "inline";
        this.startDateHoursField.innerText = startDateHours;
      }
      else {
        this.startDateHoursField.style.display = "none";
      }
    }

    if (this.initialData.endDate) {
      const endDateHours = DatetimeUtils.getTimeStringIfNonZeroLocally(this.initialData.endDate);
      if (endDateHours !== null) {
        this.endDateHoursField.style.display = "inline";
        this.endDateHoursField.innerText = endDateHours;
      }
      else {
        this.endDateHoursField.style.display = "none";
      }
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

    this.setNameError(null);
    return true;
  }

  /**
   * Gets the start date from user input, otherwise defaults to initial default value.
   */
  getStartDateInputValue() {
    if (!this.startDateEdited) {
      return this.initialData.startDate ?? null;
    }const rawValue = this.startDateInput.value;
    if (rawValue) {
      return DatetimeUtils.fromLocalYMD(rawValue);
    }
    return null;
  }

  /**
   * Gets the end date from user input, otherwise defaults to initial default value.
   */
  getEndDateInputValue() {
    if (!this.endDateEdited) {
      return this.initialData.endDate ?? null;
    }const rawValue = this.endDateInput.value;
    if (rawValue) {
      const dayAfter = DatetimeUtils.fromLocalYMD(rawValue);
      dayAfter.setDate(dayAfter.getDate() + 1);
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
      if (endDate <= startDate) {
        this.setDateError("The end date must be after the start date.");
        return false;
      }
    }

    const customError = this.customDatesValidator(startDate, endDate);
    if (customError !== null) {
      this.setDateError(customError);
      return false;
    }this.setDateError(null);
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
      this.startDateHoursField.style.display = "none";  // Date is in local time now, so no hours component is necessary.
      this.validateDates();
    });
    this.endDateInput.addEventListener('change', () => {
      this.endDateEdited = true;
      this.endDateHoursField.style.display = "none";  // Date is in local time now, so no hours component is necessary.
      this.validateDates();
    });
  }

  dispose() {

  }

  /**
   * Provides a validator function for checking that the proposed dates for a sprint are allowed for the given project.
   *
   * This is a convenience method that ProjectOrSprintEditor consumers can use to pass directly in to the ProjectOrSprintEditor constructor.
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
}

/**
 * Handles the Sprint view, adding HTML in the Sprint Container.
 */
class SprintView {
  expandedView = false;

  constructor(containerElement, events, sprints, sprint, deleteCallback, editCallback) {
    this.containerElement = containerElement;
    this.sprint = sprint;
    this.editCallback = editCallback;
    this.deleteCallback = deleteCallback;
    this.events = events;
    this.sprints = sprints;

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
    </div>
    <div class="colour-block" id="sprint-colour-block-${this.sprint.sprintId}"></div>
    `;

    this.toggleButton = document.getElementById(`toggle-sprint-details-${this.sprint.sprintId}`);
    this.sprintDetails = document.getElementById(`sprint-details-${this.sprint.sprintId}`);
    this.description = document.getElementById(`sprint-description-${this.sprint.sprintId}`);
    this.colourBlock = document.getElementById(`sprint-colour-block-${this.sprint.sprintId}`);
    this.details = document.getElementById(`sprint-details-${this.sprint.sprintId}`);
    this.sprintEvents = document.getElementById(`sprint-events-${this.sprint.sprintId}`);
    document.getElementById(`sprint-order-text-${this.sprint.sprintId}`).innerText = `Sprint ${this.sprint.orderNumber}`;
    document.getElementById(`sprint-title-text-${this.sprint.sprintId}`).innerText = this.sprint.name;
    this.description.innerText = "Description: " + this.sprint.description;
    this.sprintEvents.innerHTML = this.getEvents();
    this.colourBlock.style.background = this.sprint.colour;
    document.getElementById(`start-date-${this.sprint.sprintId}`).innerText = DatetimeUtils.localToUserDMY(this.sprint.startDate);
    const displayedDate = new Date(this.sprint.endDate.valueOf());
    displayedDate.setDate(displayedDate.getDate() - 1);
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

  dispose() {

  }
}

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
    displayedDate.setDate(displayedDate.getDate() - 1);
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


/**
 * Handles switching between the editor and view screens.
 */
class Project {
  constructor(containerElement, data, deleteCallback) {
    this.containerElement = containerElement;
    this.project = data;

    this.currentView = null;
    this.showViewer();

    this.updateLoadingStatus = LoadingStatus.NotYetAttempted;

    this.deleteLoadingStatus = LoadingStatus.NotYetAttempted;
    this.deleteCallback = deleteCallback;
  }

  /**
   * Called when a sprint is updated or a new sprint is created within this project.
   *
   * Since sprints are ordered by orderNumber (derived from startDate), the dates and thus orderNumbers may have changed.
   * This method updates the orderNumbers and moves the new sprints into the correct new order.
   *
   * @param sprint to update or insert
   */
  onSprintUpdate(sprint) {
    console.log(`Project notified of update to sprint: `, sprint);

    // Delete the outdated sprint from the sprints array.
    // NB: Since this method is sometimes called with new sprints, a deletion is not guaranteed to occur here.
    for (let i=0; i < this.project.sprints.length; i++) {
      if (this.project.sprints[i].sprintId === sprint.sprintId) {
        this.project.sprints.splice(i, 1);
        break;
      }
    }

    // Insert the updated sprint.
    this.project.sprints.splice(sprint.orderNumber - 1, 0, sprint);

    // Update the orderNumbers of sprints after this one in the list.
    for (let i=sprint.orderNumber; i < this.project.sprints.length; i++) {
      this.project.sprints[i].orderNumber ++;
    }

    const showingEvents = this.currentView.showingEvents;

    // Refresh the view
    this.showViewer();

    if (showingEvents) {
      this.currentView.toggleEvents();
    }
    this.currentView.toggleSprints();
  }

  onEventUpdate(event) {
    console.log(`Project notified of update to event: `, event);

    // Delete the outdated event from the events array.
    // NB: Since this method is sometimes called with new events, a deletion is not guaranteed to occur here.
    for (let j=0; j < this.project.events.length; j++) {
      if (this.project.events[j].eventId === event.eventId) {
        this.project.events.splice(j, 1);
        break;
      }
    }

    // Insert the updated event.
    this.project.events.splice(event.orderNumber - 1, 0, event);

    // Update the orderNumbers of events after this one in the list.
    for (let j=event.orderNumber; j < this.project.events.length; j++) {
      this.project.events[j].orderNumber ++;
    }

    const showingSprints = this.currentView.showingSprints;

    // Refresh the view
    this.showViewer();
    if (showingSprints) {
      this.currentView.toggleSprints();
    }
    this.currentView.toggleEvents();
  }

  /**
   * Gets the project to explicitly destroy itself .
   */
  dispose() {
    this.currentView.dispose();
  }

  /**
   * Handles showing of project or sprint editor.
   */
  showEditor() {
    this.currentView?.dispose();
    this.currentView = new ProjectOrSprintEditor(this.containerElement,
        "Edit project details:",
        this.project,
        this.showViewer.bind(this),
        this.updateProject.bind(this),
        ProjectOrSprintEditor.makeProjectProjectDatesValidator(this.project));
  }

  /**
   * Refreshes view by disposing of current view and creating a new one.
   */
  showViewer() {
    this.currentView?.dispose();
    this.currentView = new ProjectView(this.containerElement, this.project, this.showEditor.bind(this), this.deleteProject.bind(this), this.deleteSprint.bind(this), this.onSprintUpdate.bind(this), this.deleteEvent.bind(this), this.onEventUpdate.bind(this));
  }

  /**
   * Updates project details according to newProject attributes.
   * @param newProject
   */
  async updateProject(newProject) {
    if (this.updateLoadingStatus === LoadingStatus.Pending) {
      return;
    } else if (
        newProject.name === this.project.name
        && newProject.description === this.project.description
        && DatetimeUtils.areEqual(newProject.startDate, this.project.startDate)
        && DatetimeUtils.areEqual(newProject.endDate, this.project.endDate)
    ) {
      // There is nothing to update.
      this.showViewer();
      return;
    }

    this.updateLoadingStatus = LoadingStatus.Pending;

    try {
      const result = await fetch(`api/v1/projects/${this.project.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          name: newProject.name,
          description: newProject.description,
          startDate: DatetimeUtils.localToNetworkString(newProject.startDate),
          endDate: DatetimeUtils.localToNetworkString(newProject.endDate)
        })
      })

      if (!result.ok) {
        await ErrorHandlerUtils.handleNetworkError(result, "update project");
      }

      // Saved! Show the updated view screen.
      this.updateLoadingStatus = LoadingStatus.Done;
      this.project = {
        ...newProject,
        id: this.project.id,
        sprints: this.project.sprints,
        events: this.project.events
      };
      this.showViewer();
    } catch (ex) {
      this.updateLoadingStatus = LoadingStatus.Error;

      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }

      ErrorHandlerUtils.handleUnknownNetworkError(ex, "update project");
    }
  }

  /**
   * Handles project deletion by making DELETE request.
   */
  async deleteProject() {
    if (this.deleteLoadingStatus === LoadingStatus.Pending) {
      return;
    }

    this.deleteLoadingStatus = LoadingStatus.Pending;

    try {
      const response = await fetch(`api/v1/projects/${this.project.id}`, {
        method: 'DELETE'
      })

      if (!response.ok) {
        await ErrorHandlerUtils.handleNetworkError(response, "delete project");
      }

      this.deleteLoadingStatus = LoadingStatus.Done;
      this.deleteCallback(this.project.id);
    } catch (ex) {
      this.deleteLoadingStatus = LoadingStatus.Error;

      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }

      ErrorHandlerUtils.handleUnknownNetworkError(ex, "delete project");
    }

  }

  /**
   * Handles sprint deletion of sprint with sprintId and renumbers sprints once one has been deleted.
   * @param sprintId - sprint to be deleted
   */
  deleteSprint(sprintId) {
    for (let i=0; i < this.project.sprints.length; i++) {
      if (this.project.sprints[i].sprintId === sprintId) {
        this.project.sprints.splice(i, 1);
      }

    }

    for (let i=0; i < this.project.sprints.length; i++) {
      this.project.sprints[i].orderNumber = i + 1;
    }

    const showingEvents = this.currentView.showingEvents;
    this.showViewer();
    this.currentView.toggleSprints();
    if (showingEvents) {
      this.currentView.toggleEvents();
    }
  }

  deleteEvent(eventId) {
    for (let j=0; j < this.project.events.length; j++) {
      if (this.project.events[j].eventId === eventId) {
        this.project.events.splice(j, 1);
      }

    }

    for (let i=0; i < this.project.events.length; i++) {
      this.project.events[i].orderNumber = i + 1;
    }

    const showingSprints = this.currentView.showingSprints;

    this.showViewer();
    this.currentView.toggleEvents();

    if (showingSprints) {
      this.currentView.toggleSprints();
    }

  }

}

/**
 * Handles switching between edit and view screens.
 */
class Sprint {
  constructor(containerElement, data, project, deleteCallback, sprintUpdateSavedCallback) {
    this.containerElement = containerElement;
    this.project = project;
    this.sprint = data;
    this.sprintUpdateSavedCallback = sprintUpdateSavedCallback;
    this.deleteCallback = deleteCallback;
    this.updateSprintLoadingStatus = LoadingStatus.NotYetAttempted;

    this.currentView = null;
    this.showViewer();
  }

  /**
   * Updates sprint according to newValue attributes.
   * @param newValue
   */
  async updateSprint(newValue) {
    if (this.updateSprintLoadingStatus === LoadingStatus.Pending) {
      return;
    }
    else if (
        newValue.name === this.sprint.name
        && newValue.description === this.sprint.description
        && DatetimeUtils.areEqual(newValue.startDate, this.sprint.startDate)
        && DatetimeUtils.areEqual(newValue.endDate, this.sprint.endDate)
        && newValue.colour === this.sprint.colour
    ) {
      // Nothing has changed
      const showingEvents = this.currentView.showingEvents;

      this.showViewer();

      if (showingEvents) {
        this.currentView.toggleEvents();
      }
      return;
    }

    this.updateSprintLoadingStatus = LoadingStatus.Pending;

    try {
      const response = await fetch(`api/v1/sprints/${this.sprint.sprintId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newValue)
      })

      if (!response.ok) {
        await ErrorHandlerUtils.handleNetworkError(response, "update sprint");
      }

      const newSprint = await response.json();
      this.sprintUpdateSavedCallback({
        ...newSprint,
        startDate: DatetimeUtils.networkStringToLocalDate(newSprint.startDate),
        endDate: DatetimeUtils.networkStringToLocalDate(newSprint.endDate),
        colour: newSprint.colour
      });
    }
    catch (ex) {
      this.updateSprintLoadingStatus = LoadingStatus.Error;

      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }

      ErrorHandlerUtils.handleUnknownNetworkError(ex, "update sprint");
    }
  }

  /**
   * Shows sprint editing view.
   */
  showEditor() {
    this.currentView?.dispose();
    this.currentView = new ProjectOrSprintEditor(
        this.containerElement,
        "Edit sprint details:",
        this.sprint,
        this.showViewer.bind(this),
        this.updateSprint.bind(this),
        ProjectOrSprintEditor.makeProjectSprintDatesValidator(this.project, this.sprint.sprintId)
    );
  }

  /**
   * Refreshes view, disposing of the previous view and reloading it.
   */
  showViewer() {
    this.currentView?.dispose();
    this.currentView = new SprintView(this.containerElement, this.project.events, this.project.events, this.sprint, this.deleteSprint.bind(this), this.showEditor.bind(this));
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
  async deleteSprint() {
    if (this.deleteLoadingStatus === LoadingStatus.Pending) {
      return;
    }
    console.log("here")
    this.deleteLoadingStatus = LoadingStatus.Pending;

    try {
      const response = await fetch(`api/v1/sprints/${this.sprint.sprintId}`, {
        method: 'DELETE'
      })
      if (!response.ok) {
        await ErrorHandlerUtils.handleNetworkError(response, "delete sprint");
      }

      this.deleteLoadingStatus = LoadingStatus.Done;
      this.deleteCallback(this.sprint.sprintId);
    } catch (ex) {
      this.deleteLoadingStatus = LoadingStatus.Error;

      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }

      ErrorHandlerUtils.handleUnknownNetworkError(ex, "delete sprint");
    }
  }
}


class Event {
  constructor(containerElement, data, project, deleteCallback, eventUpdateSavedCallback) {
    this.containerElement = containerElement;
    this.project = project;
    this.event = data;
    this.eventUpdateSavedCallback = eventUpdateSavedCallback;
    this.deleteCallback = deleteCallback;
    this.updateEventLoadingStatus = LoadingStatus.NotYetAttempted;

    this.currentView = null;
    this.showViewer();
  }

  /**
   * Updates event according to newValue attributes.
   * @param newValue
   */
  async updateEvent(newValue) {
    if (this.updateEventLoadingStatus === LoadingStatus.Pending) {
      return;
    }
    else if (
        newValue.name === this.event.name
        && newValue.description === this.event.description
        && DatetimeUtils.areEqual(newValue.startDate, this.event.startDate)
        && DatetimeUtils.areEqual(newValue.endDate, this.event.endDate)
    ) {
      // Nothing has changed

      const showingSprints = this.currentView.showingSprints;

      this.showViewer();

      if (showingSprints) {
        this.currentView.toggleSprints();
      }

      return;
    }

    this.updateEventLoadingStatus = LoadingStatus.Pending;

    try {
      const response = await fetch(`api/v1/events/${this.event.eventId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newValue)
      })

      if (!response.ok) {
        await ErrorHandlerUtils.handleNetworkError(response, "update event");
      }

      const newEvent = await response.json();
      this.eventUpdateSavedCallback({
        ...newEvent,
        startDate: DatetimeUtils.networkStringToLocalDate(newEvent.startDate),
        endDate: DatetimeUtils.networkStringToLocalDate(newEvent.endDate)
      });
    }
    catch (ex) {
      this.updateEventLoadingStatus = LoadingStatus.Error;

      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }

      ErrorHandlerUtils.handleUnknownNetworkError(ex, "update event");
    }
  }

  /**
   * Shows event editing view.
   */
  showEditor() {
    this.currentView?.dispose();
    this.currentView = new ProjectOrSprintEditor(
        this.containerElement,
        "Edit event details:",
        this.event,
        this.showViewer.bind(this),
        this.updateEvent().bind(this),
        ProjectOrSprintEditor.makeProjectEventDatesValidator(this.project)
    );
  }

  /**
   * Refreshes view, disposing of the previous view and reloading it.
   */
  showViewer() {
    this.currentView?.dispose();
    this.currentView = new EventView(this.containerElement, this.project.sprints, this.event, this.deleteEvent.bind(this), this.showEditor.bind(this));
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
  async deleteEvent() {
    if (this.deleteLoadingStatus === LoadingStatus.Pending) {
      return;
    }

    this.deleteLoadingStatus = LoadingStatus.Pending;

    try {
      const response = await fetch(`api/v1/events/${this.event.eventId}`, {
        method: 'DELETE'
      })
      if (!response.ok) {
        await ErrorHandlerUtils.handleNetworkError(response, "delete event");
      }

      this.deleteLoadingStatus = LoadingStatus.Done;
      this.deleteCallback(this.event.eventId);
    } catch (ex) {
      this.deleteLoadingStatus = LoadingStatus.Error;

      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }

      ErrorHandlerUtils.handleUnknownNetworkError(ex, "delete event");
    }
  }
}

/**
 * Manage the projects (creation and deletion and loading)
 */
class Application {
  addProjectButton = document.getElementById("add-project");

  addProjectForm = null;
  addProjectLoadingStatus = LoadingStatus.NotYetAttempted;

  constructor(containerElement) {
    this.projects = null;
    this.projectsLoadingState = LoadingStatus.NotYetAttempted;
    this.containerElement = containerElement;

    this.wireView();
  }

  wireView() {
    this.addProjectButton.addEventListener("click", this.openAddProjectForm.bind(this));
  }

  /**
   * Submits a new project form if the response from POST request is valid.
   * @param project
   */
  async submitAddProjectForm(project) {
    if (this.addProjectLoadingStatus === LoadingStatus.Pending) {
      return;
    }

    this.addProjectLoadingStatus = LoadingStatus.Pending;

    try {
      const res = await fetch("api/v1/projects", {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(project)
      });

      if (!res.ok) {
        await ErrorHandlerUtils.handleNetworkError(res, "create project");
      }

      const newProject = await res.json();
      this.appendProject(newProject, {
        prepend: true,
        scrollIntoView: true
      });
      this.addProjectLoadingStatus = LoadingStatus.Done;
      this.closeAddProjectForm();
    } catch (ex) {
      this.addProjectLoadingStatus = LoadingStatus.Error;

      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }

      ErrorHandlerUtils.handleUnknownNetworkError(ex, "create project");
    }
  }

  /**
   * Closes the add project form and refreshes the view.
   */
  closeAddProjectForm() {
    if (this.addProjectForm === null) {
      return;
    }

    this.addProjectForm.controller.dispose();
    this.containerElement.removeChild(this.addProjectForm.container);
    this.addProjectForm = null;
  }

  /**
   * Opens the add project form, creating a new formContainer and a default project.
   */
  openAddProjectForm() {
    if (this.addProjectForm !== null) {
      return;
    }

    const formContainerElement = document.createElement("div");
    formContainerElement.classList.add("project-view", "raised-card");
    formContainerElement.id = 'create-project-form-container';
    this.containerElement.insertBefore(formContainerElement, this.containerElement.firstChild);

    const defaultProject = {
      id: '__NEW_PROJECT_FORM',
      name: `Project ${new Date().getFullYear()}`,
      description: null,
      startDate: null,
      endDate: null
    };

    this.addProjectForm = {
      container: formContainerElement,
      controller: new ProjectOrSprintEditor(formContainerElement, "New project details:", defaultProject, this.closeAddProjectForm.bind(this), this.submitAddProjectForm.bind(this))
    };
  }

  /**
   * Clears all projects.
   */
  clearProjects() {
    if (this.projects) {
      this.projects.map(project => project.dispose());
      this.projects = null;
    }
  }

  /**
   * Append a project element to the containerElement and instantiate and store a Project with the given data.
   */
  appendProject(projectData, options) {
    const {prepend, scrollIntoView} = options ?? {};
    console.log(`${prepend ? 'Prepending' : 'Appending'} project with id: ${projectData.id}...`);

    // Post-process the projectData
    projectData.startDate = DatetimeUtils.networkStringToLocalDate(projectData.startDate);
    projectData.endDate = DatetimeUtils.networkStringToLocalDate(projectData.endDate);
    projectData.sprints = projectData.sprints.map(sprint => ({
      ...sprint,
      startDate: DatetimeUtils.networkStringToLocalDate(sprint.startDate),
      endDate: DatetimeUtils.networkStringToLocalDate(sprint.endDate)
    }));
    projectData.events = projectData.events.map(event => ({
      ...event,
      startDate: DatetimeUtils.networkStringToLocalDate(event.startDate),
      endDate: DatetimeUtils.networkStringToLocalDate(event.endDate)
    }));

    // Construct base HTML
    const projectElement = document.createElement("div");
    projectElement.classList.add("project-view", "raised-card");
    projectElement.id = `project-view-${projectData.id}`;

    if (prepend) {
      this.containerElement.insertBefore(projectElement, this.containerElement.firstChild);
    } else {
      this.containerElement.appendChild(projectElement);
    }

    console.log("Binding project");
    this.projects.set(projectData.id, new Project(projectElement, projectData, this.deleteProject.bind(this)));

    console.log("Project bound");

    if (scrollIntoView) {
      projectElement.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
      });
    }
  }

  /**
   * Fetches projects by making a GET request.
   * @returns {Promise<void>}
   */
  async fetchProjects() {
    if (this.projectsLoadingState === LoadingStatus.Pending) {
      return;
    }

    this.projectsLoadingState = LoadingStatus.Pending;
    this.clearProjects();

    try {
      const result = await fetch('api/v1/projects');

      if (!result.ok) {
        await ErrorHandlerUtils.handleNetworkError(result, "get projects");
      }

      const data = await result.json();
      console.log(`Acquired ${data.length} projects...`);
      this.projects = new Map();
      data.map(project => this.appendProject(project));
      if (this.projects.size === 1) {
        // Automatically expand the list of sprints if only one project is loaded.
        this.projects.forEach((project) => {
          if (project.currentView.toggleSprints) {
            project.currentView.toggleSprints();
          }
          if (project.currentView.toggleEvents) {
            project.currentView.toggleEvents();
          }
        })
      }
      this.projectsLoadingState = LoadingStatus.Done;
    } catch (ex) {
      this.projectsLoadingState = LoadingStatus.Error;

      if (ex instanceof PortfolioNetworkError) {
        throw ex;
      }

      ErrorHandlerUtils.handleUnknownNetworkError(ex, "get projects");
    }
  }

  /**
   * Handles project deletion according to the given project ID.
   * @param projectId - project do be deleted
   */
  deleteProject(projectId) {
    const projectElement = document.getElementById(`project-view-${projectId}`)
    this.containerElement.removeChild(projectElement);
    this.projects.get(projectId).dispose();
    this.projects.delete(projectId);
  }

}

(() => {
  // Start
  const application = new Application(document.getElementById("project-list"));
  application.fetchProjects();
})()