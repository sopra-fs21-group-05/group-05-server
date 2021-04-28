package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameroomRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("gameroomRepository")
    @Autowired
    private GameroomRepository gameroomRepository;

    @Test
    public void findByRoomname_success() {
        // given
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setPassword("123");

        entityManager.persist(gameroom);
        entityManager.flush();

        // when
        Gameroom found = gameroomRepository.findByRoomname(gameroom.getRoomname());

        //then
        assertEquals(found.getRoomname(), gameroom.getRoomname());
        assertEquals(found.getPassword(), gameroom.getPassword());
    }

    @Test
    public void findById_success() {
        // given
        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setPassword("123");

        entityManager.persist(gameroom);
        entityManager.flush();

        // when
        Gameroom found = gameroomRepository.findById(gameroom.getId()).orElse(null);

        //then
        assertEquals(found.getRoomname(), gameroom.getRoomname());
        assertEquals(found.getPassword(), gameroom.getPassword());

    }
}
