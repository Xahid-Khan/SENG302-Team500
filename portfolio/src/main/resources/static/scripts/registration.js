form = document.getElementById("form");
password1 = document.getElementById("password");
password2 = document.getElementById("confirmPassword");
error = document.getElementById("field-error");

form.addEventListener('submit', passwordMatchValidator);

/**
 * Checks whether both password fields match. Directly prevents submission if not.
 */
function passwordMatchValidator (event) {
    if(password1.value != password2.value){
        event.preventDefault();
        error.innerText = "Passwords don't match";
    }
}