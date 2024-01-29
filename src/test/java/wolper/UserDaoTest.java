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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
    void testTransactionPass(){
        Gamer gamer = new Gamer();
        gamer.setName("denis");
        gamer.setPassword("papa");
        gamer.setRole("gamer");
        userDao.saveGamer(gamer);

        verify(database, times(1)).update(any(), any(), any());
        verify(database, times(1)).update(any(), (SqlParameterSource) any());
    }

    @Test
    void testTransactionFails(){
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
}
