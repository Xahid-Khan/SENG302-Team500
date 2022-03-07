let currentDate = '2022-03-06'
let dateIn8Months = '2022-12-06'


let project_list = [
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

let default_project = {
    "name": "Project 2022",
    "description": "",
    "startDate": "2022-03-06",
    "endDate": "2022-12-06",
    "sprints": []
};

getAllProjects()

const toggleSprintsButtons = document.querySelectorAll('.toggle-sprints');
const showSprintDetailsButtons = document.querySelectorAll('.toggle-sprint-details')

for (let toggleSprintButton of toggleSprintsButtons) {
    toggleSprintButton.addEventListener('click', function() {
        viewSprints(this)
    });
}

for (let toggleSprintDetailButton of showSprintDetailsButtons) {
    toggleSprintDetailButton.addEventListener('click', function() {
        viewSprintDetails(this)
    });
}

function getAllProjects() {
    let copycat = document.cloneNode(true)
    if (project_list.length !== 0) {
        document.getElementById("project-view-0").style.display = 'none';
        project_list.forEach((project, i) => {

            let new_project = copycat.cloneNode(true)

            new_project.getElementById("project-view-0").id = `project-view-${i+1}`
            new_project.getElementById("project-title-text").id = `project-title-text-${i+1}`
            new_project.getElementById(`project-title-text-${i+1}`).innerHTML = `${project['name']} | ${project['startDate']} - ${project['endDate']}`
            new_project.getElementById("project-description").id = `project-description-${i+1}`
            new_project.getElementById(`project-description-${i+1}`).innerHTML = project['description']
            new_project.getElementById("toggle-sprints-0").id = `toggle-sprints-${i+1}`
            new_project.getElementById("sprints-0").id = `sprints-${i+1}`
            new_project.getElementById("add-sprint-0").id = `add-sprint-${i+1}`
            new_project.getElementById("sprint-view-0-0").id = `sprint-view-${i+1}-0`
            new_project.getElementById("sprint-title-text-0-0").id = `sprint-title-text-${i+1}-0`
            new_project.getElementById(`toggle-sprint-details-0-0`).id = `toggle-sprint-details-${i+1}-0`
            new_project.getElementById("sprint-description-0-0").id = `sprint-description-${i+1}-0`
            new_project.getElementById("start-date-0-0").id = `start-date-${i+1}-0`
            new_project.getElementById("end-date-0-0").id = `end-date-${i+1}-0`

            let copycat_new = new_project.cloneNode(true)
            let sprints = project['sprints']
            if (sprints.length !== 0) {
                project['sprints'].forEach((sprint, j) => {
                    let new_sprint = copycat_new.cloneNode(true)
                    new_sprint.getElementById(`sprint-view-${i+1}-0`).id = `sprint-view-${i+1}-${j+1}`
                    new_sprint.getElementById(`sprint-title-text-${i+1}-0`).id = `sprint-title-text-${i+1}-${j+1}`
                    new_sprint.getElementById(`sprint-title-text-${i+1}-${j+1}`).innerHTML = sprint['name']
                    new_sprint.getElementById(`start-date-${i+1}-0`).id = `start-date-${i+1}-${j+1}`
                    new_sprint.getElementById(`start-date-${i+1}-${j+1}`).innerHTML = sprint['startDate']
                    new_sprint.getElementById(`end-date-${i+1}-0`).id = `end-date-${i+1}-${j+1}`
                    new_sprint.getElementById(`end-date-${i+1}-${j+1}`).innerHTML = sprint['endDate']
                    new_sprint.getElementById(`sprint-description-${i+1}-0`).id = `sprint-description-${i+1}-${j+1}`
                    new_sprint.getElementById(`sprint-description-${i+1}-${j+1}`).innerHTML = sprint['description']
                    new_sprint.getElementById(`toggle-sprint-details-${i+1}-0`).id = `toggle-sprint-details-${i+1}-${j+1}`

                    new_project.getElementById(`sprints-${i+1}`).innerHTML += new_sprint.getElementById(`sprint-view-${i+1}-${j+1}`).outerHTML
                    new_project.getElementById(`sprint-view-${i+1}-0`).style.display = 'none';
                })
            }

            document.getElementById("project-list").innerHTML += new_project.getElementById(`project-view-${i+1}`).outerHTML
        })
    } 
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