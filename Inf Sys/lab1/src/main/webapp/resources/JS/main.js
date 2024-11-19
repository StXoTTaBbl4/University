let socket = connectToWebsocket("ws://localhost:8080/lab1-1.0-SNAPSHOT/ws/vehicles");

const vehicle_page_button = document.getElementById("vehicle-page--button");
const coordinates_page_button = document.getElementById("coordinates-page--button");

const snackbar = document.getElementById("snackbar");
const snackbar_msg = document.getElementById("snackbar-text")

const load_vehicles = document.getElementById("vehicle-form:vehicles_button");

let vehicle_update_required = false

vehicle_page_button.addEventListener("click", () => {
    vehicle_page_button.classList.add("active-page");
    coordinates_page_button.classList.remove("active-page");
    if (vehicle_update_required) {
        load_vehicles.click();
        vehicle_update_required = false
    }
});

coordinates_page_button.addEventListener("click", () => {
    coordinates_page_button.classList.add("active-page");
    vehicle_page_button.classList.remove("active-page");
});

snackbar_msg.addEventListener("change", () => {
    snackbar.classList.toggle("show");
    setTimeout(function () {
        snackbar.classList.toggle("show");
    }, 3000);
});

socket.onopen = function() {
    console.log("Соединение установлено");
};

socket.onclose = function() {
    console.log("Соединение закрыто, переподключение через 5 секунд...");
    setTimeout(function () { socket = connectToWebsocket()}, 5000);
};

socket.onmessage = function(event) {
    console.log(event.data)
    console.log(event.data === "update-vehicle")
    if (event.data === "update-vehicle") {
        if (vehicle_page_button.classList.contains("active-page")){
            load_vehicles.click();
        } else {
            vehicle_update_required = true
        }
    }
};

function connectToWebsocket(url) {
    return new WebSocket(url);
}

