const vehicle_page_button = document.getElementById("vehicle-page--button");
const coordinates_page_button = document.getElementById("coordinates-page--button");

vehicle_page_button.addEventListener("click", () => {
    vehicle_page_button.classList.add("active-page");
    coordinates_page_button.classList.remove("active-page");
});

coordinates_page_button.addEventListener("click", () => {
    coordinates_page_button.classList.add("active-page");
    vehicle_page_button.classList.remove("active-page");
});

function loadUsers() {
    jsf.ajax.request(
        document.getElementById('userForm'),
        null,
        {execute: '@this', render: 'userForm'}
    );
}