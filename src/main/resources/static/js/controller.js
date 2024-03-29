//ОСНОВАННАЯ НА СОБЫТИЯХ ИГРОВАЯ ЛОГИКА

import {
    strFinishesWith,
    strStartsWith,
    myName,
    setPageHeader
} from '/js/utils.js';

import {myState, invitationTimeOut} from '/js/set_and_state.js';

import {initModel, Model } from '/js/model.js';

import {forDrag, forDragNot } from '/js/dragged_preparation.js';

import {initFightModel, informDialogHandler, inviteDialog, hitMe } from '/js/game_field_update.js';

export { initWebsocket, preventMultiEntrance, sendJSONtoServer, goNextStep };


//переменные модуля
let tableInit = true;
let gamersList;
let gamersListTable;
let invitationCallback;



//Отправлям сформированную модель на сервер
function sendJSONtoServer() {
    let toSend = '{"shipLines":' + JSON.stringify(Model.ships) + '}';
    sendAJAXpost("/rest/update/" + myName(), toSend, () => console.log('well saved'),
        () => goNextStep('play'), ajaxErrorMessage)
}


//СТОМП для мамы всех страниц
function initWebsocket() {
    function router(incoming) {
        if (incoming.body.includes("playersUpdated")) {
            handleListOfUsers(incoming)
        } else if (incoming.body.includes("invite")) {
            handleInvitations(incoming)
        } else {
            handleInfoExchange(incoming)
        }
    }

    //Подписка на webSocket
    initSTOMP.callback = function (frame) {
        initSTOMP.client.subscribe("/topic/" + myName(), router, {'ack': 'client'});
        //И первая иницаилизация стартовой таблицы
        drawListGamersTable();
        $("body").css('cursor', 'default');
        $("#clockWait").remove();
    };

    $("#clockWait").css('visibility', "visible");
    $("body").css('cursor', 'wait !important; z-index: 999; height: 100%; width: 100%;');


    initSTOMP('ws://' + window.location.hostname + ':15674/ws');

    //Подписались на обмен сообщениями по добавлению и уходу игроков
    let handleListOfUsers = function (incoming) {
        incoming.ack();
        if (!myState.free) return;
        drawListGamersTable();
    }

    //Подписались на обмен сообщениями по приглашению
    let handleInvitations = function (incoming) {
        incoming.ack();
        if (!myState.free) return;
        if (strStartsWith(incoming.body, myName() + "&invitedNew")) {
            inviteShowDialog(incoming.body);
            return;
        }
        if (strStartsWith(incoming.body, myName() + "&invitedFail")) {
            let otherSide = incoming.body.split("&")[2];
            informDialogHandler('Приглашение к ' + otherSide + ' отвергнуто!', function () {
            });
            return;
        }
        if (strStartsWith(incoming.body, myName() + "&invitedDone")) {
            myState.playingWith = incoming.body.split("&")[2];
            informDialogHandler("Приглашение принято игроком " + myState.playingWith, function () {
            });
            goNextStep("prepare");
            return;
        }
        if (strFinishesWith(incoming.body, "invitedDone&" + myName())) {
            myState.playingWith = incoming.body.split("&")[0];
            goNextStep("prepare");
            return;
        }
        if (strFinishesWith(incoming.body, "invitedFail&" + myName())) {
            let otherSide = incoming.body.split("&")[0];
            if (myState.agreed === otherSide) {  //если мы соглашались только что играть
                myState.agreed = "";
                informDialogHandler('Ваше приглашение от ' + otherSide + ' более не действует!', function () {
                });
            }
        }
    };
}


//Рисование таблицы игроков (с обновлением по добавлению новых)
function drawListGamersTable() {

    let callback = function (newRowsOfData) {
        gamersList = [];
        for (let i = 0; i < newRowsOfData.length; i++)
            gamersList[i] = [newRowsOfData[i].name.toString(), newRowsOfData[i].partner.toString(),
                newRowsOfData[i].free.toString(), newRowsOfData[i].rating.toString()];
        setDataForTable();
    };

    sendAJAXget("/rest/gamers", callback, function () {
    }, function () {
    });

    function setDataForTable() {
        if (tableInit) {
            tableInit = false;

            //Используем плаги ДатаТэблс для создания адаптивной таблицы
            gamersListTable = $("#allGamersTable").DataTable({
                autoWidth: false,
                responsive: true,
                paging: false,
                scrollCollapse: true,
                scrollY: '250px',
                info: false,
                searching: true,
                ordering: true,
                language: {
                    search: "Найти:",
                    zeroRecords: "Не найдено"
                },
                columnDefs: [{
                    title: '<i class="bi-person-arms-up"></i>',
                    "render": function (data, type, row) {
                        return '<b style="color: navy">' + data + '</b>';
                    },
                    className: 'dt-body-left',
                    "width": "40%",
                    "targets": 0
                },
                    {
                        title: '<i class="bi-person-arms-up"></i>',
                        "render": function (data, type, row) {
                            if (data == "nobody") return '<b style="color: green">...</b>';
                            return '<b style="color: red">' + data + '</b>';
                        },
                        className: 'dt-body-left',
                        "width": "40%",
                        "targets": 1
                    },
                    {
                        title: '<i class="bi-pin-fill"></i>',
                        "render": function (data, type, row) {
                            if (data == "true") return '<b style="color: green">Нет</b>';
                            return '<b style="color: red">Да</b>';
                        },
                        className: 'dt-body-left',
                        "width": "10%",
                        "targets": 2
                    },
                    {
                        title: '<i class="bi-piggy-bank"></i>',
                        "render": function (data, type, row) {
                            return '<b style="color: navy">' + data + '</b>';
                        },
                        className: 'dt-body-left',
                        "width": "10%",
                        "targets": 3
                    }
                ],
            });
            //Устанавливаем обработчики нажатия ее строк (кроме заголовка)
            $('#allGamersTable tbody').on('click', 'tr', function () {
                let row = gamersListTable.row($(this)).data();
                let nameHis = row[0];
                let free = row[2];
                //Сам себе не отправляем приглашение
                if (nameHis === myName()) return;
                if (nameHis === "Имя") return;
                if (free !== 'true') return;
                //Отправляем приглашение
                informDialogHandler("Отправляем приглашение " + $(this).children(':first').text(), function () {
                    initSTOMP.client.publish({
                        destination: '/exchange/infoExchange',
                        body: "invite&" + myName() + "&" + nameHis,
                    })
                });
            });
        }
        gamersListTable.clear().rows.add(gamersList).draw();
    }
}


//Выдача диалога при поступлении сообщения о приглашении игрока

function inviteShowDialog(inviter) {
    let modDialog = $('#modalDialogInvite');
    let names = inviter.split("&");
    let newInviter = names[2];

    //Проверяем не приглашены ли уже (повторно приглашаться допустимо)
    if (myState.invited === '' || myState.invited === newInviter) {
        //ставим таймаут
        myState.invited = newInviter;
        clearTimeout(invitationCallback);
        invitationCallback = clearInviteAfterTimeout();

        modDialog.find("#modalName").text("Приглашает игрок " + names[2]);
        modDialog.find("#acceptInviteButton").on('click', function () {

            //Отправляем акцепт приглашения
            unbindAndCloseInviteDialog();
            myState.agreed = newInviter;
            sendAJAXpost("/rest/accept/" + newInviter + "/" + myName(), {},
                () => console.log('well accepted'), () => {
                }, ajaxErrorMessage);
        });
        modDialog.find("#rejectInviteButton").on('click', function () {

            //Отправляем режект приглашения
            unbindAndCloseInviteDialog();
            sendAJAXpost("/rest/reject/" + newInviter + "/" + myName(), {},
                () => console.log('well rejected'), () => {
                });
        });
        inviteDialog.show();

    } else {
        //Если уже кем то приглашены - отправляем режект приглашения (можно усложнить алгоритм... потом)
        sendAJAXpost("/rest/reject/" + newInviter + "/" + myName(), {},
            () => console.log('well rejected'), () => {
            }, ajaxErrorMessage);
    }
}

function unbindAndCloseInviteDialog() {
    myState.invited = '';
    clearTimeout(invitationCallback);
    let modDialog = $('#modalDialogInvite')
    modDialog.find("#acceptInviteButton").unbind('click');
    modDialog.find("#rejectInviteButton").unbind('click');
    inviteDialog.hide();
}


// СОБЫТИЯ

//Переход по вебФлоу по событию
function goNextStep(step) {

    let restarted = $('#restartGame');
    let fieldPreparing = $('#prepare');
    let gamerTable = $('#allGamersTable');
    let newURL = $('#forFlowPlacer').text() + '&_eventId=';

    switch (step) {
        case "restartGame":
            newURL = newURL + step;
            window.location = newURL;
            break;

        case "prepare":
            myState.free = false;
            //Перерисовываем поле игры
            gamerTable.empty();
            gamerTable.remove();
            restarted.empty();
            restarted.remove();
            initModel();
            forDrag();
            fieldPreparing.css('display', 'inline');
            setPageHeader('Противники готовятся....');
            break;

        case "play":
            //Перерисовываем поле игры
            fieldPreparing.empty();
            fieldPreparing.remove();
            forDragNot();
            Model.updateViev();
            initFightModel();
            $('#play').css('display', 'inline');
            if (myState.partnerReady) setPageHeader('Ходит ' + myState.playingWith + '....');
            else {
                setPageHeader(myState.playingWith + ' расставляет! Ждем....');
                myState.yourStep = true;
            }
            break;

        case "toExit":
            newURL = newURL + step;
            window.location = newURL;
            break;
    }

}


//Обработка входящих сообщений о ходе игры
let handleInfoExchange = function (incoming) {
    incoming.ack();
    let context = incoming.body.split("&");
    switch (context[0]) {
        case "error":
            informDialogHandler("Ошибка на сервере! Перегрузитесь", function () {
                window.location = '/';
            }, true);
            break;
        case "logout":
            window.location = '/';
            break;
        case "escaped":
            informDialogHandler(context[1], function () {
                goNextStep("restartGame");
            }, true);
            break;
        case "setUp":
            if (myState.yourStep) setPageHeader('Ходите Вы....');
            else setPageHeader(myState.playingWith + ' расставил фигуры и ожидает...');
            myState.partnerReady = true;
            break;
        case "hitYou":
            hitMe(context[1], context[2], context[3]);
            break;
    }
};

function preventMultiEntrance() {
    const alreadyPlaying = $('#forAlreadyPlaying').text();
    if (alreadyPlaying === 'true') {
        informDialogHandler("Вы повторно вошли в игру в том же браузере, из-за этого прошлая " +
            "игра (в другом окне) будет прекращена?", () => goNextStep('toExit'))
    }

}

function clearInviteAfterTimeout() {
    return setTimeout(() => {
        if (myState.free && myState.invited !== '') {
            sendAJAXpost("/rest/reject/" + myState.invited + "/" + myName(), {},
                () => console.log('well rejected'), () => {
                }, ajaxErrorMessage);
            unbindAndCloseInviteDialog();
        }
    }, invitationTimeOut);
}
