let socket = connectToWebsocket("ws://localhost:8080/lab1-1.0-SNAPSHOT/ws/vehicles");

socket.onopen = function() {
    console.log("Соединение установлено");
};

socket.onclose = function() {
    console.log("Соединение закрыто");
    // setTimeout(function () { socket = connectToWebsocket("ws://localhost:8080/lab1-1.0-SNAPSHOT/ws/vehicles")
    // console.log(socket)}, 5000);
};

socket.onerror = function () {
    console.log("Ошибка Websocket соединения.")
}

socket.onmessage = function(event) {
    console.log(event.data)
    if (event.data === "update-vehicle") {
        if (vehicle_page_button.classList.contains("active-page")){
            load_vehicles.click();
        } else {
            vehicle_update_required = true
        }
    }
     else if (event.data === "update-coordinates") {
        if (coordinates_page_button.classList.contains("active-page")){
            load_vehicles.click();
        } else {
            coordinates_update_required= true
        }
    }
};

function connectToWebsocket(url) {
    return new WebSocket(url);
}