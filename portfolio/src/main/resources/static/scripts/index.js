'use strict';

const LoadingStatus = {
  NotYetAttempted: "NotYetAttempted",
  Pending: "Pending",
  Done: "Done",
  Error: "Error"
};

function leftPadNumber(number, places) {
  const numberString = `${number}`

  if (numberString.length >= places) {
    return numberString;
  }

  return ('0'.repeat(places - numberString.length)) + number;
}

class DatetimeUtils {
  static networkStringToLocalDate(utcString) {
    return new Date(Date.parse(utcString));
  }

  static localToNetworkString(localDate) {
    return localDate.toISOString();
  }

  static toLocalYMD(localDate) {
    return `${leftPadNumber(localDate.getFullYear(), 4)}-${leftPadNumber(localDate.getMonth() + 1, 2)}-${leftPadNumber(localDate.getDate(), 2)}`
  }

  static fromLocalYMD(localString) {
    // From: https://stackoverflow.com/a/64199706
    const [year, month, day] = localString.split('-');
    return new Date(year, month - 1, day);
  }

  static getTimeStringIfNonZeroLocally(date) {
    if (date.getHours() !== 0 || date.getMinutes() !== 0 || date.getSeconds() !== 0) {
      // There is an hours/minutes/seconds component to this date in the local timezone.
      return `${date.getHours()}:${leftPadNumber(date.getMinutes(), 2)}${(date.getSeconds() !== 0) ? ':' + leftPadNumber(date.getSeconds(), 2) : ''}`;
    }
    return null;
  }

  static localToUserDMY(localDate) {
    const hoursComponent = this.getTimeStringIfNonZeroLocally(localDate);
    return `${localDate.getDate()} ${localDate.toLocaleString('default', {month: 'long'})} ${localDate.getFullYear()}${(hoursComponent !== null) ? ' ' + hoursComponent : ''}`;
  }

  static areEqual(date1, date2) {
    return date1 <= date2 && date2 <= date1;
  }
}

class ProjectView {
  showingSprints = false;

  constructor(containerElement, project, editCallback, deleteCallback, sprintUpdateCallback) {
    console.log("project", project)
    this.containerElement = containerElement;
    this.project = project;
    this.sprintContainer = null;
    this.sprints = new Map();
    this.editCallback = editCallback;
    this.deleteCallback = deleteCallback;
    this.sprintUpdateCallback = sprintUpdateCallback;

    this.constructAndPopulateView();
    this.wireView();
  }

  appendSprint(sprintData) {
    const sprintElement = document.createElement("div");
    sprintElement.classList.add("sprint-view", "raised-card");
    sprintElement.id = `sprint-view-${sprintElement.id}`;

    this.sprintContainer.appendChild(sprintElement);

    console.log("Binding sprint");

    console.log(sprintData.startDate);
    this.sprints.set(sprintData.sprintId, new Sprint(sprintElement, sprintData, this.project, this.sprintUpdateCallback));

    console.log("Sprint bound");


  }

  constructAndPopulateView() {
    this.containerElement.innerHTML = `
      <div class="project-title">
          <span class="project-title-text">
            <span id="project-title-text-${this.project.id}"></span> | <span id="project-startDate-${this.project.id}"></span> - <span id="project-endDate-${this.project.id}"></span>
          </span>   
          <span class="crud">
                  <button class="button edit-project" id="project-edit-button-${this.project.id}">Edit</button>
                  <button class="button" id="project-delete-button-${this.project.id}">Delete</button>
          </span>
      </div>
      <div>
          <div class="project-description" id="project-description-${this.project.id}"></div>
          <div class="sprint-view-controls">
              <button class="button add-sprint" id="add-sprint-button-${this.project.id}"> Add Sprint</button>
              <button class="button toggle-sprints" id="toggle-sprint-button-${this.project.id}"> Show Sprints</button>
          </div>
      </div>
      <div class="sprints" id="sprints-container-${this.project.id}"></div>
    `;

    document.getElementById(`project-title-text-${this.project.id}`).innerText = this.project.name;
    document.getElementById(`project-description-${this.project.id}`).innerText = this.project.description;
    document.getElementById(`project-startDate-${this.project.id}`).innerText = DatetimeUtils.localToUserDMY(this.project.startDate);
    document.getElementById(`project-endDate-${this.project.id}`).innerText = DatetimeUtils.localToUserDMY(this.project.endDate);

    this.addSprintButton = document.getElementById(`add-sprint-button-${this.project.id}`);
    this.toggleSprintsButton = document.getElementById(`toggle-sprint-button-${this.project.id}`);
    this.sprintsContainer = document.getElementById(`sprints-container-${this.project.id}`);
    this.sprintContainer = document.getElementById(`sprints-container-${this.project.id}`);

    for (let i=0; i<this.project.sprints.length; i++) {
      this.appendSprint(this.project.sprints[i]);

    }
  }

  toggleSprints() {
    if (this.showingSprints) {
      // Hide the sprints
      this.addSprintButton.style.display = "none";
      this.toggleSprintsButton.innerText = "Show Sprints";
      this.sprintsContainer.style.display = "none";
    }
    else {
      // Show the sprints
      this.addSprintButton.style.display = "inline";
      this.toggleSprintsButton.innerText = "Hide Sprints";
      this.sprintsContainer.style.display = "block";
    }

    this.showingSprints = !this.showingSprints;
  }

  wireView() {
    document.getElementById(`project-edit-button-${this.project.id}`).addEventListener("click", () => this.editCallback());
    document.getElementById(`project-delete-button-${this.project.id}`).addEventListener("click", () => this.deleteCallback());
    this.toggleSprintsButton.addEventListener('click', this.toggleSprints.bind(this));
    // TODO: fill in the sprints here...
  }

  dispose() {

  }
}

class ProjectOrSprintEditor {
  startDateEdited = false
  endDateEdited = false

  constructor(containerElement, entityData, cancelCallback, submitCallback, customDatesValidator) {
    this.containerElement = containerElement;
    this.initialData = entityData;
    this.entityId = entityData.id ?? entityData.sprintId;

    this.cancelCallback = cancelCallback;
    this.submitCallback = submitCallback;
    this.customDatesValidator = customDatesValidator ?? function() {return null;};

    this.constructView();
    this.fillDefaults();
    this.wireView();
  }

  constructView() {
    this.containerElement.innerHTML = `
      <div class="edit-project-section" id="edit-project-section-${this.entityId}">
          <p class="edit-section-title">Edit Project Details:</p>
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
              <div id="edit-project-date-error-${this.entityId}" class="form-error" style="display: none;"></div><br>
              
              <p>* = Required field.</p>
          </form>
          <div class="save-buttons">
              <button class="button save" id="edit-save-button-${this.entityId}">Save</button>
              <button class="button cancel" id="edit-cancel-button-${this.entityId}">Cancel</button>
          </div>
      </div>
    `

    this.nameInput = document.getElementById(`edit-project-name-${this.entityId}`);
    this.descriptionInput = document.getElementById(`edit-description-${this.entityId}`);
    this.startDateInput = document.getElementById(`edit-start-date-${this.entityId}`);
    this.endDateInput = document.getElementById(`edit-end-date-${this.entityId}`);
    this.saveButton = document.getElementById(`edit-save-button-${this.entityId}`);

    this.startDateHoursField = document.getElementById(`edit-start-date-hours-${this.entityId}`);
    this.endDateHoursField = document.getElementById(`edit-end-date-hours-${this.entityId}`);

    // Error fields
    this.nameErrorEl = document.getElementById(`edit-project-name-error-${this.entityId}`);
    this.dateErrorEl = document.getElementById(`edit-project-date-error-${this.entityId}`);
  }

  setNameError(message) {
    if (message) {
      this.nameErrorEl.style.display = "block";
      this.nameErrorEl.innerText = message;
    }
    else {
      this.nameErrorEl.style.display = "none";
    }
  }

  setDateError(message) {
    if (message) {
      this.dateErrorEl.style.display = "block";
      this.dateErrorEl.innerText = message;
    }
    else {
      this.dateErrorEl.style.display = "none";
    }
  }

  fillDefaults() {
    this.nameInput.value = this.initialData.name ?? "";
    this.descriptionInput.value = this.initialData.description ?? "";
    this.startDateInput.value = (this.initialData.startDate) ? DatetimeUtils.toLocalYMD(this.initialData.startDate) : "";
    this.endDateInput.value = (this.initialData.endDate) ? DatetimeUtils.toLocalYMD(this.initialData.endDate) : "";

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

    this.setNameError(null);
    return true;
  }

  getStartDateInputValue() {
    if (!this.startDateEdited) {
      return this.initialData.startDate ?? null;
    }

    const rawValue = this.startDateInput.value;
    if (rawValue) {
      return DatetimeUtils.fromLocalYMD(rawValue);
    }
    return null;
  }

  getEndDateInputValue() {
    if (!this.endDateEdited) {
      return this.initialData.endDate ?? null;
    }

    const rawValue = this.endDateInput.value;
    if (rawValue) {
      return DatetimeUtils.fromLocalYMD(rawValue);
    }
    return null;
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
    }
    else {
      if (endDate <= startDate) {
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
          endDate: this.getEndDateInputValue()
        })
      }
      finally {
        this.saveButton.innerText = "Save";
        this.saveButton.setAttribute("disabled", "false");
      }

    }
  }

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
}


class SprintView {
  constructor(containerElement, sprint, editCallback) {
    this.containerElement = containerElement;
    this.sprint = sprint;
    this.editCallback = editCallback;

    this.constructView();
    this.wireView();
  }

  constructView() {
    this.containerElement.innerHTML = `
    <div class="sprints" id="sprints-container-${this.sprint.sprintId}"></div>
    <div class="sprint-title">
        <span id="sprint-order-text-${this.sprint.sprintId}"></span>: <span id="sprint-title-text-${this.sprint.sprintId}" style="font-style: italic;"></span> | <span id="start-date-${this.sprint.sprintId}"></span> - <span id="end-date-${this.sprint.sprintId}"></span>

        <span class="crud">
            <button class="button sprint-controls" id="sprint-button-edit-${this.sprint.sprintId}">Edit</button>
            <button class="button sprint-controls">Delete</button>
            <button class="button toggle-sprint-details" id="toggle-sprint-details-0-0">+</button>
        </span>
    </div>
    <div class="sprint-description" id="sprint-description-${this.sprint.sprintId}">

    </div>
    `;

    document.getElementById(`sprint-order-text-${this.sprint.sprintId}`).innerText = `Sprint ${this.sprint.orderNumber}`;
    document.getElementById(`sprint-title-text-${this.sprint.sprintId}`).innerText = this.sprint.name;
    document.getElementById(`sprint-description-${this.sprint.sprintId}`).innerText = this.sprint.description;
    document.getElementById(`start-date-${this.sprint.sprintId}`).innerText = DatetimeUtils.localToUserDMY(this.sprint.startDate);
    document.getElementById(`end-date-${this.sprint.sprintId}`).innerText = DatetimeUtils.localToUserDMY(this.sprint.endDate);
  }

  wireView() {
    document.getElementById(`sprint-button-edit-${this.sprint.sprintId}`).addEventListener('click', () => this.editCallback());
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

  onSprintUpdate(sprint) {
    console.log(`Project notified of update to sprint: `, sprint);

    // Delete the outdated sprint from the sprints array.
    for (let i=0; i < this.project.sprints.length; i++) {
      if (this.project.sprints[i].sprintId === sprint.sprintId) {
        this.project.sprints.splice(i, 1);
        break;
      }
    }

    // Insert the updated sprint.
    this.project.sprints.splice(sprint.orderNumber - 1, 0, sprint);

    // Update the orderNumbers of sprints after this one in the list.
    for (let i=sprint.orderNumber - 1; i < this.project.sprints.length; i++) {
      this.project.sprints[i].orderNumber ++;
    }

    // Refresh the view
    this.showViewer();
    this.currentView.toggleSprints();
  }

  /**
   * Gets the project to explicitly destroy itself.
   */
  dispose() {
    this.currentView.dispose();
  }

  showEditor() {
    this.currentView?.dispose();
    this.currentView = new ProjectOrSprintEditor(this.containerElement, this.project, this.showViewer.bind(this), this.updateProject.bind(this));
  }

  showViewer() {
    this.currentView?.dispose();
    this.currentView = new ProjectView(this.containerElement, this.project, this.showEditor.bind(this), this.deleteProject.bind(this), this.onSprintUpdate.bind(this));
  }

  async updateProject(newProject) {
    if (this.updateLoadingStatus === LoadingStatus.Pending) {
      return;
    }
    else if (
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
      const result = await fetch(`/api/v1/projects/${this.project.id}`, {
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
        this.updateLoadingStatus = LoadingStatus.Error;
        throw new Error(`Received unsuccessful response code: ${result.status} ${result.statusText}`);
      }

      // Saved! Show the updated view screen.
      this.updateLoadingStatus = LoadingStatus.Done;
      this.project = {
        ...newProject,
        id: this.project.id,
        sprints: this.project.sprints
      };
      this.showViewer();
    }
    catch (ex) {
      this.updateLoadingStatus = LoadingStatus.Error;
      throw ex;
    }
  }

  async deleteProject() {
    if (this.deleteLoadingStatus === LoadingStatus.Pending) {
      return;
    }

    this.deleteLoadingStatus = LoadingStatus.Pending;

    try {
      const result = await fetch(`/api/v1/projects/${this.project.id}`, {
        method: 'DELETE'
      })

      if (!result.ok) {
        this.deleteLoadingStatus = LoadingStatus.Error;
        throw new Error(`Got unexpected status code: ${result.status} ${result.statusText}`);
      }

      this.deleteLoadingStatus = LoadingStatus.Done;
      this.deleteCallback(this.project.id);
    }
    catch (ex) {
      this.deleteLoadingStatus = LoadingStatus.Error;
      throw ex;
    }

  }


}

class Sprint {
  constructor(containerElement, data, project, sprintUpdateSavedCallback) {
    this.containerElement = containerElement;
    this.project = project;
    this.sprint = data;
    this.sprintUpdateSavedCallback = sprintUpdateSavedCallback;

    this.updateSprintLoadingStatus = LoadingStatus.NotYetAttempted;

    this.currentView = null;
    this.showViewer();
  }

  async updateSprint(newValue) {
    if (this.updateSprintLoadingStatus === LoadingStatus.Pending) {
      return;
    }
    else if (
      newValue.name === this.sprint.name
      && newValue.description === this.sprint.description
      && DatetimeUtils.areEqual(newValue.startDate, this.sprint.startDate)
      && DatetimeUtils.areEqual(newValue.endDate, this.sprint.endDate)
    ) {
      // Nothing has changed
      this.showViewer();
      return;
    }

    this.updateSprintLoadingStatus = LoadingStatus.Pending;

    try {
      const result = await fetch(`/api/v1/sprints/${this.sprint.sprintId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newValue)
      })

      if (!result.ok) {
        throw new Error(`Received unsuccessful status code while updating sprint: ${result.status} ${result.statusText}`);
      }

      const newSprint = await result.json();
      this.sprintUpdateSavedCallback({
        ...newSprint,
        startDate: DatetimeUtils.networkStringToLocalDate(newSprint.startDate),
        endDate: DatetimeUtils.networkStringToLocalDate(newSprint.endDate)
      });
    }
    catch (ex) {
      this.updateSprintLoadingStatus = LoadingStatus.Error;
      throw ex;
    }
  }

  validateDates(startDate, endDate) {
    if (startDate < this.project.startDate || this.project.endDate < endDate) {
      return "Sprint must fit within the project dates.";
    }
    else {
      // Find overlaps...
      for (const sprint of this.project.sprints.values()) {
        if (sprint.sprintId === this.sprint.sprintId) {
          continue;
        }

        // Taken from: https://stackoverflow.com/a/325964
        if (startDate <= sprint.endDate && endDate >= sprint.startDate) {
          return `This date range overlaps with Sprint ${sprint.orderNumber}. Please choose a non-overlapping date range.`;
        }
      }
    }

    return null;
  }

  showEditor() {
    this.currentView?.dispose();
    this.currentView = new ProjectOrSprintEditor(
      this.containerElement,
      this.sprint,
      this.showViewer.bind(this),
      this.updateSprint.bind(this),
      this.validateDates.bind(this)
    );
  }

  showViewer() {
    this.currentView?.dispose();
    this.currentView = new SprintView(this.containerElement, this.sprint, this.showEditor.bind(this));
  }

  /**
   * Gets the sprint to explicitly destroy itself.
   */
  dispose() {
    this.currentView.dispose();
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

  async submitAddProjectForm(project) {
    if (this.addProjectLoadingStatus === LoadingStatus.Pending) {
      return;
    }

    this.addProjectLoadingStatus = LoadingStatus.Pending;

    try {
      const res = await fetch("/api/v1/projects", {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(project)
      });

      if (!res.ok) {
        throw new Error(`Received unsuccessful response code when creating project: ${res.status} ${res.statusText}`);
      }

      const newProject = await res.json();
      this.appendProject(newProject, {
        prepend: true,
        scrollIntoView: true
      });
      this.addProjectLoadingStatus = LoadingStatus.Done;
      this.closeAddProjectForm();
    }
    catch (ex) {
      this.addProjectLoadingStatus = LoadingStatus.Error;
      throw ex;
    }
  }

  closeAddProjectForm() {
    if (this.addProjectForm === null) {
      return;
    }

    this.addProjectForm.controller.dispose();
    this.containerElement.removeChild(this.addProjectForm.container);
    this.addProjectForm = null;
  }

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
      controller: new ProjectOrSprintEditor(formContainerElement, defaultProject, this.closeAddProjectForm.bind(this), this.submitAddProjectForm.bind(this))
    };
  }

  clearProjects() {
    if (this.projects) {
      this.projects.map(project => project.dispose());
      this.projects = null;
    }
  }

  /**
   * Append a project element to the containerElement and instaniate and store a Project with the given data.
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

    // Construct base HTML
    const projectElement = document.createElement("div");
    projectElement.classList.add("project-view", "raised-card");
    projectElement.id = `project-view-${projectData.id}`;

    if (prepend) {
      this.containerElement.insertBefore(projectElement, this.containerElement.firstChild);
    }
    else {
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

  async fetchProjects() {
    if (this.projectsLoadingState === LoadingStatus.Pending) {
      return;
    }

    this.projectsLoadingState = LoadingStatus.Pending;
    this.clearProjects();

    try {
      const result = await fetch('/api/v1/projects');

      if (!result.ok) {
        this.projectsLoadingState = LoadingStatus.Error;
        return;
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
        })
      }

      this.projectsLoadingState = LoadingStatus.Done;
    }
    catch (ex) {
      this.projectsLoadingState = LoadingStatus.Error;
      throw ex;
    }
  }

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

window.addProject = async () => {
  const res = await fetch('/api/v1/projects', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      name: "Test project",
      description: "Test description",
      startDate: "2020-01-01T00:00:00.00Z",
      endDate: "2021-01-01T00:00:00.00Z"
    })
  });

  const project = await res.json();

  for (let i=1; i <= 3; i+=2) {
    await fetch(`/api/v1/projects/${project.id}/sprints`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        name: `Test sprint ${i}`,
        description: `Test sprint description ${i}\n\nNB: This sprint covers the entire date range of its parent project.`,
        startDate: `2020-01-0${i}T00:00:00.00Z`,
        endDate: `2020-01-0${i+1}T00:00:00.00Z`
      })
    });
  }
}