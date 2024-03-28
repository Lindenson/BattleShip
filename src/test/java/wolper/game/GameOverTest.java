package wolper.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import wolper.application.BattleShip;
import wolper.domain.BoardOfShips;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;
import wolper.persistence.inmemory.GameDao;
import wolper.messaging.EventMessenger;
import wolper.game.GameLogic;
import wolper.game.PlayerValidator;
import wolper.game.ShipMapper;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

//для одного теста, так как мы мокируем статические методы
@ExtendWith(MockitoExtension.class)
class GameOverTest {

    @Mock
    PlayerValidator playerValidator;
    @Mock
    GameDao gameDao;
    @Mock
    ShipMapper shipMapper;
    @Mock
    EventMessenger eventMessenger;
    @InjectMocks
    GameLogic gameLogic;
    @Captor
    ArgumentCaptor<String> to;

    private static String ships = """
            {"shipLines":[[{"x":1,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":1,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":10},{"x":7,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":8,"y":1,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":9},{"x":9,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}],[{"x":1,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":7,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":8,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":9,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":2,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":8}],[{"x":1,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":3,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":3,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":4,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":4,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":7}],[{"x":1,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":5,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":5,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":2,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":3,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":6,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":6}],[{"x":1,"y":7,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":4,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":7,"y":7,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":7,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":7,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":6}],[{"x":1,"y":8,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":8,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":8,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":8,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":9,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":9,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":9,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":9,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":4},{"x":8,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":9,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":5},{"x":10,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}],[{"x":1,"y":10,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":10,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":10,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":10,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":4},{"x":8,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":10,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":5},{"x":10,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}]]}
            """;

    @AfterAll
    static void clean(){
        when(GamerSet.addKilled(any())).thenCallRealMethod();
        when(GamerSet.withAddRating(any())).thenCallRealMethod();
    }

    @Test
    void doNextMoveAndWin() throws Exception {
        when(gameDao.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("daughter").build());

        GamerSet daughter = mock(GamerSet.class);
        when(daughter.getName()).thenReturn("daughter");
        when(daughter.ifKilledEnough()).thenReturn(true);

        mockStatic(GamerSet.class);
        when(GamerSet.addKilled(any())).thenReturn(daughter);
        when(GamerSet.withAddRating(any())).thenReturn(daughter);
        when(gameDao.getGamerByName(eq("daughter"))).thenReturn(daughter);

        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships, BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);
        ShipList.SmallSip smallSip = sList.smallSipList.stream().findFirst().orElse(null);
        ReflectionTestUtils.setField(sList, "smallSipList", List.of(smallSip));

        when(gameDao.getShipListByName(any())).thenReturn(sList);
        gameLogic.doNextMove("mama", "daughter", 1, 7);
        gameLogic.doNextMove("mama", "daughter", 1, 8);
        gameLogic.doNextMove("mama", "daughter", 1, 9);
        String result = gameLogic.doNextMove("mama", "daughter", 1, 10);

        assertEquals("victory", result);
        verify(eventMessenger).gameOverPlayEvent(to.capture(),anyInt(),anyInt());
        verify(gameDao).tryUpdateGamersAtomically(any(),any(),any(),any());

        assertEquals("daughter", to.getValue());
    }
}
