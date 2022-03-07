let currentDate = '2022-03-06'
let dateIn8Months = '2022-12-06'


let projectList = [
    {
        "name": "Project 1",
        "description": "This is the first test project",
        "startDate": "2022-03-06",
        "endDate": "2022-12-06",
        "sprints": [
            {
                "name": "test sprint 1 ",
                "startDate": "2023-01-01",
                "endDate": "2023-01-15",
                "description": "test description 1"
            },
            {
                "name": "test sprint 2",
                "startDate": "2023-01-02",
                "endDate": "2023-01-16",
                "description": "test description 2"
            }
        ]
    },
    {
        "name": "Project 2",
        "description": "This is the second test project. I just want to type a lot of stuff in here so I can see if the text is going to format properly when I am making the screen smaller and just if it goes over on a large screen.",
        "startDate": "2022-03-06",
        "endDate": "2022-12-06",
        "sprints": [
            {
                "name": "test sprint 1 ",
                "startDate": "2023-01-01",
                "endDate": "2023-01-15",
                "description": "test description 1"
            }
        ]
    }
];

let defaultProject = {
    "name": "Project 2022",
    "description": "",
    "startDate": "2022-03-06",
    "endDate": "2022-12-06",
    "sprints": []
};

configureAllProjects()
addEventListeners()

function addEventListeners() {
    const toggleSprintsButtons = document.querySelectorAll('.toggle-sprints');

    for (let toggleSprintButton of toggleSprintsButtons) {
        toggleSprintButton.addEventListener('click', function() {
            viewSprints(this)
        })
    }
    const showSprintDetailsButtons = document.querySelectorAll('.toggle-sprint-details')

    for (let toggleSprintDetailButton of showSprintDetailsButtons) {
        toggleSprintDetailButton.addEventListener('click', function() {
            viewSprintDetails(this)
        })
    }
    const saveEditChangesButtons = document.querySelectorAll('.save')

    for (let saveEditChangesButton of saveEditChangesButtons) {
        saveEditChangesButton.addEventListener('click', function() {
            saveEditChanges(this)
        })
    }

    document.getElementById('add-project').addEventListener('click', function() {
        addProject()
    })
}

function configureAllProjects() {

    let documentCopy = document.cloneNode(true)
    if (projectList.length !== 0) {
        document.getElementById("project-view-0").style.display = 'none';
        projectList.forEach((project, i) => {
            addProjectToHtml(project, i, documentCopy)
        })
    } 
}

function addProjectToHtml(project, i, documentCopy) {

    let newProject = documentCopy.cloneNode(true)

    newProject.getElementById("project-view-0").id = `project-view-${i+1}`
    newProject.getElementById("project-title-text-0").id = `project-title-text-${i+1}`
    newProject.getElementById(`project-title-text-${i+1}`).innerHTML = `${project['name']} | ${project['startDate']} - ${project['endDate']}`
    newProject.getElementById("project-description-0").id = `project-description-${i+1}`
    newProject.getElementById("save-0").id = `save-${i+1}`
    newProject.getElementById("description-0").id = `description-${i+1}`
    newProject.getElementById("start-date-0").id = `start-date-${i+1}`
    newProject.getElementById("end-date-0").id = `end-date-${i+1}`
    newProject.getElementById("project-name-0").id = `project-name-${i+1}`
    newProject.getElementById("edit-project-section-0").id = `edit-project-section-${i+1}`
    newProject.getElementById("user-inputs-0").id = `user-inputs-${i+1}`
    newProject.getElementById(`project-description-${i+1}`).innerHTML = project['description']
    newProject.getElementById("toggle-sprints-0").id = `toggle-sprints-${i+1}`
    newProject.getElementById("sprints-0").id = `sprints-${i+1}`
    newProject.getElementById("add-sprint-0").id = `add-sprint-${i+1}`
    newProject.getElementById("sprint-view-0-0").id = `sprint-view-${i+1}-0`
    newProject.getElementById("sprint-title-text-0-0").id = `sprint-title-text-${i+1}-0`
    newProject.getElementById(`toggle-sprint-details-0-0`).id = `toggle-sprint-details-${i+1}-0`
    newProject.getElementById("sprint-description-0-0").id = `sprint-description-${i+1}-0`
    newProject.getElementById("start-date-0-0").id = `start-date-${i+1}-0`
    newProject.getElementById("end-date-0-0").id = `end-date-${i+1}-0`

    let documentCopyNew = newProject.cloneNode(true)
    let sprints = project['sprints']
    newProject.getElementById(`sprint-view-${i+1}-0`).style.display = 'none';

    if (sprints.length !== 0) {
        project['sprints'].forEach((sprint, j) => {
            let newSprint = documentCopyNew.cloneNode(true)
            newSprint.getElementById(`sprint-view-${i+1}-0`).id = `sprint-view-${i+1}-${j+1}`
            newSprint.getElementById(`sprint-title-text-${i+1}-0`).id = `sprint-title-text-${i+1}-${j+1}`
            newSprint.getElementById(`sprint-title-text-${i+1}-${j+1}`).innerHTML = sprint['name']
            newSprint.getElementById(`start-date-${i+1}-0`).id = `start-date-${i+1}-${j+1}`
            newSprint.getElementById(`start-date-${i+1}-${j+1}`).innerHTML = sprint['startDate']
            newSprint.getElementById(`end-date-${i+1}-0`).id = `end-date-${i+1}-${j+1}`
            newSprint.getElementById(`end-date-${i+1}-${j+1}`).innerHTML = sprint['endDate']
            newSprint.getElementById(`sprint-description-${i+1}-0`).id = `sprint-description-${i+1}-${j+1}`
            newSprint.getElementById(`sprint-description-${i+1}-${j+1}`).innerHTML = sprint['description']
            newSprint.getElementById(`toggle-sprint-details-${i+1}-0`).id = `toggle-sprint-details-${i+1}-${j+1}`

            newProject.getElementById(`sprints-${i+1}`).innerHTML += newSprint.getElementById(`sprint-view-${i+1}-${j+1}`).outerHTML

        })
    }

    document.getElementById("project-list").innerHTML += newProject.getElementById(`project-view-${i+1}`).outerHTML
}

function viewSprintDetails(button) {

    let index = button.id.slice(22)

    if (document.getElementById(`sprint-description-${index}`).style.display === "inline") {
        document.getElementById(`sprint-description-${index}`).style.display = "none";
        document.getElementById(`toggle-sprint-details-${index}`).innerHTML = "+";
    } else {
        document.getElementById(`sprint-description-${index}`).style.display = "inline";
        document.getElementById(`toggle-sprint-details-${index}`).innerHTML = "-";
    }
}

function viewSprints(button) {

    let index = button.id.charAt(button.id.length - 1)

    if (document.getElementById(`sprints-${index}`).style.display === "inline") {
        document.getElementById(`sprints-${index}`).style.display = "none";
        document.getElementById(`add-sprint-${index}`).style.display = "none";
        document.getElementById(`toggle-sprints-${index}`).innerHTML = "Show Sprints";
    } else {
        document.getElementById(`sprints-${index}`).style.display = "inline";
        document.getElementById(`add-sprint-${index}`).style.display = "inline";
        document.getElementById(`toggle-sprints-${index}`).innerHTML = "Hide Sprints";
    }
}

function saveEditChanges(button) {
    let index = button.id.charAt(button.id.length - 1)
    let title = document.getElementById(`project-title-text-${index}`).innerHTML.split('|')[0].trim()
    let projectIndex = projectList.findIndex(project => project['name'] === title)
    let project = projectList[projectIndex]

    let name = document.getElementById(`project-name-${index}`).value
    let description = document.getElementById(`description-${index}`).value
    let startDate = document.getElementById(`start-date-${index}`).value
    let endDate = document.getElementById(`end-date-${index}`).value

    project['name'] = name.length !== 0 ? name : defaultProject['name']
    project['description'] = description
    project['startDate'] = startDate.length !== 0 ? startDate : defaultProject['startDate']
    project['endDate'] = endDate.length !== 0 ? endDate : defaultProject['endDate']

    document.getElementById(`project-title-text-${index}`).innerHTML = `${project['name']} | ${project['startDate']} - ${project['endDate']}`
    document.getElementById(`project-description-${index}`).innerHTML = project['description']

    document.getElementById(`edit-project-section-${index}`).style.display = 'none'
    document.getElementById(`project-description-${index}`).style.display = 'block'
}

function addProject() {
    let index = projectList.length + 1
    projectList.push(defaultProject)
    let clone = document.cloneNode(true)
    addProjectToHtml(defaultProject, index-1, clone)
    document.getElementById(`project-view-${index}`).style.display = 'block'
    document.getElementById(`edit-project-section-${index}`).style.display = 'block'
    document.getElementById(`project-description-${index}`).style.display = 'none'
    addEventListeners()
}