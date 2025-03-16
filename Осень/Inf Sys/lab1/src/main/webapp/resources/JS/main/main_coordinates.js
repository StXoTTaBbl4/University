const coordinates_page_button = document.getElementById("coordinates-page--button");

const coordinates_form = document.getElementById("coordinates-form");
const load_coordinates = document.getElementById("coordinates-form:coordinates_button");

let coordinates_update_required = false

coordinates_page_button.addEventListener("click", () => {
    coordinates_page_button.classList.add("active-page");
    vehicle_page_button.classList.remove("active-page");
    if (coordinates_update_required) {
        load_coordinates.click();
        coordinates_update_required = false
    }
    vehicle_form.classList.add("hidden");
    coordinates_form.classList.remove("hidden");
});

reattachHandlers("coordinates-form:coordinates-table", "coordinates-form:inputCoordinatesField", "coordinates-form:hiddenCoordinatesButton", "coordinates-form:searchCoordinatesInput");
