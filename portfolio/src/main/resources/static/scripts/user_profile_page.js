'use strict';


const activateDeletePhotoButton = () => {
    const userImage = document.getElementById("userPhoto").getAttribute("src")
    console.log(userImage)
    if (userImage === "https://humanimals.co.nz/wp-content/uploads/2019/11/blank-profile-picture-973460_640.png") {
        console.log("1111")
        document.getElementById("deleteUserPhoto").setAttribute("disabled", "true")
    }
    return null
}

document.getElementById("userPhoto").addEventListener("load", () => activateDeletePhotoButton())

const modalDeleteContainer = document.getElementById(`modal-delete-open`);
const modalDeleteX = document.getElementById(`modal-delete-x`);
const modalDeleteCancel = document.getElementById(`modal-delete-cancel`);
const modalDeleteConfirm = document.getElementById(`modal-delete-confirm`);

const openDeleteModal = () => {
    modalDeleteContainer.style.display='block';
    modalDeleteX.addEventListener("click",()=>cancelDeleteModal())
    modalDeleteCancel.addEventListener("click",()=>cancelDeleteModal())
    modalDeleteConfirm.addEventListener("click",()=>confirmDeleteModal())
}

const cancelDeleteModal = () => {
    modalDeleteContainer.style.display='none';
    modalDeleteX.removeEventListener("click",()=>cancelDeleteModal())
    modalDeleteCancel.removeEventListener("click",()=>cancelDeleteModal())
    modalDeleteConfirm.removeEventListener("click",()=>confirmDeleteModal())

}

const confirmDeleteModal = () => {
    modalDeleteContainer.style.display='none';
    modalDeleteX.removeEventListener("click",()=>cancelDeleteModal())
    modalDeleteCancel.removeEventListener("click",()=>cancelDeleteModal())
    modalDeleteConfirm.removeEventListener("click",()=>confirmDeleteModal())
    document.getElementById("deletePhotoSubmissionForm").click();
}

document.getElementById("deletePhotoForm").addEventListener('click', ()=> openDeleteModal())
