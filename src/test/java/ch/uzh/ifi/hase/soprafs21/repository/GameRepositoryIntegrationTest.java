package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;


    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("gameroomRepository")
    @Autowired
    private GameroomRepository gameroomRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(gameRepository).isNotNull();
        assertThat(gameroomRepository).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(userRepository).isNotNull();
    }


    //TODO: check if findByGameroom is used anywhere (except in tests)
    /*@Test
    public void findByGameroom_success(){
        // given
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("room1");
        gameroom.setPassword("pass");

        gameroomRepository.save(gameroom);
        gameroomRepository.flush();



        Game game = new Game();
        game.setRoundNr(2);
        game.setGameroom(gameroom);
        gameroom.setGame(game);


        gameRepository.save(game);
        gameRepository.flush();


        // when
        Game found = gameRepository.findByGameroom(gameroom);

        // then
        assertNotNull(found.getGameId());
        assertEquals(found.getRoundNr(), game.getRoundNr());
    }
*/

    @Test
    public void getOne_success() {
        // given
        Game game = new Game();
        game.setRoundNr(2);

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found = gameRepository.getOne(game.getGameId());

        // then
        assertEquals(found.getRoundNr(), game.getRoundNr());
        assertNotNull(found.getGameId());

    }


}
