const add_button = document.querySelector(".add-entity-button");
const add_wrapper = document.querySelector(".add-entity-inner-wrapper");

const add_vehicle = document.querySelector(".add-entity-slider--vehicle");
const add_coordinates = document.querySelector(".add-entity-slider--coordinates");

let add_vehicle_form = document.getElementById("add-new-vehicle-form");
let add_coordinates_form = document.getElementById("add-new-coordinates-form");

const coordinates_table = document.getElementById("coordinates-form:coordinates-table");
const coordinates_table_headers = coordinates_table.querySelectorAll("thead > tr > th");

    add_coordinates_form.classList.add("hidden");

    add_button.addEventListener("click", ()=>{
        add_wrapper.classList.toggle("add-wrapper__active");
    });

    add_vehicle.addEventListener("click", ()=>{
        add_coordinates_form = document.getElementById("add-new-coordinates-form");
        add_vehicle_form = document.getElementById("add-new-vehicle-form");
        add_vehicle_form.classList.remove("hidden");
        add_coordinates_form.classList.add("hidden");
        add_vehicle.classList.add("select-entity-button--active");
        add_coordinates.classList.remove("select-entity-button--active");

    });

    add_coordinates.addEventListener("click", ()=>{
        add_vehicle_form = document.getElementById("add-new-vehicle-form");
        add_coordinates_form = document.getElementById("add-new-coordinates-form");
        add_vehicle_form.classList.add("hidden");
        add_coordinates_form.classList.remove("hidden");
        add_vehicle.classList.remove("select-entity-button--active");
        add_coordinates.classList.add("select-entity-button--active");
    });

    // coordinates_table_headers.forEach((header, index) => {
    //     header.addEventListener("click", () => sortTable(index, coordinates_table));
    // });

function sortTable(columnIndex, table) {
    const rows = Array.from(table.rows).slice(1);
    let ascending = table.dataset.sortOrder !== "asc";

    rows.sort((rowA, rowB) => {
        const cellA = rowA.cells[columnIndex].textContent.trim();
        const cellB = rowB.cells[columnIndex].textContent.trim();

        const a = isNaN(cellA) ? cellA : parseFloat(cellA);
        const b = isNaN(cellB) ? cellB : parseFloat(cellB);

        if (a < b) return ascending ? -1 : 1;
        if (a > b) return ascending ? 1 : -1;
        return 0;
    });

    // Перестраиваем таблицу
    const tbody = table.tBodies[0];
    rows.forEach((row) => tbody.appendChild(row));

    // Сохраняем направление сортировки
    table.dataset.sortOrder = ascending ? "asc" : "desc";
}

function parseFilters(input) {
    const filters = {};
    input.split(',').forEach(filter => {
        const [key, value] = filter.split(':').map(item => item.trim());
        if (key && value) {
            filters[key] = value;
        }
    });
    return filters;
}

function filterTable(filters, rows, headers) {
    rows.forEach(row => {
        const cells = Array.from(row.querySelectorAll('td'));
        const matches = Object.entries(filters).every(([header, value]) => {
            const index = headers.indexOf(header);
            if (index === -1) return false;
            return cells[index]?.textContent.trim() === value;
        });
        row.classList.toggle('hidden', !matches);
    });
}

function reattachHandlers(table_id, inputField_id, button_id, search_input) {
    let table = document.getElementById(table_id);
    let table_headers = table.querySelectorAll("thead > tr > th");

    table.querySelectorAll("tbody > tr").forEach(function (row) {
        // console.log(row.querySelector("td").textContent);
        row.addEventListener("click", () => {
            document.getElementById(inputField_id).value = row.querySelector("td").textContent;
            document.getElementById(button_id).click();
        });
    });

    table_headers.forEach((header, index) => {
        header.addEventListener("click", () => {
            sortTable(index, table)
        });
    });

    // Поиск
    let input = document.getElementById(search_input);
    let headers = Array.from(table_headers).map(th => th.textContent.trim());
    let rows = Array.from(table.querySelectorAll('tbody tr'));

    input.addEventListener('input', () => {
        const filters = parseFilters(input.value);
        filterTable(filters, rows, headers);
    });


}





