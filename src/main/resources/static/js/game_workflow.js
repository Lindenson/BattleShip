

//УПРАВЛЕНИЕ СУДАМИ

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
        const url_to_send = "/rest/doMove/" + myName() + "/" + yourPartner;
        const data_to_sent = {x: cellObject.x, y: cellObject.y};
        const step_data = JSON.stringify(data_to_sent);
        yourStep=false;
        setPageHeader('.......');
        //Отправка на сервер хода!
        sendAJAXpost (url_to_send, step_data, function (x) {
            switch (x[0]) {
                case "zero" :
                    yourStep=false;
                    setPageHeader('Ходит '+yourPartner+'....');
                    break;
                case "injured" :
                    $('#'+cellObject.toIdPartner()).addClass('injured');
                    yourStep=true;
                    setPageHeader('Ходите Вы....');
                    break;
                case "killed" :
                    $('#'+cellObject.toIdPartner()).addClass('killed');
                    showKill(cellObject.x, cellObject.y);
                    yourStep=true;
                    setPageHeader('Ходите Вы....');
                    bomb_explode();
                    break;
                case "victory" :
                    $('#'+cellObject.toIdPartner()).addClass('killed');
                    showKill(cellObject.x, cellObject.y);
                    yourStep=false;
                    setPageHeader('Вы победили!');
                    alertMy("Поздравляем Вас с победой!", function () {});
                    goNextStep("restartGame");
                    break;
                default :
                    yourStep=true;
                    setPageHeader('Ходите Вы.... (прошлый ход не принят из-за нарушения безопасности!)');
            }},
            function () {},
            function (jqXHR, textStatus, errorThrown) {
                yourStep = true;
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
            yourStep=true;
            setPageHeader('Ходите Вы....');
            break;
        case "injured" :
            $('#'+cellObject.toId()).addClass('injured');
            yourStep=false;
            setPageHeader('Ходит '+yourPartner+'....');
            break;
        case "killed" :
            $('#'+cellObject.toId()).addClass('killed');
            showKillMe(cellObject.x, cellObject.y);
            yourStep=false;
            setPageHeader('Ходит '+yourPartner+'....');
            bomb_explode();
            break;
        case "defeated" :
            $('#'+cellObject.toId()).addClass('killed');
            yourStep=false;
            setPageHeader('Вы проиграли!');
            alertMy("Вы проиграли! Удачи в следующей игре...", function () {});
            goNextStep("restartGame");
            break;
    }
}



function showKill(x, y) {
    //СканнВверх
    let goto_finish = true;
    cellObject.x=x;
    cellObject.y=y;
    while (goto_finish){
        console.log(cellObject.toIdPartner());
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
        console.log(cellObject.toIdPartner());
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
        console.log(cellObject.toIdPartner());
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
        console.log(cellObject.toIdPartner());
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
        console.log(cellObject.toId());
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
        console.log(cellObject.toId());
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
        console.log(cellObject.toId());
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
        console.log(cellObject.toId());
        $('#'+cellObject.toId()).removeClass('injured');
        $('#'+cellObject.toId()).addClass('killed');
        if (--cellObject.y<1) goto_finish=false;
        let check=$('#'+cellObject.toId()).hasClass('injured');
        if (!check) goto_finish=false;
    }
}


//универсальный диалог сообщений
let alertMy = function (text, callback, alter) {
    let alertDialog = new bootstrap.Modal('#modalDialogInfo', {
        backdrop: 'static',
        keyboard: false
    });
    alertDialog.hide()
    if (alter) $('#escape').css('display', 'none');
    $("#forContentInfo").text(text);
    $("#modalInfoButton").on('click', function () {
        $("#modalInfoButton").unbind( "click" );
        alertDialog.hide();
        inviteDialog.hide();
        callback();
        $('#modalDialogInfo').modal('hide');
        $('#escape').css('display', 'inline');
    });
    alertDialog.show();
}
let inviteDialog = new bootstrap.Modal('#modalDialogInvite', {
    backdrop: 'static',
    keyboard: false
});



//звуковые эффекты
function bomb_explode() {
    let audio = new Audio('/js/ship_down.mp3');
    audio.play().then(r => {});
}
