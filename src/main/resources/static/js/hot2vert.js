
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
