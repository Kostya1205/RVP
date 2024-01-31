sendGetToServer();
function sendGetToServer(){
    let xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:8080/table/info", true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            displayTable(JSON.parse(xhr.responseText));
        }
    };
    xhr.send();
}
function sendPostToServer(url,data){
    let xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:8080"+url, true);
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            document.getElementById('name').value='';
            document.getElementById('price').value='';
            document.getElementById('volume').value ='';
            displayTable(JSON.parse(xhr.responseText));
        }
    };
    xhr.send(JSON.stringify(data));
}
function openModal() {
    // Отобразить модальное окно
    document.getElementById('modal').style.display = 'block';
    document.getElementById('button-modal-save').style.display = 'block';
    document.getElementById('button-modal-save').onclick = saveData;
}

function saveData() {
    // Создание объекта с данными
    let data = { name:document.getElementById('name').value,
        price: document.getElementById('price').value,
        volume:document.getElementById('volume').value };

    // Отправка данных на сервер
    sendPostToServer("/table/add", data);

    // Скрыть модальное окно после сохранения
    document.getElementById('button-modal-save').style.display = 'none';

    document.getElementById('modal').style.display = 'none';
}
function displayTable(table) {
    let tableElement = document.getElementById('table-id');

    // Очистим текущую таблицу перед добавлением новых данных
    clearTable(tableElement);

    // Перебираем каждую запись в списке и добавляем ее в таблицу
    table.forEach(item => {
        let newRow = tableElement.insertRow(-1);
        let cell1 = newRow.insertCell(0);
        let cell2 = newRow.insertCell(1);
        let cell3 = newRow.insertCell(2);
        let cell4 = newRow.insertCell(3);
        let cell5 = newRow.insertCell(4);

        cell1.innerHTML = item.name;
        cell2.innerHTML = item.price;
        cell3.innerHTML = item.volume;
        let buttonDelete = document.createElement('button');
        buttonDelete.textContent = 'Удалить';
        buttonDelete.addEventListener('click', function() {
            deleteItem(item);
        });
        cell4.appendChild(buttonDelete);

        let buttonChange = document.createElement('button');
        buttonChange.textContent = 'Изменить';
        buttonChange.addEventListener('click', function() {
            // Отобразить модальное окно
            document.getElementById('modal').style.display = 'block';
            document.getElementById('button-modal-edit').style.display = 'block';
            document.getElementById('button-modal-edit').onclick = function() {
                editItem(item);
            };

            document.getElementById('name').value=item.name;
            document.getElementById('price').value=item.price;
            document.getElementById('volume').value = item.volume;

        });
        cell5.appendChild(buttonChange);
    });
}

function deleteItem(item) {
    sendPostToServer("/table/delete",item);
}

function editItem(item) {

    let data = {
        id:item.id,
        name:document.getElementById('name').value,
        price: document.getElementById('price').value,
        volume:document.getElementById('volume').value };
    // Отправка данных на сервер
    sendPostToServer("/table/change", data);

    document.getElementById('button-modal-edit').style.display = 'none';
    document.getElementById('modal').style.display = 'none';
}
// Функция для очистки таблицы
function clearTable(tableElement) {
    while (tableElement.rows.length > 1) {
        tableElement.deleteRow(1);
    }
}
