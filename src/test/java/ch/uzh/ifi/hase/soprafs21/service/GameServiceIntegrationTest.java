
package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;


    @Autowired
    private GameService gameService;


    @BeforeEach
    public void setup() {
        gameRepository.deleteAll();
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

}

