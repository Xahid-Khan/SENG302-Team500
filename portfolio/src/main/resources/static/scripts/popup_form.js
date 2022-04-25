'use strict'
//let openPopupButton = document.getElementById("userPhotoButton");
let popupForm = document.getElementById("popupForm");

//openPopupButton.addEventListener('submit', openPopupForm);

function openPopupForm() {
    popupForm.classList.add("open-popupForm");
}

function closePopupForm() {
    popupForm.classList.remove("open-popupForm");
}