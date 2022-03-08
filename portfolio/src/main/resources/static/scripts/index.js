'use strict';

const LoadingStatus = {
    NotYetAttempted: "NotYetAttempted",
    Pending: "Pending",
    Done: "Done",
    Error: "Error"
};

class ProjectView {
    constructor(containerElement, project, editCallback, deleteCallback) {
        console.log("project", project)
        this.containerElement = containerElement;
        this.project = project;
        this.sprintContainer = null;
        this.sprints = new Map();
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;

        this.constructAndPopulateView();
        this.wireView();
    }

    appendSprint(sprintData) {
        const sprintElement = document.createElement("div");
        sprintElement.classList.add("sprint-view");
        sprintElement.id = `sprint-view-${sprintElement.id}`;

        this.sprintContainer.appendChild(sprintElement);

        console.log("Binding sprint");

        console.log(sprintData.startDate);
        this.sprints.set(sprintData.sprintId, new Sprint(sprintElement, sprintData));

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
    <div class="sprints" id="sprints-container-${this.project.id}"></div>;`

        document.getElementById(`project-title-text-${this.project.id}`).innerText = this.project.name;
        document.getElementById(`project-description-${this.project.id}`).innerText = this.project.description;
        document.getElementById(`project-startDate-${this.project.id}`).innerText = this.project.startDate;
        document.getElementById(`project-endDate-${this.project.id}`).innerHTML = this.project.endDate;
        this.sprintContainer = document.getElementById(`sprints-container-${this.project.id}`);

        for (let i=0; i<this.project.sprints.length; i++) {
            this.appendSprint(this.project.sprints[i]);

        }
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


class SprintView {
    constructor(containerElement, sprint) {
        this.containerElement = containerElement;
        this.sprint = sprint;
        this.sprintConstruct(sprint);
    }

    sprintConstruct(sprint) {
        this.containerElement.innerHTML = `
    <div class="sprints" id="sprints-container-${sprint.sprintId}"></div>
    <div class="sprint-title">

                        <span id="sprint-title-text-${sprint.sprintId}"></span> | <span id="start-date-${sprint.sprintId}"></span> - <span id="end-date-${sprint.sprintId}"></span>

                        <span class="crud">
                            <button class="button sprint-controls">Edit</button>
                            <button class="button sprint-controls">Delete</button>
                            <button class="button toggle-sprint-details" id="toggle-sprint-details-0-0">+</button>
                        </span>
                    </div>
                    <div class="sprint-description" id="sprint-description-${sprint.sprintId}">

                    </div>
                    
                        `;

            document.getElementById(`sprint-title-text-${sprint.sprintId}`).innerHTML = sprint.name;
            document.getElementById(`sprint-description-${sprint.sprintId}`).innerHTML = sprint.description;
            document.getElementById(`start-date-${sprint.sprintId}`).innerHTML = sprint.startDate;
            document.getElementById(`end-date-${sprint.sprintId}`).innerHTML = sprint.endDate;

        }
    }



/**
 * Handles switching between the editor and view screens.
 */
class Project {
    constructor(containerElement, data, deleteCallback) {
        this.containerElement = containerElement;
        this.currentView = new ProjectView(containerElement, data, () => console.log("editing"), () => this.deleteProject());
        this.projectId = data.id;
        this.deleteLoadingStatus = LoadingStatus.NotYetAttempted;
        this.deleteCallback = deleteCallback;
    }

    /**
     * Gets the project to explicitly destroy itself prior
     */
    dispose() {

    }

    async deleteProject() {
        if (this.deleteLoadingStatus === LoadingStatus.Pending) {
            return;
        }

        this.deleteLoadingStatus = LoadingStatus.Pending;

        const result = await fetch(`/api/v1/projects/${this.projectId}`, {
            method: 'DELETE'
        })

        if (!result.ok) {
            this.deleteLoadingStatus = LoadingStatus.Error;
            return;
        }

        this.deleteCallback(this.projectId);

    }


}

class Sprint {
    constructor(containerElement, data) {
        this.containerElement = containerElement;
        this.currentView = new SprintView(containerElement, data);
        this.sprintId = data.id;
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
     * Append a project element to the containerElement and instantiate and store a Project with the given data.
     */
    appendProject(projectData) {
        console.log(`Appending project with id: ${projectData.id}...`);
        const projectElement = document.createElement("div");
        projectElement.classList.add("project-view");
        projectElement.id = `project-view-${projectData.id}`;

        this.containerElement.appendChild(projectElement);

        console.log("Binding project");

        this.projects.set(projectData.id, new Project(projectElement, projectData, this.deleteProject.bind(this)));

        console.log("Project bound");
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
            console.log(this.projects);

        } catch (ex) {
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

let items = null;

(() => {
    // Start
    console.log("here")
    const application = new Application(document.getElementById("project-list"));
    console.log(application)
    console.log(document.getElementById("project-list"));
    application.fetchProjects();
    console.log(application.projects);
    items = application.projects
})()


window.addProject = async () => {
   await fetch('/api/v1/projects', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: "Test project",
            description: "Test description",
            startDate: "2020-01-01T00:00:00.00Z",
            endDate: "2021-01-01T00:00:00.00Z",
        })
    })

    const data = await fetch('/api/v1/projects', {
        method: 'GET'
    })

    let result = await data.json()
    let projId = result[0].id;
    console.log("result", result);
    console.log(projId);


    fetch(`/api/v1/projects/${projId}/sprints`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: "Test sprint",
            description: "Test sprint desc",
            startDate: "2020-01-01T00:00:00.00Z",
            endDate: "2021-01-01T00:00:00.00Z",

        })

    })

}



