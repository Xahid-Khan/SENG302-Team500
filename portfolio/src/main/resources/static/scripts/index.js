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

  static localToUserDMY(localDate) {
    return `${localDate.getDate()} ${localDate.toLocaleString('default', {month: 'long'})} ${localDate.getFullYear()}`
  }

  static areEqual(date1, date2) {
    return date1 <= date2 && date2 <= date1;
  }
}

class ProjectView {
  showingSprints = false;

  constructor(containerElement, project, editCallback, deleteCallback) {
    this.containerElement = containerElement;
    this.project = project;

    this.editCallback = editCallback;
    this.deleteCallback = deleteCallback;

    this.constructAndPopulateView();
    this.wireView();
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

    // TODO: fill in the sprints here...
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

class ProjectEditor {
  constructor(containerElement, projectData, cancelCallback, submitCallback) {
    this.containerElement = containerElement;
    this.initialProjectData = projectData;
    this.projectId = projectData.id;

    this.cancelCallback = cancelCallback;
    this.submitCallback = submitCallback;

    this.constructView();
    this.fillDefaults();
    this.wireView();
  }

  constructView() {
    this.containerElement.innerHTML = `
      <div class="edit-project-section" id="edit-project-section-${this.projectId}">
          <p class="edit-section-title">Edit Project Details:</p>
          <form class="user-inputs" id="edit-project-section-form-${this.projectId}">
  
              <label>Project Name*:</label>
              <input type="text" name="project-name" id="edit-project-name-${this.projectId}"><br>
              <div id="edit-project-name-error-${this.projectId}" class="form-error" style="display: none;"></div><br>
              
              <div class="description">
                  <label>Description:</label>
                  <textarea name="description" id="edit-description-${this.projectId}" cols="50" rows="10"></textarea><br><br>
              </div>
              <label>Start Date*:</label>
              <input type="date" name="start-date" id="edit-start-date-${this.projectId}"><br><br>
              <label>End Date*:</label>
              <input type="date" name="end-date" id="edit-end-date-${this.projectId}"><br>
              <div id="edit-project-date-error-${this.projectId}" class="form-error" style="display: none;"></div><br>
              
              <p>* = Required field.</p>
  
          </form>
          <div class="save-buttons">
              <button class="button save" id="edit-save-button-${this.projectId}">Save</button>
              <button class="button cancel" id="edit-cancel-button-${this.projectId}">Cancel</button>
          </div>
      </div>
    `

    this.nameInput = document.getElementById(`edit-project-name-${this.projectId}`);
    this.descriptionInput = document.getElementById(`edit-description-${this.projectId}`);
    this.startDateInput = document.getElementById(`edit-start-date-${this.projectId}`);
    this.endDateInput = document.getElementById(`edit-end-date-${this.projectId}`);
    this.saveButton = document.getElementById(`edit-save-button-${this.projectId}`);

    // Error fields
    this.nameErrorEl = document.getElementById(`edit-project-name-error-${this.projectId}`);
    this.dateErrorEl = document.getElementById(`edit-project-date-error-${this.projectId}`);
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
    this.nameInput.value = this.initialProjectData.name ?? "";
    this.descriptionInput.value = this.initialProjectData.description ?? "";
    this.startDateInput.value = (this.initialProjectData.startDate) ? DatetimeUtils.toLocalYMD(this.initialProjectData.startDate) : "";
    this.endDateInput.value = (this.initialProjectData.endDate) ? DatetimeUtils.toLocalYMD(this.initialProjectData.endDate) : "";
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
    const rawValue = this.startDateInput.value;
    if (rawValue) {
      return DatetimeUtils.fromLocalYMD(rawValue);
    }
    return null;
  }

  getEndDateInputValue() {
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
    document.getElementById(`edit-project-section-form-${this.projectId}`).addEventListener('submit', (evt) => {
      evt.preventDefault();
      this.validateAndSubmit();
    });
    document.getElementById(`edit-cancel-button-${this.projectId}`).addEventListener('click', () => this.cancelCallback());

    this.nameInput.addEventListener('change', this.validateName.bind(this));  // Is only called after the text field loses focus.
    this.nameInput.addEventListener('input', this.validateName.bind(this));  // Ensure that the validator is called as the user types to provide real-time feedback.
    this.startDateInput.addEventListener('change', this.validateDates.bind(this));
    this.endDateInput.addEventListener('change', this.validateDates.bind(this));
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

    this.currentView = new ProjectView(containerElement, this.project, this.showEditor.bind(this), this.deleteProject.bind(this));

    this.updateLoadingStatus = LoadingStatus.NotYetAttempted;

    this.deleteLoadingStatus = LoadingStatus.NotYetAttempted;
    this.deleteCallback = deleteCallback;
  }

  /**
   * Gets the project to explicitly destroy itself prior
   */
  dispose() {
    this.currentView.dispose();
  }

  showEditor() {
    this.currentView.dispose();
    this.currentView = new ProjectEditor(this.containerElement, this.project, this.showViewer.bind(this), this.updateProject.bind(this));
  }

  showViewer() {
    this.currentView.dispose();
    this.currentView = new ProjectView(this.containerElement, this.project, this.showEditor.bind(this), this.deleteProject.bind(this));
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
    formContainerElement.classList.add("project-view");
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
      controller: new ProjectEditor(formContainerElement, defaultProject, this.closeAddProjectForm.bind(this), this.submitAddProjectForm.bind(this))
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

    // Construct base HTML
    const projectElement = document.createElement("div");
    projectElement.classList.add("project-view");
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

  for (let i=1; i <= 5; i++) {
    await fetch(`/api/v1/projects/${project.id}/sprints`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        name: `Test sprint ${i}`,
        description: `Test sprint description ${i}\n\nNB: This sprint covers the entire date range of its parent project.`,
        startDate: `2020-01-0${i}T00:00:00.00Z`,
        endDate: `2020-01-0${i}T01:00:00.00Z`
      })
    });
  }
}