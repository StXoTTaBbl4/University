const snackbar = document.getElementById("snackbar");
const snackbar_msg = document.getElementById("snackbar-text");

const add_button = document.querySelector(".add-entity-button");
const back_button = document.querySelector(".back-test");
const add_wrapper = document.querySelector(".add-entity-inner-wrapper");

snackbar_msg.addEventListener("change", () => {
    snackbar.classList.toggle("show");
    setTimeout(function () {
        snackbar.classList.toggle("show");
    }, 3000);
});

add_button.addEventListener("click", ()=>{
    add_wrapper.classList.toggle("add-wrapper__active");
});

back_button.addEventListener("click", ()=>{
    add_wrapper.classList.toggle("add-wrapper__active");
});



