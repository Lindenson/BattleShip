package wolper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import wolper.dao.UserDao;
import wolper.domain.Gamer;
import wolper.domain.LogicException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserDaoTest {

    @Mock
    private NamedParameterJdbcTemplate database;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    UserDao userDao;


    @Test
    void transactionPass(){
        Gamer gamer = new Gamer();
        gamer.setName("denis");
        gamer.setPassword("papa");
        gamer.setRole("gamer");
        userDao.saveGamer(gamer);

        verify(database, times(1)).update(any(), any(), any());
        verify(database, times(1)).update(any(), (SqlParameterSource) any());
    }

    @Test
    void transactionFails(){
        willThrow(new ConcurrencyFailureException("")).given(database).update(any(), any(), any());
        Gamer gamer = new Gamer();
        gamer.setName("denis");
        gamer.setPassword("papa");
        gamer.setRole("gamer");
        try {
            userDao.saveGamer(gamer);
        } catch (LogicException le) {
            assertInstanceOf(LogicException.class, le);
        }

        verify(database, times(1)).update(any(), any(), any());
        verify(database, never()).update(any(), (SqlParameterSource) any());
    }

    @Test
    void doubleUserFound(){
        Gamer gamer = new Gamer();
        gamer.setName("denis");
        gamer.setPassword("papa");
        gamer.setRole("gamer");
        willReturn(List.of(gamer)).given(database).queryForList(any(), (SqlParameterSource) any(), eq(String.class));

        assertTrue(userDao.ifDoubleGamer("denis"));
        verify(database, times(1)).queryForList(any(), (SqlParameterSource) any(), eq(String.class));
    }
}
