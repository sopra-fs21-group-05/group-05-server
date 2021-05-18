
package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GridCoordinates;
import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.PictureRepository;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PictureRepository pictureRepository;

    @Mock
    private GameroomService gameroomService;

    @InjectMocks
    private GameService gameService;

    @Mock
    private ScoreboardService scoreboardService;

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
        // given
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
        // given
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
    void getExistingGame_returnsGameIfGameExists() {
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);
        createdGame.setRoundNr(1);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);

        Game existingGame = gameService.getExistingGame(createdGame.getGameId());
        assertNotNull(existingGame);
        assertEquals(createdGame.getGameId(), existingGame.getGameId());
        assertEquals(createdGame.getRoundNr(),existingGame.getRoundNr());
    }

    @Test
    void getExistingGame_ThrowsExceptionForNonExistentGame() {
        assertThrows(ResponseStatusException.class, () -> gameService.getExistingGame(1L));
    }

    @Test
    void getPlayerInGame_returnsUserIfPlayerInGame() {
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);
        createdGame.setRoundNr(1);

        User user = new User();
        user.setToken("token");
        user.setId(3L);

        User user2 = new User();
        user2.setToken("token");
        user2.setId(4L);

        List<User> players = new ArrayList<>();
        players.add(user);
        players.add(user2);

        createdGame.setUserList(players);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);

        User existingPlayer = gameService.getPlayerInGame(user2.getId(),createdGame.getGameId());
        assertNotNull(existingPlayer);
        assertEquals(user2.getId(), existingPlayer.getId());
        assertEquals(user2.getToken(),existingPlayer.getToken());
    }


    @Test
    void getPlayerInGame_ThrowsExceptionIfPlayerNotInGame() {
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);
        createdGame.setRoundNr(1);

        User user = new User();
        user.setToken("token");
        user.setId(3L);

        User user2 = new User();
        user2.setToken("token");
        user2.setId(4L);

        List<User> players = new ArrayList<>();
        players.add(user);
        players.add(user2);

        createdGame.setUserList(players);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);

        assertThrows(ResponseStatusException.class, () -> gameService.getPlayerInGame(5L,createdGame.getGameId()));
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
    }

    @Test
    void getPicturesFromPixabay_returnsListOfEncodedStrings(){
        List<String> pictures = gameService.getPicturesFromPixabay();
        assertNotNull(pictures);
        assertEquals(pictures.size(),16);
        assertEquals(pictures.get(0).getClass(), String.class);
    }

    @Test
    void getGameroomById_returnsGameroom(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        //mock the gameroom service method
        when(gameroomService.getGameroomById(Mockito.any())).thenReturn(gameroom);

        Gameroom retrievedGameroom = gameService.getGameroomById(gameroom.getId());
        assertNotNull(retrievedGameroom);
        assertEquals(retrievedGameroom.getId(),gameroom.getId());
        assertEquals(retrievedGameroom.getRoomname(),gameroom.getRoomname());
    }


    @Test
    void submitPicture_returnsMapOfSubmittedPictures() {
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

        String pictureAsString = "recreatedPicture";

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);


        Map<Long,String> submittedPictures = gameService.submitPicture(createdGame,pictureAsString,user.getId());
        assertFalse(submittedPictures.isEmpty());
        assertEquals(submittedPictures.size(),1);
        assertTrue(submittedPictures.containsKey(user.getId()));
    }

    @Test
    void makePictureList_returnsPictureList(){
        List<Picture> pictureList = gameService.makePictureList();
        assertFalse(pictureList.isEmpty());
        assertEquals(pictureList.size(),16);
    }

    @Test
    void createPicture_returnsPicture(){
        String pictureAsString = "pictureString";
        Picture picture = gameService.createPicture(pictureAsString);
        assertEquals(picture.getClass(), Picture.class);
    }

    @Test
    void getSubmittedPictures_returnsMapOfSubmissions(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        User user1 = new User();
        user1.setId(1L);
        user1.setRecreatedPicture("recreation1");

        List<User> players = new ArrayList<>();
        players.add(user1);

        createdGame.setUserList(players);


        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);

        Map<Long,String> submissions = gameService.getSubmittedPictures(createdGame.getGameId());
        assertFalse(submissions.isEmpty());
        assertTrue(submissions.containsKey(user1.getId()));
        assertEquals(submissions.size(),1);
    }

    @Test
    void submitAndCheckGuesses_updatesScores(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        User user1 = new User();
        user1.setId(1L);
        user1.setCoordinatesAssignedPicture(GridCoordinates.A1);

        User user2 = new User();
        user2.setId(2L);


        List<User> players = new ArrayList<>();
        players.add(user1);
        players.add(user2);

        createdGame.setUserList(players);

        Map<Long,String> guesses = new HashMap<>();
        guesses.put(1L,"A1");

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);

        gameService.submitAndCheckGuesses(createdGame.getGameId(), user2.getId(), guesses);
        assertEquals(user1.getPoints(), 1);
        assertEquals(user2.getPoints(), 1);
    }

    @Test
    void submitAndCheckGuesses_invalidInput_throwsException(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        User user1 = new User();
        user1.setId(1L);
        user1.setCoordinatesAssignedPicture(GridCoordinates.A1);

        User user2 = new User();
        user2.setId(2L);


        List<User> players = new ArrayList<>();
        players.add(user1);
        players.add(user2);

        createdGame.setUserList(players);

        Map<Long,String> guesses = new HashMap<>();
        guesses.put(1L,"Q2");

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);

        assertThrows(ResponseStatusException.class, () -> gameService.submitAndCheckGuesses(createdGame.getGameId(), user2.getId(), guesses));
    }

    @Test
    void updateGame_returnsUpdatedGame(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        User user1 = new User();
        user1.setId(1L);
        user1.setRecreatedPicture("recreation1");


        List<User> players = new ArrayList<>();
        players.add(user1);

        createdGame.setUserList(players);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);

        Game updatedGame = gameService.updateGame(createdGame.getGameId());
        assertEquals(updatedGame.getRoundNr(), 2);
        assertEquals(user1.getRecreatedPicture(),"");
    }

    @Test
    void getPictureGrid_returnsMapOfGridPictures(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        Picture picture1 = new Picture();
        picture1.setPictureId(1L);
        Picture picture2 = new Picture();
        picture2.setPictureId(3L);
        Picture picture3 = new Picture();
        picture3.setPictureId(4L);
        Picture picture4 = new Picture();
        picture4.setPictureId(5L);


        List<Picture> gridPictures = new ArrayList<>();
        gridPictures.add(picture1);
        gridPictures.add(picture2);
        gridPictures.add(picture3);
        gridPictures.add(picture4);
        gridPictures.add(picture1);
        gridPictures.add(picture2);
        gridPictures.add(picture3);
        gridPictures.add(picture4);
        gridPictures.add(picture1);
        gridPictures.add(picture2);
        gridPictures.add(picture3);
        gridPictures.add(picture4);
        gridPictures.add(picture1);
        gridPictures.add(picture2);
        gridPictures.add(picture3);
        gridPictures.add(picture4);

        createdGame.setGridPictures(gridPictures);

        // when -> setup additional mocks for GameRepository
        when(gameRepository.findByGameroom(any())).thenReturn(createdGame);
        when(gameRepository.findById(any())).thenReturn(Optional.of(createdGame));
        when(gameRepository.getOne(any())).thenReturn(createdGame);

        Map<String, String> pictureGrid = gameService.getPictureGrid(createdGame.getGameId());
        assertEquals(pictureGrid.size(), 16);
        assertTrue(pictureGrid.keySet().contains("A3"));
        assertTrue(pictureGrid.keySet().contains("D4"));

    }


    @Test
    void endGame_deletedGame(){
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);

        Game createdGame = gameService.createGame(gameroom);

        List<Picture> pictureList = new ArrayList<>();
        createdGame.setGridPictures(pictureList);

        Scoreboard scoreboard = new Scoreboard();
        createdGame.setScoreboard(scoreboard);

        Long id = createdGame.getGameId();
        
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(createdGame));
        doNothing().when(scoreboardService).endGame(any());
        doNothing().when(pictureRepository).delete(any());



        gameService.endGame(id);
        assertNull(gameRepository.getOne(id));
    }


}

