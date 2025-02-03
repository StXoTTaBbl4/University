const sidebar_logout = document.getElementById("sidebar-logout");
const sidebar_logout_button = document.getElementById("sidebar-logout-form:logout-button");

if (sidebar_logout != null && sidebar_logout_button != null) {
    sidebar_logout.addEventListener("click", (evt)=> {
        sidebar_logout_button.click();
    });
}

const sidebar_adreq = document.getElementById("sidebar-admin-request");
const sidebar_adreq_button = document.getElementById("sidebar-adreq-form:adreq-button");

if (sidebar_adreq != null && sidebar_adreq_button != null) {
    sidebar_adreq.addEventListener("click", (evt)=> {
        sidebar_adreq_button.click();
    });
}