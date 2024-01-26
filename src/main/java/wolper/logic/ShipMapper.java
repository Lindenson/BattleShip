package wolper.logic;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wolper.domain.BoardOfShips;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;

@Service("shipService")
@RequiredArgsConstructor
@Slf4j
public class ShipMapper {

    private final AllGames allGames;

    // Метод расставленные корабли (как их присылает фронтэнд) и сканирует в удобное для
    // бекэнеда предатвление - получаеться список кораблей с перечнем занимаемых ими клеточек для быстрой и
    //эффективной проверки
    public ShipList detectSips(@NonNull String name, @NonNull BoardOfShips boardOfShips) {
        final ShipList shipList = new ShipList();
        try {
            boolean[][] foundShips = new boolean[10][10];
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 10; j++)
                    foundShips[i][j] = false;
            BoardOfShips.Ships[][] ships = boardOfShips.getShipLines();
            for (int j = 0; j < ships.length; j++)
                for (int i = 0; i < ships[j].length; i++) {
                    if (!foundShips[i][j]) {
                        if (ships[i][j].staT() != 3) {
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
                }
        } catch (Exception e) {
            log.error("Board create exception: {}", e.getMessage());
            return null;
        }
        //StoreShipListInAllGamesList
        allGames.setShipListByName(name, shipList);
        return shipList;
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
        return (gamerSet.ifKilledEnough());
    }

}




