function sendToServer(){
    let xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:8080/exercise", true);
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Выполняем колбэк с полученными данными
            document.getElementById("result").innerText = xhr.responseText;
        }
    };
    let data = document.getElementById("input-field").value;
    xhr.send(JSON.stringify(data));
}