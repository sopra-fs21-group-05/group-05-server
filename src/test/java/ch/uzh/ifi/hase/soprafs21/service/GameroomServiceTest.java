package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameroomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

public class GameroomServiceTest {

    @Mock
    private GameroomRepository gameroomRepository;

    @InjectMocks
    private GameroomService gameroomService;

    private Gameroom testGameroom;
    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("password");
        testUser.setUsername("testUsername");

        testGameroom = new Gameroom();
        testGameroom.setId(2L);
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");


        // when -> any object is being save in the gameroomRepository -> return the dummy testGameroom
        Mockito.when(gameroomRepository.save(Mockito.any())).thenReturn(testGameroom);
    }

    @Test
    public void createGameroom_validInputs_success() {
        // when -> any object is being save in the gameroomRepository -> return the dummy testGameroom
        Gameroom createdGameroom = gameroomService.createGameroom(testGameroom);

        // then
        Mockito.verify(gameroomRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testGameroom.getId(), createdGameroom.getId());
        assertEquals(testGameroom.getPassword(), createdGameroom.getPassword());
        assertEquals(testGameroom.getRoomname(), createdGameroom.getRoomname());
        assertNull(createdGameroom.getStartedGame());
    }

    @Test
    public void createGameroom_duplicateInputs_throwsException() {
        // given -> a first gameroom has already been created
        gameroomService.createGameroom(testGameroom);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.findByRoomname(Mockito.any())).thenReturn(testGameroom);

        // then -> attempt to create second gameroom with same gameroom -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameroomService.createGameroom(testGameroom));
    }

    @Test
    public void joinGameroom_validCredentials_success() {
        // given -> a first gameroom has already been created
        gameroomService.createGameroom(testGameroom);

        List<User> users = Collections.singletonList(testUser);
        Gameroom joinedTestGameroom = new Gameroom();
        joinedTestGameroom.setId(2L);
        joinedTestGameroom.setPassword("123");
        joinedTestGameroom.setRoomname("test");
        joinedTestGameroom.setUsers(users);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testGameroom));
        Mockito.when(gameroomRepository.save(Mockito.any())).thenReturn(joinedTestGameroom);

        Gameroom joinedGameroom = gameroomService.joinGameroom(testGameroom, testUser);

        assertEquals(joinedTestGameroom.getId(), joinedGameroom.getId());
        assertEquals(joinedTestGameroom.getPassword(), joinedGameroom.getPassword());
        assertEquals(joinedTestGameroom.getRoomname(), joinedGameroom.getRoomname());
        assertNull(joinedGameroom.getStartedGame());
        assertEquals(joinedTestGameroom.getUsers(), joinedGameroom.getUsers());
    }

    @Test
    public void joinGameroom_invalidCredentials_throwsException() {
        // gameroom with wrong credentials
        Gameroom wrongGameroom = new Gameroom();
        wrongGameroom.setId(2L);
        wrongGameroom.setPassword("wrong");
        wrongGameroom.setRoomname("test");

        // given -> a first gameroom has already been created
        gameroomService.createGameroom(testGameroom);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testGameroom));

        // then -> attempt to create second gameroom with same gameroom -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameroomService.joinGameroom(wrongGameroom, testUser));
    }

    @Test
    public void joinGameroom_userAlreadyJoined_throwsException() {
        // gameroom where user already joined
        List<User> users = Collections.singletonList(testUser);
        testGameroom.setUsers(users);

        // given -> a first gameroom has already been created
        gameroomService.createGameroom(testGameroom);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testGameroom));

        // then -> attempt to create second gameroom with same gameroom -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> gameroomService.joinGameroom(testGameroom, testUser));
    }

    @Test
    public void checkCreator_creatorExists() throws Exception {
        User user = new User();
        user.setId(1L);
        List<User> users = Collections.singletonList(user);

        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setPassword("123");
        gameroom.setId(2L);
        gameroom.setUsers(users);
        gameroom.setCreator(1L);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.save(Mockito.any())).thenReturn(gameroom);

        Gameroom checkedGameroom = gameroomService.checkCreator(gameroom);

        assertEquals(gameroom.getCreator(), checkedGameroom.getCreator());
    }

    @Test
    public void checkCreator_creatorNotExists() throws Exception {
        User user = new User();
        user.setId(1L);
        List<User> users = Collections.singletonList(user);

        Gameroom gameroom = new Gameroom();
        gameroom.setRoomname("test");
        gameroom.setPassword("123");
        gameroom.setId(2L);
        gameroom.setUsers(users);
        gameroom.setCreator(3L);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.save(Mockito.any())).thenReturn(gameroom);

        Gameroom checkedGameroom = gameroomService.checkCreator(gameroom);

        assertEquals(1L, checkedGameroom.getCreator());
    }

    @Test
    public void storeWinner_success(){
        User user = new User();
        user.setId(1L);
        List<User> users = Collections.singletonList(user);

        // Create a gameroom
        Gameroom testGameroom = new Gameroom();
        testGameroom.setPassword("123");
        testGameroom.setRoomname("test");
        testGameroom.setUsers(users);
        testGameroom = gameroomService.createGameroom(testGameroom);

        Mockito.when(gameroomRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testGameroom));

        assert testGameroom != null;
        Gameroom gameroom = gameroomService.storeWinner(testGameroom.getId(), users);

        assertEquals(1, gameroom.getLastWinner().size());
    }

    @Test
    public void leaveGameroom_lastUser(){
        // given -> a first gameroom has already been created
        gameroomService.createGameroom(testGameroom);

        List<User> users = new ArrayList<>();
        users.add(testUser);
        Gameroom joinedTestGameroom = new Gameroom();
        joinedTestGameroom.setId(2L);
        joinedTestGameroom.setPassword("123");
        joinedTestGameroom.setRoomname("test");
        joinedTestGameroom.setUsers(users);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(joinedTestGameroom));
        Mockito.when(gameroomRepository.save(Mockito.any())).thenReturn(joinedTestGameroom);
        doNothing().when(gameroomRepository).delete(any());
        doNothing().when(gameroomRepository).flush();

        Gameroom leftGameroom = gameroomService.leaveGameroom(joinedTestGameroom.getId(), testUser);

        assertNull(leftGameroom);
    }

    @Test
    public void leaveGameroom_NotlastUser(){
        // given -> a first gameroom has already been created
        gameroomService.createGameroom(testGameroom);

        User testUser1 = new User();
        testUser1.setId(3L);
        testUser1.setPassword("password");
        testUser1.setUsername("username");

        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(testUser1);
        Gameroom joinedTestGameroom = new Gameroom();
        joinedTestGameroom.setId(2L);
        joinedTestGameroom.setPassword("123");
        joinedTestGameroom.setRoomname("test");
        joinedTestGameroom.setUsers(users);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(joinedTestGameroom));
        Mockito.when(gameroomRepository.save(Mockito.any())).thenReturn(joinedTestGameroom);
        doNothing().when(gameroomRepository).delete(any());
        doNothing().when(gameroomRepository).flush();

        // assert 2 users are in gameroom
        assertEquals(2,joinedTestGameroom.getUsers().size());

        //one of the users leaves, one should be left in gameroom
        Gameroom leftGameroom = gameroomService.leaveGameroom(joinedTestGameroom.getId(), testUser);

        assertEquals(1, leftGameroom.getUsers().size());
    }

    @Test
    public void leaveGameroom_exception(){
        // given -> a first gameroom has already been created
        gameroomService.createGameroom(testGameroom);

        User testUser1 = new User();
        testUser1.setId(3L);
        testUser1.setPassword("password");
        testUser1.setUsername("username");

        List<User> users = new ArrayList<>();
        users.add(testUser);

        Gameroom joinedTestGameroom = new Gameroom();
        joinedTestGameroom.setId(2L);
        joinedTestGameroom.setPassword("123");
        joinedTestGameroom.setRoomname("test");
        joinedTestGameroom.setUsers(users);

        // when -> setup additional mocks for GameroomRepository
        Mockito.when(gameroomRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(joinedTestGameroom));
        Mockito.when(gameroomRepository.save(Mockito.any())).thenReturn(joinedTestGameroom);
        doNothing().when(gameroomRepository).delete(any());
        doNothing().when(gameroomRepository).flush();

        // assert testUser1 not in gameroom
        assertFalse(joinedTestGameroom.getUsers().contains(testUser1));

        // user tries to leave that isn't in gameroom leaves, one should be left in gameroom
        assertThrows(ResponseStatusException.class, () -> gameroomService.leaveGameroom(joinedTestGameroom.getId(), testUser1));
    }

}

