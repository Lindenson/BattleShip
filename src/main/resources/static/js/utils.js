import { sizes, myState } from "/js/set_and_state.js";

export {
    adaptSize,
    strFinishesWith,
    strStartsWith,
    myName,
    yourMove,
    getMainTableHeight,
    getMainTableWidth,
    getMainTableOffset,
    setPageHeader,
    is_safari
}



//Определяем браузер
let is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
let is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
let is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
let is_safari = navigator.userAgent.indexOf("Safari") > -1;
let is_opera = navigator.userAgent.toLowerCase().indexOf("op") > -1;
if ((is_chrome) && (is_safari)) {
    is_safari = false;
}
if ((is_chrome) && (is_opera)) {
    is_chrome = false;
}

//Узнаем - не мобильный ли у нас
function detectmob() {
    return window.innerWidth <= 1200 && window.innerHeight <= 800;
}

//И настраиваем под мобильник размеры ячеек и допустимую точность позиционирования
function adaptSize() {
    if (detectmob()) {
        sizes.forDivBox = 22;
        sizes.forDivBoxDela = 14;
        $(".row1").css("margin-bottom", 4);
        $(".spaser3").width(4);
    } else {
        sizes.forDivBox = 50;
        sizes.forDivBoxDela = 25;
    }
    $(".items").width(sizes.forDivBox);
    $(".items").height(sizes.forDivBox);
    $(".appoints").width(sizes.forDivBox);
    $(".appoints").height(sizes.forDivBox);
}


//помощники
function getMainTableOffset() {
    return $(".todrag").offset();
}

function getMainTableWidth() {
    return $(".todrag").width();
}

function getMainTableHeight() {
    return $(".todrag").height();
}

function setPageHeader(input) {
    $("#headerForMessage").text(input);
}

function yourMove() {
    return (myState.partnerReady && myState.yourStep);
}

function myName() {
    return $("#forNamePlacer").text();
}

function strStartsWith(str, prefix) {
    return str.indexOf(prefix) === 0;
}

function strFinishesWith(str, prefix) {
    return str.indexOf(prefix) === str.length - prefix.length;
}




