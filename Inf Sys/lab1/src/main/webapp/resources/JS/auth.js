const signInBtnLink = document.querySelector('.signInBtn-link');
const signUpBtnLink = document.querySelector('.signUpBtn-link');
const wrapper = document.querySelector('.wrapper');

const reg_password = document.querySelector(".reg-pwd");
const re_reg_password = document.querySelector(".re-reg-pwd");
const signup_button = document.querySelector(".signup-button");
const reg_pwd = document.querySelector(".pwd");

const message = document.getElementById("snackbar-text");
const snackbar = document.getElementById("snackbar");

signInBtnLink.addEventListener("click", () => {
    wrapper.classList.toggle("active");
});

signUpBtnLink.addEventListener("click", () => {
    wrapper.classList.toggle("active");
});

reg_password.addEventListener("keyup", ()=>{
    validatePassword();
});

re_reg_password.addEventListener("keyup", ()=>{
    validatePassword();
});

window.addEventListener("load", () => {
    signup_button.disabled = false;

    if(message.textContent !== "") {
        snackbar.classList.toggle("show")
        setTimeout(function () {
            snackbar.classList.toggle("show");
        }, 3000);
    }
});

function validatePassword() {
    if (reg_password.value !== re_reg_password.value){
        signup_button.disabled = true;
        reg_pwd.value = reg_password.value;
    } else {
        signup_button.disabled = false;
    }
}






