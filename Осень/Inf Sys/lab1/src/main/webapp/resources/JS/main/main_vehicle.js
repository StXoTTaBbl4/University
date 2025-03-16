const vehicle_page_button = document.getElementById("vehicle-page--button");
const vehicle_form = document.getElementById("vehicle-form");
const load_vehicles = document.getElementById("vehicle-form:vehicles_button");

const next_button = document.getElementById("vehicle-form:vehicle-table_next");
const prev_button = document.getElementById("vehicle-form:vehicle-table_previous");

// let vehicle_table = document.getElementById("vehicle-form:vehicle-table");
// let vehicle_table_headers = vehicle_table.querySelectorAll("thead > tr > th");

// const vehicle_table = document.querySelector(".vehicle-table");

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

reattachHandlers("vehicle-form:vehicle-table", "vehicle-form:inputVehicleField", "vehicle-form:hiddenVehicleButton", "vehicle-form:searchVehicleInput")

// reattachVehicleHandlers()
// next_button.addEventListener("click", ()=>{
//     console.log("clicked");
//     vehicle_table.querySelectorAll("tbody > tr").forEach(function (row) {
//         row.addEventListener("click", ()=>{
//             // row.querySelector("td > a").click();
//         })
//     });
// });
//
// prev_button.addEventListener("click", ()=>{
//     console.log("clicked");
//
// });

// function reattachVehicleHandlers() {
//     let vehicle_table = document.getElementById("vehicle-form:vehicle-table");
//     let vehicle_table_headers = vehicle_table.querySelectorAll("thead > tr > th");
//
//     vehicle_table.querySelectorAll("tbody > tr").forEach(function (row) {
//         row.addEventListener("click", ()=>{
//             document.getElementById("vehicle-form:inputVehicleField").value = row.querySelector("td").textContent;
//             document.getElementById("vehicle-form:hiddenVehicleButton").click();
//         });
//     });
//
//     vehicle_table_headers.forEach((header, index) => {
//         header.addEventListener("click", () => {
//             sortTable(index, vehicle_table)});
//     });
// }

