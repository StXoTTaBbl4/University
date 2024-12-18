const vehicle_page_button = document.getElementById("vehicle-page--button");
const vehicle_form = document.getElementById("vehicle-form");
const load_vehicles = document.getElementById("vehicle-form:vehicles_button");

let vehicle_update_required = false

vehicle_page_button.addEventListener("click", () => {
    vehicle_page_button.classList.add("active-page");
    coordinates_page_button.classList.remove("active-page");
    if (vehicle_update_required) {
        load_vehicles.click();
        vehicle_update_required = false
    }
    vehicle_form.classList.remove("hidden");
    coordinates_form.classList.add("hidden");
});
