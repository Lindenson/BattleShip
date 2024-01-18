package wolper.logic;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wolper.domain.BoardOfShips;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;

@Service("shipService")
@RequiredArgsConstructor
public class ShipMapper {

    private final AllGames allGames;

    // Метод расставленные корабли (как их присылает фронтэнд) и сканирует в удобное для
    // бекэнеда предатвление - получаеться список кораблей с перечнем занимаемых ими клеточек для быстрой и
    //эффективной проверки
    public void detectSips(String name, BoardOfShips boardOfShips) {


        boolean [][] foundShips = new boolean[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                foundShips[i][j] = false;


        final ShipList shipList = new ShipList();

        BoardOfShips.Ships[][] ships = boardOfShips.getShipLines();
        for (int j = 0; j < 10; j++)
            for (int i = 0; i < 10; i++) {
                if (!foundShips[i][j]) {
                    if (ships[i][j].staT() == 1) {
                        //FoundShip
                        int ofSize = ships[i][j].siZe();
                        final var withChecks = ShipList.getFreeSet();
                        if (ships[i][j].pos() == 1) {
                            //Horizontal Ship
                            for (int m = 0; m < ships[i][j].siZe(); m++) {
                                withChecks.add(ShipList.converter(i + m, j));
                                foundShips[i + m][j] = true;
                            }
                        } else {
                            //Vertical Ship
                            for (int m = 0; m < ships[i][j].siZe(); m++) {
                                withChecks.add(ShipList.converter(i, j + m));
                                foundShips[i][m + j] = true;
                            }
                        }
                        //AdShipToList
                        shipList.smallSipList.add(new ShipList.SmallSip(ofSize, withChecks));
                    }
                }
                //StoreShipListInAllGamesList
                allGames.setShipListByName(name, shipList);
            }
    }

    public byte doHit(String name, int i, int j) {
        ShipList shipList = allGames.getShipListByName(name);

        for (ShipList.SmallSip smallSip : shipList.smallSipList) {
            if (smallSip.contains(i,j)) {
               if(smallSip.checkIfKilled()) {
                   updateGamersScore(name);
                   return 2;
               }
                updateGamersScore(name);
                return 1;
            }
        }
        return 0;

    }

    private void updateGamersScore(String name) {
        GamerSet gamerByName = allGames.getGamerByName(name);
        GamerSet gamerUpdated = GamerSet.addKilled(gamerByName);
        allGames.updateGamer(gamerUpdated);
    }

    public boolean checkKillAll(String name){
        GamerSet gamerSet = allGames.getGamerByName(name);
        return (gamerSet.killedEnough());
    }

}




