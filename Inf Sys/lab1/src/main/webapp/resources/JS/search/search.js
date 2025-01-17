const vehicle_search = document.getElementById("search-vehicle-wrapper");
const coordinates_search = document.getElementById("search-coordinates-wrapper");

const vehicle_search_button = document.getElementById("vehicle-search-button");
const coordinates_search_button = document.getElementById("coordinates-search-button");

vehicle_search_button.addEventListener("click", ()=>{
    vehicle_search.classList.remove("hidden");
    coordinates_search.classList.add("hidden");
    vehicle_search_button.classList.add("active-page");
    coordinates_search_button.classList.remove("active-page");
});

coordinates_search_button.addEventListener("click", ()=>{
    vehicle_search.classList.add("hidden");
    coordinates_search.classList.remove("hidden");
    vehicle_search_button.classList.remove("active-page");
    coordinates_search_button.classList.add("active-page");
});

