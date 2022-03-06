let currentDate = '2022-03-06'
let dateIn8Months = '2022-12-06'


let project_list = [
    {
    "name": "Project 1",
    "description": "This is the first test project",
    "startDate": "2022-03-06",
    "endDate": "2022-12-06"
}, 
{
    "name": "Project 2",
    "description": "This is the second test project. I just want to type a lot of stuff in here so I can see if the text is going to format properly when I am making the screen smaller and just if it goes over on a large screen.",
    "startDate": "2022-03-06",
    "endDate": "2022-12-06"
}
];

let default_project = {
    "name": "Project 2022",
    "description": "",
    "startDate": "2022-03-06",
    "endDate": "2022-12-06"
};


function getAllProjects() {
    let copy = document.getElementById("default-project-view");
    let new_project;
    let copycat = document.cloneNode(true)
    if (project_list.length !== 0) {
        document.getElementById("default-project-view").style.display = 'none';
        project_list.forEach((project, i) => {
            new_project = copycat.cloneNode(true)
            console.log(new_project)
            new_project.getElementById("default-project-view").id = `project-view-${i}`
            new_project.getElementById("project-title-text").id = `project-title-text-${i}`
            new_project.getElementById(`project-title-text-${i}`).innerHTML = `${project['name']} | ${project['startDate']} - ${project['endDate']}`
            new_project.getElementById("project-description").id = `project-description-${i}`
            new_project.getElementById(`project-description-${i}`).innerHTML = project['description']
            document.getElementById("project-list").innerHTML += new_project.getElementById(`project-view-${i}`).outerHTML;
        })
    } 
}


function viewSprintDetails() {

    if (document.getElementsByClassName("sprint-details").style.display === "inline") {
        document.getElementByClassName("sprint-details").style.display = "none";
        document.querySelector('#toggle-sprint-details').innerHTML = "+";
    } else {
        document.getElementByClassName("sprint-details").style.display = "inline";
        document.querySelector('#toggle-sprint-details').innerHTML = "-";
    }
}

function viewSprints(number) {
    console.log(number)
    if (document.getElementByClassName("sprints").style.display === "inline") {
        document.getElementByClassName("sprints").style.display = "none";
        document.getElementByClassName("add-sprint").style.display = "none";
        document.querySelector('#toggle-view-sprints').innerHTML = "Show Sprints";
    } else {
        document.getElementByClassName("sprints").style.display = "inline";
        document.getElementByClassName("add-sprint").style.display = "inline";
        document.querySelector('#toggle-view-sprints').innerHTML = "Hide Sprints";
    }
}