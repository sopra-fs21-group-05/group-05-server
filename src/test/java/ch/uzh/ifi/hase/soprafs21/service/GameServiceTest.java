package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given

        testGame = new Game();
        testGame.setGameId(1L);
        testGame.setRoundNr(0);


        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
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
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());

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
        Mockito.when(gameService.createGame(gameroom)).thenThrow(ResponseStatusException.class);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(gameroom));
    }


    @Test void assignGridPictures_success(){
        // given -> a first user has already been created
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");
        gameroom.setId(2L);
        Game createdGame = gameService.createGame(gameroom);

        // when -> setup additional mocks for GameRepository
        Mockito.when(gameRepository.findByGameroom(Mockito.any())).thenReturn(testGame);

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

}
