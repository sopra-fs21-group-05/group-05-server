package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameroomGetDTO;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    @Test
    public void getGameroom_validInput_returnGameroom() throws Exception {

        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setId(2L);

        Long roomId = 2L;

        given(gameroomService.getGameroomById(roomId)).willReturn(gameroom);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/gamerooms/overview/2")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameroom.getId()), Long.class))
                .andExpect(jsonPath("$.roomname", is(gameroom.getRoomname())));
    }

    @Test
    public void getGameroom_invalidInput_notFound() throws Exception {

        Long roomId = 3L;

        given(gameroomService.getGameroomById(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/gamerooms/overview/2")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void putGameroom_validInput_startGame() throws Exception {

        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setId(2L);

        Game game = new Game();
        game.setGameroom(gameroom);
        game.setGameId(3L);

        Scoreboard scoreboard = new Scoreboard();
        scoreboard.setGame(game);

        given(gameroomService.getGameroomById(Mockito.any())).willReturn(gameroom);
        given(gameService.createGame(gameroom)).willReturn(game);
        given(scoreboardService.createScoreboard(game)).willReturn(scoreboard);
        given(gameroomService.addGame(gameroom, game)).willReturn(gameroom);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/gamerooms/overview/2")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(game.getGameId()), Long.class));
    }

    @Test
    public void putGameroom_invalidInput_conflict() throws Exception {

        given(gameroomService.getGameroomById(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));
        given(gameService.createGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));
        given(scoreboardService.createScoreboard(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));
        given(gameroomService.addGame(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/gamerooms/overview/2")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void givenGamerooms_getGameroomList_thenReturnJsonArray() throws Exception {

        Gameroom gameroom = new Gameroom();
        gameroom.setId(2L);
        gameroom.setRoomname("test");

        List<Gameroom> gamerooms = Collections.singletonList(gameroom);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(gameroomService.getGamerooms()).willReturn(gamerooms);

        // when
        MockHttpServletRequestBuilder getRequest = get("/gamerooms/list")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(gameroom.getId()), Long.class))
                .andExpect(jsonPath("$[0].roomname", is(gameroom.getRoomname())));
    }

    @Test
    public void joinGameroom_validInput_userAdded() throws Exception {

        GameroomPostDTO gameroomPostDTO = new GameroomPostDTO();
        gameroomPostDTO.setRoomname("test");
        gameroomPostDTO.setRoomId(2L);
        gameroomPostDTO.setPassword("123");
        gameroomPostDTO.setUserId(1L);

        User user = new User();
        user.setId(1L);
        List<User> users = Collections.singletonList(user);

        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setPassword("123");
        gameroom.setId(2L);
        gameroom.setUsers(users);

        given(userService.getExistingUser(Mockito.any())).willReturn(user);
        given(gameroomService.joinGameroom(Mockito.any(),Mockito.any())).willReturn(gameroom);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/gamerooms/list/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameroomPostDTO));;

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void joinGameroom_invalidInput_Unauthorized() throws Exception {

        GameroomPostDTO gameroomPostDTO = new GameroomPostDTO();
        gameroomPostDTO.setRoomname("test");
        gameroomPostDTO.setRoomId(2L);
        gameroomPostDTO.setPassword("123");
        gameroomPostDTO.setUserId(1L);

        given(userService.getExistingUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        given(gameroomService.joinGameroom(Mockito.any(),Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/gamerooms/list/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameroomPostDTO));;

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
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
