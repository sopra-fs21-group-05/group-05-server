package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.GridCoordinates;
import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Picture;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET/POST request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;
    @MockBean
    private UserService userService;
    @MockBean
    private GameroomService gameRoomService;
    @MockBean
    private ScoreboardService scoreboardService;


    @Test
    public void gameSetup_gridPicturesAssigned() throws Exception{
        User user = new User();
        user.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        User user3 = new User();
        user3.setId(3L);
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        users.add(user3);

        Game game = new Game();
        game.setGameId(4L);
        game.setRoundNr(0);

        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setPassword("123");
        gameroom.setId(5L);
        gameroom.setUsers(users);
        gameroom.setGame(game);


        List<Picture> pictureList = new ArrayList<>();
        Picture pic1 = new Picture();
        pic1.setPictureId(6L);
        pictureList.add(pic1);
        Picture pic2 = new Picture();
        pic1.setPictureId(7L);
        pictureList.add(pic2);
        game.setGridPictures(pictureList);


        given(gameService.getGameroomById(Mockito.any())).willReturn(gameroom);
        given(gameService.makePictureList()).willReturn(pictureList);
        given(gameService.assignGridPictures(game,pictureList)).willReturn(game);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/game/setup/{roomId}",gameroom.getId());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andReturn();

    }


    @Test
    public void getMaterialSet_returnsMaterialSetNr() throws Exception {
       User user = new User();
       user.setId(1L);
       user.setMaterialSet(MaterialSet.COLOR_CUBES);

       Game game = new Game();
       game.setGameId(2L);
       game.setRoundNr(0);

        given(gameService.assignMaterialset(Mockito.any(),Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/game/{gameId}/{userId}/set",game.getGameId(),user.getId());


        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }


    @Test
    public void getPicture_returnsPictureAndCoordinates() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setCoordinatesAssignedPicture(GridCoordinates.A1);

        String coordinates = "A1";
        String picture = "encodedPicture";


        Game game = new Game();
        game.setGameId(2L);
        game.setRoundNr(0);

        given(gameService.assignPicture(Mockito.any(),Mockito.any())).willReturn(Map.of(coordinates,picture));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/game/{gameId}/{userId}/picture",game.getGameId(),user.getId());


        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }


    @Test
    public void getWinner_returnsListOfWinners() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setPoints(12);

        User user2 = new User();
        user2.setId(2L);
        user2.setPoints(12);

        List<User> winnersList = new ArrayList<>();
        winnersList.add(user);
        winnersList.add(user2);

        Gameroom gameroom = new Gameroom();
        given(gameRoomService.getGameroomByGameId(Mockito.any())).willReturn(gameroom);
        given(gameRoomService.getGameroomByStartedGame(Mockito.any())).willReturn(gameroom);


        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setGameId(3L);


        given(gameService.getWinner(Mockito.any())).willReturn(winnersList);


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/3/winner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameGetDTO));


        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void submitRecreatedPicture_returnsLocationAsString() throws Exception {
        User user = new User();
        user.setId(1L);

        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameId(3L);
        gamePostDTO.setSubmittedPicture("recreatedPicture");

        Map<Long,String> userRecreations = new HashMap<>();
        userRecreations.put(1L,"recreatedPicture");

        given(gameService.submitPicture(Mockito.any(),Mockito.any(),Mockito.any())).willReturn(userRecreations);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/game/{userId}",user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));


        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/game/1"));
    }

    @Test
    public void getSubmittedPictures_returnsListOfStrings() throws Exception {

        Game game = new Game();
        game.setGameId(1L);

        User user = new User();
        user.setId(2L);
        user.setUsername("test");

        Map<Long, String> userRecreations = new HashMap<>();
        userRecreations.put(2L,"recreatedPicture");
        userRecreations.put(3L,"recreatedPicture2");

        given(gameService.getSubmittedPictures(Mockito.any())).willReturn(userRecreations);
        given(userService.getExistingUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/game/recreations/overview/{gameId}",game.getGameId());


        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void submitAndCheckGuesses_returnsLocationAsString() throws Exception {
        User user = new User();
        user.setId(1L);

        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameId(3L);


        Map<Long,String> userGuesses = new HashMap<>();
        userGuesses.put(1L,"A1");
        userGuesses.put(2L,"B3");

        //mocking void method
        Mockito.doNothing().when(gameService).submitAndCheckGuesses(Mockito.any(),Mockito.any(),Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/game/round/{userId}",user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));


        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/game/round/1"));
    }

    @Test
    public void updateGame_returnsLocationAsString() throws Exception {
        Game game = new Game();
        game.setGameId(1L);
        game.setRoundNr(2);


        given(gameService.updateGame(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/game/{gameId}",game.getGameId());

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void getPictureGrid_returnsMapOfPictureGrid() throws Exception {
        Game game = new Game();
        game.setGameId(1L);
        game.setRoundNr(0);

        Map<String,String> pictureGrid = new HashMap<>();
        pictureGrid.put("A1","pictureOne");
        pictureGrid.put("A2","pictureTwo");

        given(gameService.getPictureGrid(Mockito.any())).willReturn(pictureGrid);


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/game/grid/{gameId}",game.getGameId());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void getRoundNr_returnsMapOfPictureGrid() throws Exception {
        Game game = new Game();
        game.setGameId(1L);
        game.setRoundNr(1);


        given(gameService.getExistingGame(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/game/round/{gameId}",game.getGameId());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());

    }

    @Test
    public void getAllGuessesSubmitted_returnsBoolean() throws Exception {
        Game game = new Game();
        game.setGameId(1L);
        game.setRoundNr(1);


        given(gameService.getExistingGame(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/game/guesses/{gameId}",game.getGameId());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
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
