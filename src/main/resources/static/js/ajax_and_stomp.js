//ВСЕ ДЛЯ СТОМПА И АЯКСА

function initSTOMP(urlToGo) {
    initSTOMP.url=urlToGo;

    let DEBUG_STOMP= false;
    if (checkSupport()) return;
    connect();


    //Обработка ошибок делает реконнект 3 раза - а потом сдаеться и выходит их игры
    function stompError(error) {
        console.log('Broker reported error: ' + error.headers['message']);
        console.log('Additional details: ' + error.body);
    }

    //Отладка стомпа
    function onDebug(m) {
        if (DEBUG_STOMP) console.log("STOMP DEBUG: "+m);
    }

    //Проверка поддержки
    function checkSupport() {
        if (window.WebSocket){
            console.log("BROWSER SUPPORTED");
        } else {
            console.log("BROWSER NOT SUPPORTED");
            alert("Загрузите игру в браузере с поддержкой современных функций (Веб Соккет в т.ч.)");
            window.location = window.location.host;
            return false;
        }
    }

    //Коннект и реконнект с настройкой опций
    function connect() {
        initSTOMP.client = new StompJs.Client({
            brokerURL: initSTOMP.url,
            debug: onDebug,
            connectHeaders: {
                login: 'sb',
                passcode: 'sb',
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,
        });
        initSTOMP.client.onConnect = initSTOMP.callback;
        initSTOMP.client.onStompError = stompError;
        initSTOMP.client.activate();
    }
}





//ВСЕ ДЛЯ АЯКСА

// Колбеки: 1 - обрадотка полученной информации, 2 - по завершении получения, 3 - обработка ошибок
function sendAJAXget (URLserver, on_success, on_complete, on_error) {
    initAJAXforSpring();
    $("body").css('cursor','wait !important; z-index: 999; height: 100%; width: 100%;');
    $.ajax({
        type: "GET",
        url: URLserver,
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        error: function(jqXHR, textStatus, errorThrown) {
            on_error(jqXHR, textStatus, errorThrown);
        },
        success: function (result) {
            on_success(result);
        },
        complete:
            function() {
                on_complete();
                $('body').css('cursor', 'default');
            }
    });
}


// Колбеки: 1 - обрадотка полученной информации, 2 - по завершении получения, 3 - обработка ошибок
function  sendAJAXpost (URLserver, toSend, on_success, on_complete, on_error) {
    initAJAXforSpring();
    $("body").css('cursor','wait !important; z-index: 999; height: 100%; width: 100%;');
    $.ajax({
        type: "POST",
        url: URLserver,
        contentType: "application/json; charset=utf-8",
        data: toSend,
        dataType: "json",
        error: function(jqXHR, textStatus, errorThrown) {
            on_error(jqXHR, textStatus, errorThrown);
        },
        success: function (result) {
            on_success(result);
        },
        complete:
            function() {
                on_complete();
                $('body').css('cursor', 'default');
            }
    });
}



//Настройка безопасности для Аякс
function initAJAXforSpring() {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
}



//Заготовочка для обработки ошибок
const ajaxErrorMessage = function (jqXHR, textStatus, errorThrown) {
    alert("Игровые данные не получены с сервера: ошибка интренета или истек таймаут Вашей сессии");
    console.log(textStatus, errorThrown)
};


