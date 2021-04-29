
package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GridCoordinates;
import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Picture;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given

        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setRoundNr(1);


        // when -> any object is being save in the gameRepository -> return the dummy testGame
        when(gameRepository.save(any())).thenReturn(testGame);
    }

    @Test
    public void createGame_validInputs_success() {
        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        // then
        Mockito.verify(gameRepository, Mockito.times(1)).save(any());

        assertNotNull(createdGame.getGameId());
        assertEquals(testGame.getRoundNr(), createdGame.getRoundNr());
    }
    

    @Test
    public void createGame_duplicateInputs_throwsException() {
        // given -> a first game has already been created
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);
        Game createdGame = gameService.createGame(gameroom);
        gameroom.setGame(createdGame);


        // when -> setup additional mocks for UserRepository
        when(gameService.createGame(gameroom)).thenThrow(ResponseStatusException.class);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(gameroom));
    }


    @Test
    void assignGridPictures_success(){
        // given -> a first user has already been created
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);
        Game createdGame = gameService.createGame(gameroom);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(testGame);

        Picture pic1 = new Picture();
        pic1.setPictureId(3L);
        Picture pic2 = new Picture();
        pic2.setPictureId(4L);

        List<Picture> pictureList = new ArrayList<>();
        pictureList.add(pic1);
        pictureList.add(pic2);

        Game targetGame = gameService.assignGridPictures(createdGame,pictureList);
        assertEquals(testGame, targetGame);
        assertEquals(pictureList.size(), targetGame.getGridPictures().size());
    }

    @Test
    void assignMaterialSet_getsNextMaterialSet(){
        // given -> a first user has already been created
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        User user = new User();
        user.setToken("token");
        user.setId(3L);
        user.setMaterialSet(MaterialSet.COLOR_CUBES);

        List<User> players = new ArrayList<>();
        players.add(user);

        createdGame.setUserList(players);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);


        User updatedUser = gameService.assignMaterialset(createdGame.getGameId(),user.getId());
        assertEquals(updatedUser.getMaterialSet(),MaterialSet.BUILDING_BLOCKS );
    }

    @Test
    void assignPictureToRecreate_returnAssignedPicture(){
        // given -> a first user has already been created
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        List<GridCoordinates> coordinatesList = new ArrayList<>();
        coordinatesList.add(GridCoordinates.A1);
        createdGame.setGridCoordinates(coordinatesList);

        Picture pic1 = new Picture();
        pic1.setPictureId(5L);
        Picture pic2 = new Picture();
        pic2.setPictureId(4L);
        List<Picture> pictureList = new ArrayList<>();
        pictureList.add(pic1);
        pictureList.add(pic2);
        createdGame.setGridPictures(pictureList);

        User user = new User();
        user.setToken("token");
        user.setId(3L);
        user.setCoordinatesAssignedPicture(GridCoordinates.A1);

        List<User> players = new ArrayList<>();
        players.add(user);

        createdGame.setUserList(players);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(testGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);


        Map<String,String> assignedPicture = gameService.assignPicture(createdGame.getGameId(),user.getId());
        assertEquals(assignedPicture.size(),1);
    }

    @Test
    void getPlayers_returnsListOfPlayers() {
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        User user = new User();
        user.setToken("token");
        user.setId(3L);

        List<User> players = new ArrayList<>();
        players.add(user);

        createdGame.setUserList(players);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);


        List<User> playersInGame = gameService.getPlayers(createdGame.getGameId());
        assertEquals(playersInGame,players);
        assertEquals(playersInGame.size(),1);
    }

    @Test
    void getWinner_success(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);


        //creating 2 new users (can't use create user function as it always returns testUser as specified in the setup)
        User user1 = new User();
        User user2 = new User();

        //making sure the users have different points
        user1.setPoints(2);
        user2.setPoints(4);

        //create a list containing the two users (mocks what the getUsers() function in the userService should return)
        List <User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        createdGame.setUserList(users);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);


        //call to the function
        List <User> winners = gameService.getWinner(createdGame.getGameId());
        // mocking the expected response
        List <User> expectedWinner = new ArrayList<>();
        expectedWinner.add(user2);

        assertEquals(expectedWinner, winners);
        assertFalse(user1.getRestrictedMode());
        assertTrue(user2.getRestrictedMode());
    }


    @Test
    void getWinner_two_winners(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        //creating 2 new users (can't use create user function as it always returns testUser as specified in the setup)
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();

        //making sure the users have different points
        user1.setPoints(2);
        user2.setPoints(4);
        user3.setPoints(4);

        //create a list containing the two users (mocks what the getUsers() function in the userService should return)
        List <User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        createdGame.setUserList(users);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);


        //call to the function
        List <User> winners = gameService.getWinner(createdGame.getGameId());
        // mocking the expected response
        List <User> expectedWinner = new ArrayList<>();
        expectedWinner.add(user2);
        expectedWinner.add(user3);

        assertEquals(expectedWinner, winners);
        assertFalse(user1.getRestrictedMode());
        assertTrue(user2.getRestrictedMode());
        assertTrue(user3.getRestrictedMode());
    }

    

}

