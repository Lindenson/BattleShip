package wolper.game;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wolper.domain.BoardOfShips;
import wolper.domain.ShipList;


@Slf4j
@Service
@RequiredArgsConstructor
public class ShipMapper {

    public ShipList map(@NonNull BoardOfShips boardOfShips) {
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
                        if (ships[i][j].siZe()!=0) {
                            //нашли
                            int ofSize = ships[i][j].siZe();
                            final var withChecks = ShipList.getFreeSet();
                            if (ships[i][j].pos() == 1) {
                                //горизонтальный
                                for (int m = 0; m < ships[i][j].siZe(); m++) {
                                    withChecks.add(ShipList.converter(i + m, j));
                                    foundShips[i + m][j] = true;
                                }
                            } else {
                                //вертикальный
                                for (int m = 0; m < ships[i][j].siZe(); m++) {
                                    withChecks.add(ShipList.converter(i, j + m));
                                    foundShips[i][m + j] = true;
                                }
                            }
                            //добавлям
                            shipList.smallSipList.add(new ShipList.SmallSip(ofSize, withChecks));
                        }
                    }
                }
        } catch (Exception e) {
            log.error("Board create exception: {}", e.getMessage());
            return null;
        }
        return shipList;
    }
}




