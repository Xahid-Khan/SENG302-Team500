'use strict';

const LoadingStatus = {
  NotYetAttempted: "NotYetAttempted",
  Pending: "Pending",
  Done: "Done",
  Error: "Error"
};

class ProjectView {
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

    document.getElementById(`project-title-text-${this.project.id}`).innerText = this.project.title;
    document.getElementById(`project-description-${this.project.id}`).innerText = this.project.description;
    document.getElementById(`project-startDate-${this.project.id}`).innerText = this.project.startDate;
    document.getElementById(`project-endDate-${this.project.id}`).innerHTML = this.project.endDate;

    // TODO: fill in the sprints here...
  }

  wireView() {
    document.getElementById(`project-edit-button-${this.project.id}`).addEventListener("click", () => this.editCallback());
    document.getElementById(`project-delete-button-${this.project.id}`).addEventListener("click", () => this.deleteCallback());
    // TODO: fill in the sprints here...
  }
}

class ProjectEditor {
  constructor(containerElement) {

  }
}

/**
 * Handles switching between the editor and view screens.
 */
class Project {
  constructor(containerElement, data) {
    this.containerElement = containerElement;
    this.currentView = new ProjectView(containerElement, data, () => console.log("editing"), ()=>console.log("deleting"));
  }

  /**
   * Gets the project to explicitly destroy itself prior
   */
  dispose() {
    
  }
}


/**
 * Manage the projects (creation and deletion and loading)
 */
class Application {
  addProjectButton = document.getElementById("add-project");

  constructor(containerElement) {
    this.projects = null;
    this.projectsLoadingState = LoadingStatus.NotYetAttempted;
    this.containerElement = containerElement;
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
  appendProject(projectData) {
    console.log(`Appending project with id: ${projectData.id}...`);
    const projectElement = document.createElement("div");
    projectElement.classList.add("project-view");
    projectElement.id = `project-view-${projectData.id}`;

    this.containerElement.appendChild(projectElement);
      
    console.log("Binding project");

    this.projects.push(new Project(projectElement, projectData));
  
    console.log("Project bound");
  }

  async fetchProjects() {
    if (this.projectsLoadingState === LoadingStatus.Pending) {
      return;
    }

    this.projectsLoadingState = LoadingStatus.Pending;
    this.clearProjects();

    //try {
      const result = await fetch('/api/v1/projects');

      if (!result.ok) {
        this.projectsLoadingState = LoadingStatus.Error;
        return;
      }

      const data = await result.json();
      console.log(`Acquired ${data.length} projects...`);
      this.projects = [];
      data.map(project => this.appendProject(project));
    /*}
    catch (ex) {
      this.projectsLoadingState = LoadingStatus.Error;
      throw ex;
    }*/
    
  }
}

(() => {
  // Start
  const application = new Application(document.getElementById("project-list"));
  application.fetchProjects();
})()

window.addProject = () => {
  fetch('/api/v1/projects', {
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
  })
}