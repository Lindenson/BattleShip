package wolper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import wolper.domain.BoardOfShips;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;
import wolper.logic.AllGames;
import wolper.logic.GameLogic;
import wolper.logic.EventMessenger;
import wolper.logic.ShipMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameLogicTest {

    @Mock
    AllGames allGames;
    @Mock
    ShipMapper shipMapper;
    @Mock
    EventMessenger eventMessenger;
    @InjectMocks
    GameLogic gameLogic;

    @Captor
    ArgumentCaptor<String> from;
    @Captor
    ArgumentCaptor<String> to;

    private static String ships = """
            {"shipLines":[[{"x":1,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":1,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":10},{"x":7,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":8,"y":1,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":9},{"x":9,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}],[{"x":1,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":7,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":8,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":9,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":2,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":8}],[{"x":1,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":3,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":3,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":4,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":4,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":7}],[{"x":1,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":5,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":5,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":2,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":3,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":6,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":6}],[{"x":1,"y":7,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":4,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":7,"y":7,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":7,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":7,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":6}],[{"x":1,"y":8,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":8,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":8,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":8,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":9,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":9,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":9,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":9,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":4},{"x":8,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":9,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":5},{"x":10,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}],[{"x":1,"y":10,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":10,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":10,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":10,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":4},{"x":8,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":10,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":5},{"x":10,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}]]}
            """;

    @Test
    void inviteWithFree(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.inviteOneAnother("mama", "papa");
        verify(eventMessenger).inviteEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void inviteWithBusyFirst(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().free(false).build());
        gameLogic.inviteOneAnother("mama", "papa");
        verify(eventMessenger, never()).inviteEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
    }

    @Test
    void inviteWithBusySecond(){
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().free(false).build());
        gameLogic.inviteOneAnother("mama", "papa");
        verify(eventMessenger, never()).inviteEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
    }

    @Test
    void acceptWithFree(){
        when(allGames.tryUpdateGamersAtomically(any(), any(), any(), any())).thenCallRealMethod();
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.acceptInvitation("mama", "papa");
        verify(eventMessenger).inviteAcceptedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void notAcceptWithoutAtomicity(){
        when(allGames.tryUpdateGamersAtomically(any(), any(), any(), any())).thenCallRealMethod();
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0),
                GamerSet.freshGamerInstance("mama", 0).toBuilder().free(false).build());
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void notAcceptWithBusyFirst(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().free(false).build());
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void notAcceptWithBusySecond(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().free(false).build());
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void rejectIfNotPlaying(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.rejectInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void notRejectIfAlreadyPlaying(){
        when(allGames.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().playWith("papa").build());
        when(allGames.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().playWith("mama").build());
        gameLogic.rejectInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger, never()).inviteRejectedEvent(any(), any());
    }

    @Test
    void informPartnerOfFinishedSetUp(){
        when(allGames.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().playWith("papa").build());
        when(allGames.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().playWith("mama").build());
        gameLogic.informPartnerOfFinishedSetUp("mama");
        verify(eventMessenger).readyToPlayEvent(to.capture());
    }

    @Test
    void notInformPartnerOfFinishedSetUpIfNotPlayingWith(){
        when(allGames.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().playWith("son").build());
        when(allGames.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().playWith("mama").build());
        gameLogic.informPartnerOfFinishedSetUp("mama");
        verify(eventMessenger, never()).readyToPlayEvent(any());
    }

    @Test
    void mapperTest() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships, BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);

        assertEquals(10, sList.smallSipList.size());
    }


    @Test
    void doNextMoveZero() throws Exception {
        when(allGames.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().playWith("papa").build());
        when(allGames.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().playWith("mama").build());
        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships,BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);

        when(allGames.getShipListByName(any())).thenReturn(sList);
        String result = gameLogic.doNextMove("mama", "papa", 7, 1);

        assertEquals("zero", result);
    }

    @Test
    void doNextMoveHit() throws Exception {
        when(allGames.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().playWith("papa").build());
        when(allGames.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().playWith("mama").build());
        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships, BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);

        when(allGames.getShipListByName(any())).thenReturn(sList);
        String result = gameLogic.doNextMove("mama", "papa", 1, 7);

        assertEquals("injured", result);
    }

    @Test
    void doNextMoveError() throws Exception {
        when(allGames.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().playWith("son").build());
        when(allGames.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().playWith("mama").build());
        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships, BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);

        when(allGames.getShipListByName(any())).thenReturn(sList);
        String result = gameLogic.doNextMove("mama", "papa", 1, 7);

        assertEquals("error", result);
    }

    @Test
    void doNextMoveKilled() throws Exception {
        when(allGames.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().playWith("papa").build());
        when(allGames.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().playWith("mama").build());
        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships, BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);

        when(allGames.getShipListByName(any())).thenReturn(sList);
        gameLogic.doNextMove("mama", "papa", 1, 7);
        gameLogic.doNextMove("mama", "papa", 1, 8);
        gameLogic.doNextMove("mama", "papa", 1, 9);
        String result = gameLogic.doNextMove("mama", "papa", 1, 10);


        assertEquals("killed", result);
    }
}
