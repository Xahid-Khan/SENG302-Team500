'use strict';

const form = document.getElementById("form");
const password1 = document.getElementById("password");
const password2 = document.getElementById("confirmPassword");
const error = document.getElementById("field-error");

form.addEventListener('submit', passwordMatchValidator);

/**
 * Enables the 'Confirm Password' field if a password is entered.
 */
const enableConfirmPassword = () => {
    password2.enable = password1.value.length > 0
}

/**
 * Checks whether both password fields match. Directly prevents submission if not.
 */
function passwordMatchValidator (event) {
    if(password1.value != password2.value){
        event.preventDefault();
        error.innerText = "Passwords don't match";
    }
}