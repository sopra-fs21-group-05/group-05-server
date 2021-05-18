package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Picture;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameroomRepository;
import ch.uzh.ifi.hase.soprafs21.repository.PictureRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("pictureRepository")
    @Autowired
    private PictureRepository pictureRepository;

    @Qualifier("gameroomRepository")
    @Autowired
    private GameroomRepository gameroomRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameroomService gameroomService;


    @BeforeEach
    public void setup() {
        gameRepository.deleteAll();
        userRepository.deleteAll();
        gameroomRepository.deleteAll();
        pictureRepository.deleteAll();
    }


    @Test
    public void createGame_validInputs_success() {

        // Create a game
        Game testGame = new Game();
        testGame.setGameId(1L);
        testGame.setRoundNr(1);

        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        assertNull(gameRepository.findByGameroom(gameroom));

        gameroom.setGame(testGame);
        testGame.setGameroom(gameroom);

        // when
        Game createdGame = gameService.createGame(gameroom);

        // then
        assertNotNull(createdGame.getGameId());
        assertEquals(testGame.getRoundNr(), createdGame.getRoundNr());
        assertEquals(testGame.getGameroom(), createdGame.getGameroom());
        assertNotNull(createdGame.getGameId());
    }


    @Test
    public void createGame_duplicateGameroom_throwsException() {
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game testgame = new Game();
        testgame.setGameId(3L);
        testgame.setRoundNr(0);
        testgame.setGameroom(gameroom);

        gameroom.setGame(testgame);
        gameroom.setStartedGame(testgame.getGameId());


        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(gameroom));
    }

    @Test
    public void assignGridPictures_success(){
        // given
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);
        Game createdGame = gameService.createGame(gameroom);

        Picture pic1 = new Picture();
        pictureRepository.save(pic1);
        pictureRepository.flush();

        List<Picture> pictureList = new ArrayList<>();
        pictureList.add(pic1);

        gameService.assignGridPictures(createdGame,pictureList);
        Game found = gameRepository.findById(createdGame.getGameId()).get();
        assertNotNull(found);
        assertNotNull(found.getGridPictures());
    }


    @Test
    public void assignMaterialset_success(){
        // given
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(4L);

        User user = new User();
        user.setId(5L);
        user.setToken("token");
        user.setPassword("pass");
        user.setUsername("user1");
        user.setStatus(UserStatus.ONLINE);
        user.setMaterialSet(MaterialSet.COLOR_CUBES);

        List<User> players = new ArrayList<>();
        players.add(user);
        gameroom.setUsers(players);

        Game createdGame = gameService.createGame(gameroom);
        createdGame.setGameroom(gameroom);
        gameroom.setGame(createdGame);
        gameroom.setStartedGame(createdGame.getGameId());

        assertNotNull(createdGame.getUserList());

        User updatedUser = gameService.assignMaterialset(createdGame.getGameId(), createdGame.getUserList().get(0).getId());
        assertEquals(MaterialSet.BUILDING_BLOCKS,updatedUser.getMaterialSet());

    }


    @Test
    public void assignPicture_success(){
        // given
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("r1");
        gameroom.setPassword("pass");
        gameroom = gameroomService.createGameroom(gameroom);

        User user = new User();
        user.setPassword("pass");
        user.setUsername("user1");
        user = userService.createUser(user);

        List<User> players = new ArrayList<>();
        players.add(user);
        gameroom.setUsers(players);

        List<Picture> gridPictures = new ArrayList<>();
        Picture pic1 = gameService.createPicture("encodedPic");
        Picture pic2 = gameService.createPicture("encodedPic");
        Picture pic3 = gameService.createPicture("encodedPic");
        Picture pic4 = gameService.createPicture("encodedPic");
        Picture pic5 = gameService.createPicture("encodedPic");
        Picture pic6 = gameService.createPicture("encodedPic");
        Picture pic7 = gameService.createPicture("encodedPic");
        Picture pic8 = gameService.createPicture("encodedPic");
        Picture pic9 = gameService.createPicture("encodedPic");
        Picture pic10 = gameService.createPicture("encodedPic");
        Picture pic11 = gameService.createPicture("encodedPic");
        Picture pic12 = gameService.createPicture("encodedPic");
        Picture pic13 = gameService.createPicture("encodedPic");
        Picture pic14 = gameService.createPicture("encodedPic");
        Picture pic15 = gameService.createPicture("encodedPic");
        Picture pic16 = gameService.createPicture("encodedPic");


        gridPictures.add(pic1);
        gridPictures.add(pic2);
        gridPictures.add(pic3);
        gridPictures.add(pic4);
        gridPictures.add(pic5);
        gridPictures.add(pic6);
        gridPictures.add(pic7);
        gridPictures.add(pic8);
        gridPictures.add(pic9);
        gridPictures.add(pic10);
        gridPictures.add(pic11);
        gridPictures.add(pic12);
        gridPictures.add(pic13);
        gridPictures.add(pic14);
        gridPictures.add(pic15);
        gridPictures.add(pic16);

        assertEquals(16, gridPictures.size());

        Game createdGame = gameService.createGame(gameroom);
        System.out.println("gameId createdGame: "+createdGame.getGameId());

        gameService.assignGridPictures(createdGame,gridPictures);

        assertEquals(16, createdGame.getGridPictures().size());
        assertNotNull(createdGame.getUserList());

        Map<String, String> assignedPicture = gameService.assignPicture(createdGame.getGameId(), createdGame.getUserList().get(0).getId());
        assertNotNull(assignedPicture);
        assertEquals(1, assignedPicture.size());
    }

    @Test
    public void getExistingGame_success(){
        Gameroom gameroom1 = new Gameroom();
        gameroom1.setRoomname("room1");
        gameroom1.setPassword("pass");
        Gameroom gameroom = gameroomService.createGameroom(gameroom1);

        Game createdGame = gameService.createGame(gameroom);

        Picture pic1 = gameService.createPicture("encodedPicture");
        List<Picture> pictureList = new ArrayList<>();
        pictureList.add(pic1);

        gameService.assignGridPictures(createdGame,pictureList);

        Game game = gameService.getExistingGame(createdGame.getGameId());
        assertEquals(game.getGameId(), createdGame.getGameId());
        assertEquals(game.getRoundNr(), createdGame.getRoundNr());
        assertEquals(game.getGridPictures().size(), createdGame.getGridPictures().size());
    }
}

