let socket = connectToWebsocket("ws://localhost:8080/lab1-1.0-SNAPSHOT/ws/vehicles");
const update_table = document.getElementById("requests:update");

socket.onopen = function() {
    console.log("Соединение установлено");
};

socket.onclose = function() {
    console.log("Соединение закрыто");
};

socket.onerror = function () {
    console.log("Ошибка Websocket соединения.")
}

socket.onmessage = function(event) {
    let date  = new Date();
    console.log(date + "::Data received: " + event.data);

    if (event.data === "update-admin") {
        update_table.click();
    }
};