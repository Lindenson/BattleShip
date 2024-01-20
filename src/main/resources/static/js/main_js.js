//КОНСТАНТЫ ДЛЯ РАБОТЫ ПРОГРАММЫ
//Размер клеточки
var forDivBox= 50;
//Погрешность позиционирования
var forDivBoxDela= 25;
//Считаем сколько мы расставили кораблей 
var goFuther= 1;


//Узнаем - не мобильный ли у нас
function detectmob() {
    return window.innerWidth <= 1200 && window.innerHeight <= 800;
}


//И настраиваем под мобильник размеры ячеек и допустимую точность позиционирования
function change() {
    if (detectmob()) {
        forDivBox=22;
        forDivBoxDela=14;
        $(".row1").css("margin-bottom", 4);
        $(".spaser3").width(4);
    }
    else {
        forDivBox=50;
        forDivBoxDela=25;
    }
    $(".items").width(forDivBox);
    $(".items").height(forDivBox);
    $(".appoints").width(forDivBox);
    $(".appoints").height(forDivBox);
}






