const sidebar = document.getElementById("sidebar");
const sidebar_extend = document.getElementById("sidebar-header--button");

sidebar_extend.addEventListener("click", () => {
    sidebar.classList.toggle("sidebar__extended");
});