package wolper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import wolper.domain.GamerSet;
import wolper.logic.EventMessenger;
import wolper.logic.PlayerValidator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ValidatorTest {

    static GamerSet testGamer;

    @BeforeAll
    public static void init(){
         testGamer = GamerSet.freshGamerInstance("1",1);
    }

    @Mock
    EventMessenger eventMessenger;
    @InjectMocks
    PlayerValidator playerValidator;

    @Test
    void ifAnyIsNull(){
        assertNotNull(testGamer);
        assertTrue(playerValidator.ifAnyIsNull(testGamer, testGamer, testGamer, null));
        assertTrue(playerValidator.ifAnyIsNull(testGamer, testGamer, null, testGamer));
        assertTrue(playerValidator.ifAnyIsNull(testGamer, null, testGamer, testGamer));
        assertTrue(playerValidator.ifAnyIsNull(null, testGamer, testGamer, testGamer));
        assertFalse(playerValidator.ifAnyIsNull(testGamer, testGamer, testGamer, testGamer));
    }

    @Test
    void ifAnyIsNotValid(){
        assertNotNull(testGamer);
        assertTrue(playerValidator.ifAnyIsNotValid("1", "2", testGamer, null));
        assertTrue(playerValidator.ifAnyIsNotValid("1", "2", null, testGamer));
        assertTrue(playerValidator.ifAnyIsNotValid("1", "2",  null, testGamer));
        assertFalse(playerValidator.ifAnyIsNotValid("1", "2", testGamer, testGamer));
    }
}
