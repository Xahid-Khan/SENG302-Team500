/*
    Adapted from https://www.w3schools.com/howto/howto_js_collapsible.asp
     */
function viewSprintDetails() {

    if (document.getElementById("content").style.display === "inline") {
        document.getElementById("content").style.display = "none";
        document.querySelector('#toggle-sprint-details').innerHTML = "+";
    } else {
        document.getElementById("content").style.display = "inline";
        document.querySelector('#toggle-sprint-details').innerHTML = "-";
    }
}

function viewSprints() {
    if (document.getElementById("sprints").style.display === "inline") {
        document.getElementById("sprints").style.display = "none";
        document.getElementById("add-sprint").style.display = "none";
        document.querySelector('#toggle-view-sprints').innerHTML = "Show Sprints";
    } else {
        document.getElementById("sprints").style.display = "inline";
        document.getElementById("add-sprint").style.display = "inline";
        document.querySelector('#toggle-view-sprints').innerHTML = "Hide Sprints";
    }
}