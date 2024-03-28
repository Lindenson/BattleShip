package wolper.dao;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import wolper.domain.GamerSet;
import wolper.messaging.EventMessenger;
import wolper.persistence.database.UserDao;
import wolper.persistence.inmemory.GameInMemoryDaoImpl;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;


import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {GameInMemoryDaoImpl.class})
@TestMethodOrder(OrderAnnotation.class)
class GameDaoTest {

    @MockBean(name = "userDao")
    UserDao userDAO;

    @MockBean(name = "messenger")
    EventMessenger eventMessenger;

    @Autowired
    GameInMemoryDaoImpl gameInMemoryDao;

    @Test
    @Order(1)
    void created(){
        gameInMemoryDao.createGamerByName("player1");
        Collection<GamerSet> allGamers = gameInMemoryDao.getAllGamers();

        assertEquals(1, allGamers.stream().count());
        assertEquals("player1", allGamers.stream().map(GamerSet::getName).findFirst().get());
    }


    @Test
    @Order(2)
    void notUpdateIfStaleDataAtomically(){
        gameInMemoryDao.createGamerByName("player2");
        List<GamerSet> allGamers = gameInMemoryDao.getAllGamers().stream().toList();
        assertEquals(2, allGamers.stream().count());

        List<GamerSet> allGamersAfter = allGamers.stream().map(it -> it.toBuilder().rating(10).build()).toList();
        gameInMemoryDao.tryUpdateGamersAtomically(allGamers.get(0), allGamersAfter.get(1), allGamersAfter.get(0), allGamersAfter.get(1));

        Collection<GamerSet> allGamersUpdated = gameInMemoryDao.getAllGamers();
        assertEquals(2, allGamersUpdated.stream().count());
        assertEquals(0, allGamersUpdated.stream().map(GamerSet::getRating).filter(it -> it.equals(10)).count());
    }


    @Test
    @Order(3)
    void tryUpdateGamersAtomically(){

        List<GamerSet> allGamers = gameInMemoryDao.getAllGamers().stream().toList();
        assertEquals(2, allGamers.stream().count());


        List<GamerSet> allGamersAfter = allGamers.stream().map(it -> it.toBuilder().rating(10).partner("player2").build()).toList();
        boolean res = gameInMemoryDao.tryUpdateGamersAtomically(allGamers.get(0), allGamers.get(1), allGamersAfter.get(0), allGamersAfter.get(1));
        Collection<GamerSet> allGamersUpdated = gameInMemoryDao.getAllGamers();

        assertTrue(res);
        assertEquals(2, allGamersUpdated.stream().count());
        assertEquals(2, allGamersUpdated.stream().map(GamerSet::getRating).filter(it -> it.equals(10)).count());
    }

    @Test
    @Order(4)
    void neverUpdateSameObjects(){

        List<GamerSet> allGamers = gameInMemoryDao.getAllGamers().stream().toList();
        assertEquals(2, allGamers.stream().count());


        List<GamerSet> allGamersAfter = allGamers.stream().map(it -> it.toBuilder().rating(5).build()).toList();
        boolean res = gameInMemoryDao.tryUpdateGamersAtomically(allGamers.get(0), allGamers.get(0), allGamersAfter.get(0), allGamersAfter.get(0));
        Collection<GamerSet> allGamersUpdated = gameInMemoryDao.getAllGamers();

        assertFalse(res);
        assertEquals(2, allGamersUpdated.stream().count());
        assertEquals(2, allGamersUpdated.stream().map(GamerSet::getRating).filter(it -> it.equals(10)).count());
    }

    @Test
    @Order(5)
    void removeGamerAndInform(){
        //партнер установлен в tryUpdateGamersAtomically
        gameInMemoryDao.removeGamerByName("player1");

        List<GamerSet> allGamers = gameInMemoryDao.getAllGamers().stream().toList();
        assertEquals(1, allGamers.stream().count());

        verify(eventMessenger).escapedPlayEvent(eq("player2"));
        verify(eventMessenger).listOfPlayersChangedEvent(any());
        verify(userDAO).setRatingOnExit(any(), anyInt());
    }
}
