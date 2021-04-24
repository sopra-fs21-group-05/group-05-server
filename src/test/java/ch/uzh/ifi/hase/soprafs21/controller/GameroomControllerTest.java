package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameroomPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.GameroomService;
import ch.uzh.ifi.hase.soprafs21.service.ScoreboardService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * GameroomControllerTest
 * This is a WebMvcTest which allows to test the GameroomController i.e. GET/POST request without actually sending them over the network.
 * This tests if the GameroomController works.
 */
@WebMvcTest(GameroomController.class)
public class GameroomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameroomService gameroomService;
    @MockBean
    private UserService userService;
    @MockBean
    private GameService gameService;
    @MockBean
    private ScoreboardService scoreboardService;

    @Test
    public void createGameroom_validInput_gameroomCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);

        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setPassword("123");
        gameroom.setId(2L);
        gameroom.setUsers(users);

        GameroomPostDTO gameroomPostDTO = new GameroomPostDTO();
        gameroomPostDTO.setRoomname("test");
        gameroomPostDTO.setPassword("123");
        gameroomPostDTO.setUserId(1L);

        given(gameroomService.createGameroom(Mockito.any())).willReturn(gameroom);
        given(gameroomService.joinGameroom(Mockito.any(),Mockito.any())).willReturn(gameroom);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/gamerooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameroomPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/gamerooms/2"));
    }

    @Test
    public void createGameroom_invalidInput_conflict() throws Exception {
        GameroomPostDTO gameroomPostDTO = new GameroomPostDTO();
        gameroomPostDTO.setRoomname("test");
        gameroomPostDTO.setPassword("123");
        gameroomPostDTO.setUserId(1L);

        given(gameroomService.createGameroom(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));
        given(gameroomService.joinGameroom(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/gamerooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameroomPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    /**
     * Helper Method to convert DTOs into a JSON string such that the input can be processed
     * Example Input will look like this: {"name": "Test User", "username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
