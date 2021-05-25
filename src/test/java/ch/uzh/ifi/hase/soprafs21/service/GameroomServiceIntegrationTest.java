package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameroomRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WebAppConfiguration
@SpringBootTest
public class GameroomServiceIntegrationTest {

    @Qualifier("gameroomRepository")
    @Autowired
    private GameroomRepository gameroomRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameroomService gameroomService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;


    @BeforeEach
    public void setup() {
        gameroomRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createGameroom_validInputs_success() {
        assertNull(gameroomRepository.findByRoomname("test"));
        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");

        // when
        Gameroom createdGameroom = gameroomService.createGameroom(testGameroom);

        // then
        assertEquals(testGameroom.getId(), createdGameroom.getId());
        assertEquals(testGameroom.getPassword(), createdGameroom.getPassword());
        assertEquals(testGameroom.getRoomname(), createdGameroom.getRoomname());
        assertNull(createdGameroom.getStartedGame());
    }

    @Test
    public void createGameroom_duplicateInputs_throwsException() {
        assertNull(gameroomRepository.findByRoomname("test"));
        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        gameroomService.createGameroom(testGameroom);

        // Create a second gameroom with same roomname
        Gameroom testGameroom2 = new Gameroom();
        testGameroom2.setPassword("123");
        testGameroom2.setRoomname("test");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameroomService.createGameroom(testGameroom2));
    }

    @Test
    public void joinGameroom_validCredentials_success() {
        assertNull(gameroomRepository.findByRoomname("test"));
        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        gameroomService.createGameroom(testGameroom);
        // assert that user list in gameroom is empty
        assertEquals(0, testGameroom.getUsers().size());

        // create a user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        // when
        Gameroom joinedGameroom = gameroomService.joinGameroom(testGameroom, testUser);

        // then
        assertEquals(testGameroom.getId(), joinedGameroom.getId());
        assertEquals(testGameroom.getPassword(), joinedGameroom.getPassword());
        assertEquals(testGameroom.getRoomname(), joinedGameroom.getRoomname());
        assertEquals(1, joinedGameroom.getUsers().size());
    }

    @Test
    public void joinGameroom_invalidCredentials_throwsException() {
        assertNull(gameroomRepository.findByRoomname("test"));
        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        gameroomService.createGameroom(testGameroom);

        // create a user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        // Create a gameroom with wrong credentials
        Gameroom wrongGameroom = new Gameroom();
        wrongGameroom.setPassword("wrong");
        wrongGameroom.setRoomname("test");
        wrongGameroom.setId(testGameroom.getId());

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameroomService.joinGameroom(wrongGameroom, testUser));

    }

    @Test
    public void joinGameroom_gameroomNotExist_throwsException() {
        assertNull(gameroomRepository.findById(3L).orElse(null));

        // Create a gameroomm but don't save it
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        testGameroom.setId(3L);

        // create a user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameroomService.joinGameroom(testGameroom, testUser));

    }

    @Test
    public void joinGameroom_userAlreadyJoined_throwsException() {
        assertNull(gameroomRepository.findByRoomname("test"));
        // create a user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        gameroomService.createGameroom(testGameroom);

        // add user to gameroom
        Gameroom joined = gameroomService.joinGameroom(testGameroom, testUser);
        List<User> users = joined.getUsers();

        // check that an error is thrown is same user is tried to add
        assertThrows(ResponseStatusException.class, () -> gameroomService.joinGameroom(testGameroom, testUser));

    }

    @Test
    public void addGame_success(){
        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        gameroomService.createGameroom(testGameroom);

        // create a game
        Game testGame = gameService.createGame(testGameroom);

        //try to add game to gameroom
        gameroomService.addGame(testGameroom, testGame);

        assertEquals(testGame, testGameroom.getGame());

    }

    @Test
    public void getGamerooms_success(){
        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        gameroomService.createGameroom(testGameroom);

        //try to get all gamerooms
        List<Gameroom> gamerooms = gameroomService.getGamerooms();

        //assert created gameroom is returned in list
        assertEquals(testGameroom.getId(), gamerooms.get(0).getId());
    }

    @Test
    public void getGameroomById_success(){
        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        gameroomService.createGameroom(testGameroom);

        //try to get created gameroom
        Gameroom gameroom = gameroomService.getGameroomById(testGameroom.getId());

        //assert created gameroom is returned
        assertEquals(testGameroom.getId(), gameroom.getId());
    }

    @Test
    public void getGameroomById_exception(){
        // assert that gameroom with this id does not exist
        assertNull(gameroomRepository.findById(2L).orElse(null));

        // assert that exception thrown when trying to get gameroom that does not exist
        assertThrows(ResponseStatusException.class, () -> gameroomService.getGameroomById(2L));
    }

    @Test
    public void getGameroomByGame_success(){

        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        gameroomService.createGameroom(testGameroom);

        Game createdGame = gameService.createGame(testGameroom);
        gameroomService.addGame(testGameroom, createdGame);

        //try to get created gameroom
        Gameroom gameroom = gameroomService.getGameroomByGameId(createdGame.getGameId());

        //assert created gameroom is returned
        assertEquals(testGameroom.getId(), gameroom.getId());
    }

    @Test
    public void getGameroomByGame_exception(){
        // assert that game with this id does not exist
        assertNull(gameRepository.findById(2L).orElse(null));

        // assert that exception thrown when trying to get gameroom with gameid that does not exist
        assertThrows(ResponseStatusException.class, () -> gameroomService.getGameroomByGameId(2L));
    }

    @Test
    public void endGame_success(){
        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        testGameroom = gameroomService.createGameroom(testGameroom);

        // add game to gameroom
        Game createdGame = gameService.createGame(testGameroom);
        gameroomService.addGame(testGameroom, createdGame);

        //try to end game
        Gameroom gameroom = gameroomService.endGame(testGameroom.getId());

        //assert game from gameroom is deleted
        assertNull(gameroom.getGame());
    }

    @Test
    public void leaveGameroom_lastUser(){
        //TODO
    }

    @Test
    public void leaveGameroom_NotlastUser(){
        //TODO
    }

    @Test
    public void leaveGameroom_exception(){
        //TODO
    }

}
