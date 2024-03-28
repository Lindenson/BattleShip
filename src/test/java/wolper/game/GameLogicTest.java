package wolper.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import wolper.application.BattleShip;
import wolper.persistence.inmemory.GameInMemoryDaoImpl;
import wolper.domain.BoardOfShips;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;
import wolper.game.GameLogic;
import wolper.messaging.EventMessenger;
import wolper.game.PlayerValidator;
import wolper.game.ShipMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameLogicTest {

    @Mock
    PlayerValidator playerValidator;
    @Mock
    GameInMemoryDaoImpl gameDao;
    @Mock
    EventMessenger eventMessenger;
    @InjectMocks
    GameLogic gameLogic;
    @Captor
    ArgumentCaptor<String> from;
    @Captor
    ArgumentCaptor<String> to;
    @Captor
    ArgumentCaptor<GamerSet> inviter;
    @Captor
    ArgumentCaptor<GamerSet> invitee;


    private static String ships = """
            {"shipLines":[[{"x":1,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":1,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":1,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":10},{"x":7,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":8,"y":1,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":9},{"x":9,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":1,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}],[{"x":1,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":2,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":7,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":8,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":9,"y":2,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":2,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":8}],[{"x":1,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":3,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":3,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":3,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":4,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":4,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":4,"staT":1,"pos":1,"siZe":1,"commonGranz":0,"id":7}],[{"x":1,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":2,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":3,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":5,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":5,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":5,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":2,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":3,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":4,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":5,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":6,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":7,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":6,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":6,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":6,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":6}],[{"x":1,"y":7,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":4,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":6,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":7,"y":7,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":8,"y":7,"staT":3,"pos":1,"siZe":0,"commonGranz":0,"id":0},{"x":9,"y":7,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":10,"y":7,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":6}],[{"x":1,"y":8,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":8,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":8,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0},{"x":8,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":10,"y":8,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0}],[{"x":1,"y":9,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":9,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":9,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":9,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":4},{"x":8,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":9,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":5},{"x":10,"y":9,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}],[{"x":1,"y":10,"staT":1,"pos":1,"siZe":4,"commonGranz":0,"id":1},{"x":2,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":3,"y":10,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":2},{"x":4,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":5,"y":10,"staT":1,"pos":1,"siZe":3,"commonGranz":0,"id":3},{"x":6,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":7,"y":10,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":4},{"x":8,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":2,"id":0},{"x":9,"y":10,"staT":1,"pos":1,"siZe":2,"commonGranz":0,"id":5},{"x":10,"y":10,"staT":2,"pos":1,"siZe":0,"commonGranz":1,"id":0}]]}
            """;

    @Test
    void inviteWithFree(){
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.inviteOneAnother("mama", "papa");
        verify(eventMessenger).inviteEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void inviteWithBusyFirst(){
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().partner("son").build());
        gameLogic.inviteOneAnother("mama", "papa");
        verify(eventMessenger, never()).inviteEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
    }

    @Test
    void inviteWithBusySecond(){
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("son").build());
        gameLogic.inviteOneAnother("mama", "papa");
        verify(eventMessenger, never()).inviteEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
    }

    @Test
    void notInviteOrRejectAgain(){
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("papa").build());
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().partner("mama").build());
        gameLogic.inviteOneAnother("mama", "papa");
        verify(eventMessenger, never()).inviteEvent(any(), any());
        verify(eventMessenger, never()).inviteRejectedEvent(from.capture(), to.capture());
        verify(eventMessenger, never()).inviteEvent(any(), any());
    }

    @Test
    void acceptWithFree(){
        when(gameDao.tryUpdateGamersAtomically(any(), any(), any(), any())).thenCallRealMethod();
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));

        gameLogic.acceptInvitation("mama", "papa");

        verify(eventMessenger).inviteAcceptedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());

        verify(gameDao).tryUpdateGamersAtomically(any(), any(), inviter.capture(), invitee.capture());
        assertEquals("mama", inviter.getValue().getName());
        assertEquals("papa", invitee.getValue().getName());
        assertEquals("papa", inviter.getValue().getPartner());
        assertEquals("mama", invitee.getValue().getPartner());
    }

    @Test
    void notAcceptWithoutAtomicity(){
        when(gameDao.tryUpdateGamersAtomically(any(), any(), any(), any())).thenCallRealMethod();
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0),
                GamerSet.freshGamerInstance("mama", 0).toBuilder().build());
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void notAcceptWithBusyFirst(){
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().build());
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void notAcceptWithBusySecond(){
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().build());
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void rejectIfNotPlaying(){
        when(gameDao.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(gameDao.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        gameLogic.rejectInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void notRejectIfAlreadyPlaying(){
        when(gameDao.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("papa").build());
        when(gameDao.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().partner("mama").build());
        gameLogic.rejectInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger, never()).inviteRejectedEvent(any(), any());
    }

    @Test
    void informPartnerOfFinishedSetUp(){
        when(gameDao.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("papa").build());
        when(gameDao.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().partner("mama").build());

        gameLogic.informPartnerOfFinishedSetUp("mama");

        verify(eventMessenger).readyToPlayEvent(to.capture());
    }

    @Test
    void notInformPartnerOfFinishedSetUpIfNotPlayingWith(){
        when(gameDao.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("son").build());
        when(gameDao.getGamerByName(eq("son")))
                .thenReturn(GamerSet.freshGamerInstance("son", 0).toBuilder().partner("ded").build());
        when(playerValidator.ifAnyIsNotValid(any(),any(),any(),any())).thenReturn(true);

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
        when(gameDao.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("papa").build());
        when(gameDao.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().partner("mama").build());
        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships,BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);

        when(gameDao.getShipListByName(any())).thenReturn(sList);
        String result = gameLogic.doNextMove("mama", "papa", 7, 1);

        assertEquals("zero", result);
    }

    @Test
    void doNextMoveHit() throws Exception {
        when(gameDao.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("papa").build());
        when(gameDao.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().partner("mama").build());
        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships, BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);

        when(gameDao.getShipListByName(any())).thenReturn(sList);
        String result = gameLogic.doNextMove("mama", "papa", 1, 7);

        assertEquals("injured", result);
    }

    @Test
    void doNextMoveError() throws Exception {
        when(gameDao.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("son").build());
        when(gameDao.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().partner("mama").build());

        String result = gameLogic.doNextMove("mama", "papa", 1, 7);
        assertEquals("error", result);
    }

    @Test
    void doNextMoveKilled() throws Exception {
        when(gameDao.getGamerByName(eq("mama")))
                .thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().partner("papa").build());
        when(gameDao.getGamerByName(eq("papa")))
                .thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().partner("mama").build());
        ObjectMapper objectMapper = new ObjectMapper();
        BoardOfShips shipList = objectMapper.readValue(ships, BoardOfShips.class);

        ShipMapper shipMapper = new ShipMapper();
        ShipList sList = shipMapper.map(shipList);

        when(gameDao.getShipListByName(any())).thenReturn(sList);
        gameLogic.doNextMove("mama", "papa", 1, 7);
        gameLogic.doNextMove("mama", "papa", 1, 8);
        gameLogic.doNextMove("mama", "papa", 1, 9);
        String result = gameLogic.doNextMove("mama", "papa", 1, 10);

        assertEquals("killed", result);
    }
}
