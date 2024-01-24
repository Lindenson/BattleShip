
//ВСЕ ДЛЯ СТОМПА И АЯКСА

function initSTOMP(urlToGo, urlOnExpire) {

    //Отладка стомпа
    let DEBUG_STOMP= false;

    //Проверка поддержки
    checkSupport();

    //запуск
    initSTOMP.url=urlToGo;
    connect();

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
        }
    }

    //Обработка ошибок делает реконнект 3 раза - а потом сдаеться и выходит их игры
    initSTOMP.stompError = function (error) {
        console.log('Broker reported error: ' + error.headers['message']);
        console.log('Additional details: ' + error.body);
    }

    //Коннект и реконнект с настройкой опций
    function connect() {
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");
        let headers = {}; [header] = token;
        initSTOMP.client = new StompJs.Client({
            brokerURL: initSTOMP.url,
            connectHeaders: headers,
            debug: onDebug,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });
        initSTOMP.client.onConnect = initSTOMP.callback;
        initSTOMP.client.onStompError = initSTOMP.stompError;
        initSTOMP.client.activate();
    }
}





//ВСЕ ДЛЯ АЯКСА

// Колбеки: 1 - обрадотка полученной информации, 2 - по завершении получения, 3 - обработка ошибок
function sendAJAXget (URLserver, callback1, callback2, callback3) {
    initAJAXforSpring();
    $("body").css('cursor','wait !important; z-index: 999; height: 100%; width: 100%;');
    $.ajax({
        type: "GET",
        url: URLserver,
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        error: function(jqXHR, textStatus, errorThrown) {
            callback3(jqXHR, textStatus, errorThrown);
        },
        success: function (result) {
            callback1(result);
        },
        complete:
            function() {
                callback2();
                $('body').css('cursor', 'default');
            }
    });
}


// Колбеки: 1 - обрадотка полученной информации, 2 - по завершении получения, 3 - обработка ошибок
function  sendAJAXpost (URLserver, toSend, callback1, callback2, callback3) {
    initAJAXforSpring();
    $("body").css('cursor','wait !important; z-index: 999; height: 100%; width: 100%;');
    $.ajax({
        type: "POST",
        url: URLserver,
        contentType: "application/json; charset=utf-8",
        data: toSend,
        dataType: "json",
        error: function(jqXHR, textStatus, errorThrown) {
            callback3(jqXHR, textStatus, errorThrown);
        },
        success: function (result) {
            callback1(result);
        },
        complete:
            function() {
                callback2();
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
};


