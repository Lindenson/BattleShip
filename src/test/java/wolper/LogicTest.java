package wolper;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import wolper.domain.GamerSet;
import wolper.logic.AllGames;
import wolper.logic.CrossGamerInfoBuss;
import wolper.logic.EventMessenger;
import wolper.logic.ShipMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogicTest {

    @Mock
    AllGames allGames;
    @Mock
    ShipMapper shipMapper;
    @Mock
    EventMessenger eventMessenger;
    @InjectMocks
    CrossGamerInfoBuss crossGamerInfoBuss;

    @Captor
    ArgumentCaptor<String> from;
    @Captor
    ArgumentCaptor<String> to;

    @Test
    void inviteWithFree(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        crossGamerInfoBuss.inviteOneAnother("mama", "papa");
        verify(eventMessenger).inviteEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void inviteWithBusyFirst(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0).toBuilder().free(false).build());
        crossGamerInfoBuss.inviteOneAnother("mama", "papa");
        verify(eventMessenger, never()).inviteEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
    }

    @Test
    void inviteWithBusySecond(){
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().free(false).build());
        crossGamerInfoBuss.inviteOneAnother("mama", "papa");
        verify(eventMessenger, never()).inviteEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
    }

    @Test
    void acceptWithFree(){
        when(allGames.tryUpdateGamersAtomically(any(), any(), any(), any())).thenCallRealMethod();
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0));
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        crossGamerInfoBuss.acceptInvitation("mama", "papa");
        verify(eventMessenger).inviteAcceptedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void acceptWithoutAtomicity(){
        when(allGames.tryUpdateGamersAtomically(any(), any(), any(), any())).thenCallRealMethod();
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0),
                GamerSet.freshGamerInstance("mama", 0).toBuilder().free(false).build());
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        crossGamerInfoBuss.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void acceptWithBusyFirst(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().free(false).build());
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        crossGamerInfoBuss.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }

    @Test
    void acceptWithBusySecond(){
        when(allGames.getGamerByName(eq("mama"))).thenReturn(GamerSet.freshGamerInstance("mama", 0).toBuilder().free(false).build());
        when(allGames.getGamerByName(eq("papa"))).thenReturn(GamerSet.freshGamerInstance("papa", 0));
        crossGamerInfoBuss.acceptInvitation("mama", "papa");
        verify(eventMessenger, never()).inviteAcceptedEvent(any(), any());
        verify(eventMessenger).inviteRejectedEvent(from.capture(), to.capture());
        assertEquals("mama", from.getValue());
        assertEquals("papa", to.getValue());
    }


}
