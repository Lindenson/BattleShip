package wolper;


import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wolper.domain.ShipList;
import wolper.game.GameLogic;
import wolper.game.PlayerValidator;
import wolper.messaging.EventMessengerImpl;
import wolper.persistence.database.UserDaoImpl;
import wolper.persistence.inmemory.GameInMemoryDaoImpl;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest
@ContextConfiguration(classes = {TestConfig.class})
class ControllerTest {
    public static final String SHIP_UPDATE = "{\"shipLines\" : [[{\"x\" : 5, \"y\" : 5, \"staT\" : 1, \"pos\" : 1, \"siZe\" : 3, \"commonGranz\" : 0, \"id\" : 1}]]}";
    public static final String SHIP_LIST = "{\"smallSipList\":[{\"checkSet\":[\"A1\",\"B1\",\"C1\"]}]}";

    @MockBean(name = "users")
    UserDaoImpl userDao;

    @MockBean(name = "gameDao")
    GameInMemoryDaoImpl gameInMemoryDao;

    @MockBean
    GameLogic gameLogic;

    @MockBean
    PlayerValidator playerValidator;

    @MockBean
    RabbitTemplate rabbitTemplate;

    @MockBean(name = "messenger")
    EventMessengerImpl eventMessenger;

    @Captor
    ArgumentCaptor<ShipList> shipListArgumentCaptor;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void home() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/home")).andDo(print())
                .andExpect(MockMvcResultMatchers.view().name("home"));
    }

    @Test
    public void login() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/login")).andDo(print())
                .andExpect(MockMvcResultMatchers.view().name("login"));
    }

    @Test
    public void nonAuthorizedGame() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/game")).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
    }

    @Test
    public void nonAuthorizedRest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/gamerInfo")).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
    }

    @Test
    public void nonAuthorizedStomp() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/data")).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
    }


    @Test
    @WithMockUser
    public void authorizedGame() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/game")).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/game?execution=e1s1"));
    }

    @Test
    @WithMockUser
    public void authorizedRestGamers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/rest/gamers")).andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    @WithMockUser
    public void authorizedRestdoMove() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/move/mama/papa")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"x\": 5, \"y\": 7}"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("[\"error\",\"вы жулик\"]"));
    }

    @Test
    @WithMockUser
    public void authorizedAccepted() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/accept/papa/mama")
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("[\"inv\",\"OK\"]"));
    }


    @Test
    @WithMockUser
    public void authorizedRejected() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/reject/papa/mama")
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("[\"rej\",\"OK\"]"));
    }

    @Test
    @WithMockUser
    public void authorizedUpdate() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/update/mama")
                        .contentType("application/json")
                        .content(SHIP_UPDATE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(SHIP_LIST));
    }

    @Test
    @WithMockUser
    public void authorizedUpdateWithNullData() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/update/mama")
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @WithMockUser
    public void authorizedSaveAndFetch() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/update/mama")
                        .contentType("application/json")
                        .content(SHIP_UPDATE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(SHIP_LIST));

        verify(gameInMemoryDao).updateShipListByName(any(), shipListArgumentCaptor.capture());
        ShipList shipList = shipListArgumentCaptor.getValue();
        when(gameInMemoryDao.getShipListByName(any())).thenReturn(shipList);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/update/mama")
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(SHIP_LIST));
    }
}