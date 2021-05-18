package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserAuthDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.ScoreboardService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.JsonPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScoreboardController.class)
public class ScoreboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScoreboardService scoreboardService;
    @MockBean
    private GameService gameService;
    @MockBean
    private UserService userService;

    @Test
    public void getScoreboard_success() throws Exception {
        //create game and userpoints to add to scoreboard
        Game game = new Game();
        Map<Long, Integer> userPoints = new HashMap<>();
        userPoints.put(1L, 4);

        //create scoreboard
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.setGame(game);
        scoreboard.setScoreboardId(1L);
        scoreboard.setUserPoints(userPoints);

        //create User
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        //ensure the services return the above specified entities
        given(gameService.getExistingGame(Mockito.any())).willReturn(game);
        given(scoreboardService.findScoreboardByGame(Mockito.any())).willReturn(scoreboard);
        given(userService.getExistingUser(Mockito.any())).willReturn(user);

        //build the server request
        MockHttpServletRequestBuilder getRequest = get("/scoreboards/1");

        //perform the request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreboardId", is(scoreboard.getScoreboardId()), Long.class));
                //.andExpect(jsonPath("$.userPoints", is(scoreboard.getUserPoints())));
    }

    @Test
    public void getScoreboard_notValidGameId_failure() throws Exception {
        //create game and userpoints to add to scoreboard
        Game game = new Game();
        Map<Long, Integer> userPoints = new HashMap<>();
        userPoints.put(1L, 4);

        //create scoreboard
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.setGame(game);
        scoreboard.setScoreboardId(1L);
        scoreboard.setUserPoints(userPoints);

        //ensure the scoreboardService will return the created scoreboard
        given(scoreboardService.findScoreboardByGame(Mockito.any())).willReturn(scoreboard);

        //ensure the getExisting game will throw an exception when no valid gameId is used
        given(gameService.getExistingGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));


        MockHttpServletRequestBuilder getRequest = get("/scoreboards/4");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());

    }

    @Test
    public void updateScoreboard_success() throws Exception {
        //create game and userpoints to add to scoreboard
        Game game = new Game();
        Map<Long, Integer> userPoints = new HashMap<>();
        userPoints.put(1L, 4);

        //create scoreboard
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.setGame(game);
        scoreboard.setScoreboardId(1L);
        scoreboard.setUserPoints(userPoints);

        //ensure the scoreboardService will return the created scoreboard
        given(gameService.getExistingGame(Mockito.any())).willReturn(game);
        given(scoreboardService.findScoreboardByGame(Mockito.any())).willReturn(scoreboard);

        MockHttpServletRequestBuilder postRequest = post("/scoreboards/1");

        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk());

    }

    @Test
    public void updateScoreboard_noValidGameId_failure() throws Exception {
        //create game and userpoints to add to scoreboard
        Game game = new Game();
        Map<Long, Integer> userPoints = new HashMap<>();
        userPoints.put(1L, 4);

        //create scoreboard
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.setGame(game);
        scoreboard.setScoreboardId(1L);
        scoreboard.setUserPoints(userPoints);

        //ensure the scoreboardService will return the created scoreboard
        given(scoreboardService.findScoreboardByGame(Mockito.any())).willReturn(scoreboard);

        //ensure the getExisting game will throw an exception when no valid gameId is used
        given(gameService.getExistingGame(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));


        MockHttpServletRequestBuilder postRequest = post("/scoreboards/1");

        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());

    }
}
