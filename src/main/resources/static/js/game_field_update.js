//ОБНОВЛЕНИЕ ИГВОГО ПОЛЯ В ХОДЕ ИГРЫ

import {yourMove, myName, setPageHeader} from '/js/utils.js'

import {cellObject} from '/js/model.js'

import { myState } from "./set_and_state.js";

import { goNextStep } from '/js/controller.js'

export { initFightModel, informDialogHandler, inviteDialog, hitMe }

//Инициация игровой модели соперника

function initFightModel() {
    $('.tohit').on('click', function() {
        if ($(this).hasClass('hit')) return;
        if (!yourMove()) return;
        $(this).addClass("hit");
        sendMyNextStep($(this).attr("id"));
    });
}

function sendMyNextStep(cell){
        cellObject.fromId(cell);
        const url_to_send = "/rest/move/" + myName() + "/" + myState.playingWith;
        const data_to_sent = {x: cellObject.x, y: cellObject.y};
        const step_data = JSON.stringify(data_to_sent);
        myState.yourStep=false;
        setPageHeader('.......');
        //Отправка на сервер хода!
        sendAJAXpost (url_to_send, step_data, function (x) {
            switch (x[0]) {
                case "zero" :
                    myState.yourStep=false;
                    setPageHeader('Ходит '+myState.playingWith+'....');
                    break;
                case "injured" :
                    $('#'+cellObject.toIdPartner()).addClass('injured');
                    myState.yourStep=true;
                    setPageHeader('Ходите Вы....');
                    break;
                case "killed" :
                    $('#'+cellObject.toIdPartner()).addClass('killed');
                    showKill(cellObject.x, cellObject.y);
                    myState.yourStep=true;
                    setPageHeader('Ходите Вы....');
                    bombExplode();
                    break;
                case "victory" :
                    $('#'+cellObject.toIdPartner()).addClass('killed');
                    showKill(cellObject.x, cellObject.y);
                    myState.yourStep=false;
                    setPageHeader('Вы победили!');
                    informDialogHandler("Поздравляем Вас с победой!",
                        function () {goNextStep("restartGame");}, true);
                    break;
                case "error" :
                    myState.yourStep=false;
                    setPageHeader('Ошибка!');
                    informDialogHandler(x[1], ()=> goNextStep("restartGame"), true);
                    break;
                default :
                    myState.yourStep=true;
                    setPageHeader('Ходите Вы.... (прошлый ход не принят из-за проблем с сетью!)');
            }},
            function () {},
            function (jqXHR, textStatus, errorThrown) {
                myState.yourStep = true;
                setPageHeader('Ходите Вы.... (повторите, произошел сбой при отправке на сервер!)');
                console.log("Ошибка отправки данных на сервер по причине "+errorThrown);
                $('#'+cell).removeClass("hit");
            }
        );
}




function hitMe(x, y, result) {
    //Меня атакавали
    cellObject.x=x;
    cellObject.y=y;
    switch (result) {
        case "zero" :
            $('#'+cellObject.toId()).addClass('zerohit');
            myState.yourStep=true;
            setPageHeader('Ходите Вы....');
            break;
        case "injured" :
            $('#'+cellObject.toId()).addClass('injured');
            myState.yourStep=false;
            setPageHeader('Ходит '+myState.playingWith+'....');
            break;
        case "killed" :
            $('#'+cellObject.toId()).addClass('killed');
            showKillMe(cellObject.x, cellObject.y);
            myState.yourStep=false;
            setPageHeader('Ходит '+myState.playingWith+'....');
            bombExplode();
            break;
        case "defeated" :
            $('#'+cellObject.toId()).addClass('killed');
            myState.yourStep=false;
            setPageHeader('Вы проиграли!');
            informDialogHandler("Вы проиграли! Удачи в следующей игре...",
                function () {goNextStep("restartGame");}, true);
            break;
    }
}



function showKill(x, y) {
    //СканнВверх
    let goto_finish = true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        $('#'+cellObject.toIdPartner()).removeClass('injured');
        $('#'+cellObject.toIdPartner()).addClass('killed');
        if (++cellObject.x>10) goto_finish=false;
        let check=$('#'+cellObject.toIdPartner()).hasClass('injured');
        if (!check) goto_finish=false;
    }
    //СканнВниз
    goto_finish=true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        $('#'+cellObject.toIdPartner()).removeClass('injured');
        $('#'+cellObject.toIdPartner()).addClass('killed');
        if (--cellObject.x<1) goto_finish=false;
        let check=$('#'+cellObject.toIdPartner()).hasClass('injured');
        if (!check) goto_finish=false;
    }
    //СканВлево
    goto_finish=true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        $('#'+cellObject.toIdPartner()).removeClass('injured');
        $('#'+cellObject.toIdPartner()).addClass('killed');
        if (++cellObject.y>10) goto_finish=false;
        let check=$('#'+cellObject.toIdPartner()).hasClass('injured');
        if (!check) goto_finish=false;
    }
    //СканВправо
    goto_finish=true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        $('#'+cellObject.toIdPartner()).removeClass('injured');
        $('#'+cellObject.toIdPartner()).addClass('killed');
        if (--cellObject.y<1) goto_finish=false;
        let check=$('#'+cellObject.toIdPartner()).hasClass('injured');
        if (!check) goto_finish=false;
    }
}


function showKillMe(x, y) {
    //СканнВверх
    let goto_finish=true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        $('#'+cellObject.toId()).removeClass('injured');
        $('#'+cellObject.toId()).addClass('killed');
        if (++cellObject.x>10) goto_finish=false;
        let check=$('#'+cellObject.toId()).hasClass('injured');
        if (!check) goto_finish=false;
    }
    //СканнВниз
    goto_finish=true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        $('#'+cellObject.toId()).removeClass('injured');
        $('#'+cellObject.toId()).addClass('killed');
        if (--cellObject.x<1) goto_finish=false;
        let check=$('#'+cellObject.toId()).hasClass('injured');
        if (!check) goto_finish=false;
    }
    //СканВлево
    goto_finish=true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        $('#'+cellObject.toId()).removeClass('injured');
        $('#'+cellObject.toId()).addClass('killed');
        if (++cellObject.y>10) goto_finish=false;
        let check=$('#'+cellObject.toId()).hasClass('injured');
        if (!check) goto_finish=false;
    }
    //СканВправо
    goto_finish=true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        $('#'+cellObject.toId()).removeClass('injured');
        $('#'+cellObject.toId()).addClass('killed');
        if (--cellObject.y<1) goto_finish=false;
        let check=$('#'+cellObject.toId()).hasClass('injured');
        if (!check) goto_finish=false;
    }
}


//универсальный диалог сообщений
let informDialogHandler = function (text, callback, alter) {
    let informDialog = new bootstrap.Modal('#modalDialogInfo', {
        backdrop: 'static',
        keyboard: false
    });
    informDialog.hide();
    if (alter) $('#escape').css('display', 'none');
    $("#forContentInfo").text(text);
    $("#modalInfoButton").on('click', function () {
        $("#modalInfoButton").unbind( "click" );
        informDialog.hide();
        inviteDialog.hide();
        callback();
        $('#modalDialogInfo').modal('hide');
        $('#escape').css('display', 'inline');
    });
    informDialog.show();
}
let inviteDialog = new bootstrap.Modal('#modalDialogInvite', {
    backdrop: 'static',
    keyboard: false
});



//звуковые эффекты
function bombExplode() {
    let audio = new Audio('/js/ship_down.mp3');
    audio.play().then(r => {});
}
