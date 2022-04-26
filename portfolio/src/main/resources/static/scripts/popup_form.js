'use strict'
//let openPopupButton = document.getElementById("userPhotoButton");
let popupForm = document.getElementById("popupForm");
let newImageInput = document.getElementById("newUploadedImage");

//openPopupButton.addEventListener('submit', openPopupForm);

function openPopupForm() {
    popupForm.classList.add("open-popupForm");
}

function closePopupForm() {
    popupForm.classList.remove("open-popupForm");
}

function cancelPopupForm() {
    newImageInput.value = null;
    popupForm.classList.remove("open-popupForm");
}