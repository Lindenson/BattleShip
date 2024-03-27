//ПОДГОТОВКА СУДОВ ПЕРЕТАСКИВАНИЕМ

import { sizes } from '/js/set_and_state.js'

import { getMainTableOffset, getMainTableHeight, getMainTableWidth, is_safari } from '/js/utils.js'

import { cellObject, Model, positioN } from '/js/model.js'

export { forDrag, forDragNot }

let savedPos;
let goFuther = 1;
let ifDragged = false;

//Обработчик драг энд дроп для расстановки собственных судов
function forDrag() {
    let finalOffset;
    let finalxPos;
    let finalyPos;
    let flowxPos;
    let flowyPos;
    let initOffset;

    $(".draggable").draggable({
        start: function (event, ui) {
            initOffset = $(this).offset();
            flowxPos = initOffset.left;
            flowyPos = initOffset.top;
        },
        stop: function (event, ui) {
            finalOffset = $(this).offset();
            finalxPos = finalOffset.left;
            finalyPos = finalOffset.top;
            //Проверяем попали ли в поле игры, перемещая кораблик
            if (checkNotInsideMainTable(finalOffset, $(this).width(), $(this).height())) {
                //Найден ли по положению элемент куда будем помещать кораблик
                let cell = getElsAt(finalyPos, finalxPos);
                if (cell.length !== 0) {
                    cellObject.fromId(cell.attr("id"));
                    //Устанавливаем размер корабля
                    let sl = 4;
                    if ($(this).hasClass("ship1")) sl = 1;
                    if ($(this).hasClass("ship2")) sl = 2;
                    if ($(this).hasClass("ship3")) sl = 3;
                    //Проверяем не укладываем ли на уже размещенный кoрабль
                    let resulT = Model.validateShip(cellObject.x, cellObject.y, positioN.HOR, sl, 0);
                    if (resulT) {
                        Model.poseShip(cellObject.x, cellObject.y, positioN.HOR, sl, 0);
                        //Удаляем верхний спейсер для нормального смещения корабликов вверх
                        if (($(this).attr("id") !== "ship11") && ($(this).attr("id") !== "ship13")) {
                            $(this).prev().remove();
                        } else {
                            $(this).next().remove();
                        }
                        //Удаляем установленный кoраблик из панели выбора и обновляем вьюв
                        $(this).remove();
                        Model.updateViev();
                        //Глюк в сафари
                        if (is_safari) $('tr').hide().show(0);
                        insertDiv(finalxPos, finalyPos, sl, positioN.HOR);
                        //Если разместили все корабли то отображаем кнопку "Вперед"
                        goFuther++;
                        if (goFuther > 10) $('#gofuther').css("visibility", "visible")
                    } else $(this).offset(initOffset);
                }
            } else {
                $(this).offset(initOffset);
            }
        }
    });
}


//Обработчик драг энд дроп для переноса расставленных судов
function forDragDiv() {
    let finalOffset;
    let finalxPos;
    let finalyPos;
    let flowxPos;
    let flowyPos;
    let initOffset;
    let cellOld;
    let cellNew;
    let lenGH;
    let oldX;
    let oldY;
    $(".draggable1").draggable({
        start: function (event, ui) {
            ifDragged = true;
            initOffset = $(this).offset();
            flowxPos = initOffset.left;
            flowyPos = initOffset.top;
            $(this).css("border", "solid");
            $(this).css("border-color", "red");
            cellOld = getElsAt(flowyPos, flowxPos);
            if (cellOld.length === 0) return;
            cellObject.fromId(cellOld.attr("id"));
            lenGH = Model.getWidth(cellObject.x, cellObject.y);
            oldX = cellObject.x;
            oldY = cellObject.y;
            savedPos = Model.getPos(oldX, oldY);
        },
        stop: function (event, ui) {
            finalOffset = $(this).offset();
            finalxPos = finalOffset.left;
            finalyPos = finalOffset.top;
            cellNew = getElsAt(finalyPos, finalxPos);
            //Найден ли по сетоположению элемент куда будем помещать кораблик
            if ((cellOld.length == 0) || (cellNew.length == 0)) {
                $(this).offset(initOffset);
                $(this).remove();
                insertDiv(flowxPos, flowyPos, lenGH, savedPos);
                return;
            }
            //Проверяем попали ли в поле игры, перемещая кораблик
            if (checkNotInsideMainTable(finalOffset, $(this).width(), $(this).height())) {
                $(this).css("border", "none");
                cellObject.fromId(cellNew.attr("id"));
                let savedId = Model.getId(oldX, oldY);
                //Проверяем не укладываем ли на уже размещенный карабль
                let resulT = Model.validateShip(cellObject.x, cellObject.y, savedPos, lenGH, savedId);
                if (resulT) {
                    Model.deleteShip(oldX, oldY, lenGH);
                    Model.poseShip(cellObject.x, cellObject.y, savedPos, lenGH, savedId);
                    Model.updateViev();
                    $(this).remove();
                    insertDiv(cellNew.offset().left, cellNew.offset().top, lenGH, savedPos);
                } else {
                    $(this).remove();
                    insertDiv(flowxPos, flowyPos, lenGH, savedPos);
                }
            } else {
                $(this).remove();
                insertDiv(flowxPos, flowyPos, lenGH, savedPos);
            }
        }
    });
}


function forDragNot() {
    $(".draggable").draggable({disabled: true});
    $(".draggable1").draggable({disabled: true});
    $(".frameFor").remove();
}


//Найдем попали ли в пределы таблицы-поля
function checkNotInsideMainTable(offset, w, h) {
    let bigOffset = getMainTableOffset();
    if (offset.top < bigOffset.top) return false;
    if (offset.left < bigOffset.left) return false;
    if ((offset.top + h - sizes.forDivBoxDela) > (getMainTableHeight() + bigOffset.top)) return false;
    if ((offset.left + w - sizes.forDivBoxDela) > (getMainTableWidth() + bigOffset.left)) return false;
    return true;

}

//Проба функции по повороту коробля в вертикальное положение
function onClickRotate(myObject) {
    let myOffset=myObject.offset();
    if (ifDragged) return;
    let xPos = myOffset.left;
    let yPos = myOffset.top;
    let cellStart=getElsAt(yPos, xPos);
    if (cellStart.length==0) return;

    cellObject.fromId(cellStart.attr("id"));
    let shipLength=Model.getWidth(cellObject.x, cellObject.y);
    if (shipLength==1) return;
    let savedId=Model.getId(cellObject.x, cellObject.y);
    let savedPos=Model.getPos(cellObject.x, cellObject.y);
    let desiredPos;

    if (savedPos==positioN.HOR) desiredPos=positioN.VERT;
    else desiredPos=positioN.HOR;

    let resultChk=Model.validateShip(cellObject.x, cellObject.y, desiredPos, shipLength, savedId);

    if (resultChk) {
        Model.deleteShip(cellObject.x, cellObject.y, shipLength);
        Model.poseShip(cellObject.x, cellObject.y, desiredPos, shipLength, savedId);
        myObject.remove();
        insertDiv(xPos, yPos, shipLength, desiredPos);
        Model.updateViev();
    }
}



//Найдем попали в центр какой ячейки +/- погрешность
function getElsAt(top, left) {
    return $("body").find(".items")
        .filter(function () {
            let centerYBox = $(this).offset().top + sizes.forDivBox / 2;
            let centerXBox = $(this).offset().left + sizes.forDivBox / 2;
            let centerYShip = top + sizes.forDivBox / 2;
            let centerXShip = left + sizes.forDivBox / 2;
            let deltaY = Math.abs(centerYBox - centerYShip);
            let deltaX = Math.abs(centerXBox - centerXShip);
            return deltaX < sizes.forDivBoxDela
                && deltaY < sizes.forDivBoxDela;
        });
}


//Вставляет виртуальную рамку, чтобы показать как мы перемещаем расставленные суда
function insertDiv(myX, myY, widTH, myPos) {
    let tagToadd = getElsAt(myY, myX);
    myY = tagToadd.offset().top;
    myX = tagToadd.offset().left;
    let newDiv;
    if (myPos === positioN.HOR) {
        newDiv = "<div class='frameFor draggable1' style='top: "
            + myY + "px; left: "
            + myX + "px; " +
            "width: " + widTH * sizes.forDivBox * 1.1
            + "px; height: " +
            sizes.forDivBox * 1.1 + "px;'>" + "</div>";
    } else {
        newDiv = "<div class='frameFor draggable1' style='top: "
            + myY + "px; left: "
            + myX + "px; " +
            "height: " + widTH * sizes.forDivBox * 1.1
            + "px; width: " +
            sizes.forDivBox * 1.1 + "px;'>" + "</div>";
    }
    $("body").append(newDiv);
    forDragDiv();

    //Настраиваем обработчик событий для переворота кораблика
    let dragging = $(".draggable1");
    dragging.on("mousedown", function () {
        ifDragged = false;
    }());
    dragging.on("mouseup", function () {
        onClickRotate($(this));
    });
}
