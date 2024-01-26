package wolper;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
public class ControllersSecurityTest {

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
    public void authorizedStomp() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/data")).andDo(print())
                .andExpect(MockMvcResultMatchers.content().string("Can \"Upgrade\" only to \"WebSocket\"."));
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
        this.mockMvc.perform(MockMvcRequestBuilders.get("/rest/gamerInfo")).andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    @WithMockUser
    public void authorizedRestdoMove() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/doMove/mama/papa")
                .with(csrf())
                .contentType("application/json")
                .content("{\"x\": 5, \"y\": 7}"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("[\"error\",\"you are a cheater\"]"));
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
                .content("{\"shipLines\" : [[{\"x\" : 5, \"y\" : 5, \"staT\" : 1, \"pos\" : 1, \"siZe\" : 3, \"commonGranz\" : 0, \"id\" : 1}]]}")
                .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("{\"smallSipList\":[{\"checkSet\":[\"A1\",\"B1\",\"C1\"]}]}"));
    }

    @Test
    @WithMockUser
    public void authorizedSaveAndFetch() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/update/mama")
                        .contentType("application/json")
                        .content("{\"shipLines\" : [[{\"x\" : 5, \"y\" : 5, \"staT\" : 1, \"pos\" : 1, \"siZe\" : 3, \"commonGranz\" : 0, \"id\" : 1}]]}")
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("{\"smallSipList\":[{\"checkSet\":[\"A1\",\"B1\",\"C1\"]}]}"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/rest/update/mama")
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json("{\"smallSipList\":[{\"checkSet\":[\"A1\",\"B1\",\"C1\"]}]}"));
    }
}