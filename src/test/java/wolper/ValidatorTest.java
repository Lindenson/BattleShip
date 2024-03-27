package wolper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import wolper.domain.GamerSet;
import wolper.events.EventMessenger;
import wolper.logic.PlayerValidator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ValidatorTest {

    private GamerSet testGamer;

    @BeforeEach
    public void init() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<GamerSet> gamerSetClass = GamerSet.class;
        Constructor<GamerSet> declaredConstructor = gamerSetClass.getDeclaredConstructor(String.class, String.class, Integer.TYPE, Integer.TYPE);
        declaredConstructor.setAccessible(true);
        GamerSet gamerSet = declaredConstructor.newInstance("", "", 1, 1);
        testGamer  = gamerSet;
    }

    @Mock
    EventMessenger eventMessenger;
    @InjectMocks
    PlayerValidator playerValidator;

    @Test
    void ifAnyIsNull(){
        assertTrue(playerValidator.ifAnyIsNull(testGamer, testGamer, testGamer, null));
        assertTrue(playerValidator.ifAnyIsNull(testGamer, testGamer, null, testGamer));
        assertTrue(playerValidator.ifAnyIsNull(testGamer, null, testGamer, testGamer));
        assertTrue(playerValidator.ifAnyIsNull(null, testGamer, testGamer, testGamer));
        assertFalse(playerValidator.ifAnyIsNull(testGamer, testGamer, testGamer, testGamer));
    }

    @Test
    void ifAnyIsNotValid(){
        assertTrue(playerValidator.ifAnyIsNotValid("1", "2", testGamer, null));
        assertTrue(playerValidator.ifAnyIsNotValid("1", "2", null, testGamer));
        assertTrue(playerValidator.ifAnyIsNotValid("1", "2",  null, testGamer));
        assertFalse(playerValidator.ifAnyIsNotValid("1", "2", testGamer, testGamer));
    }
}
