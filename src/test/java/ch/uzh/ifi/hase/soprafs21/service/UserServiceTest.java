package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("password");
        testUser.setUsername("testUsername");

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }
    

    @Test
    public void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test void loginUser_success(){
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        User targetUser = userService.loginUser(testUser.getUsername(),testUser.getPassword());
        assertEquals(testUser, targetUser);
        assertEquals(UserStatus.ONLINE, targetUser.getStatus());

    }

    @Test void loginUser_nonExistingUser_throwsException(){
        // given -> a first user has already been created
        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.loginUser("anything", "somePassword"));
    }

    @Test void loginUser_falsePassword_throwsException(){
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        String username = testUser.getUsername();

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(username, "notPassword"));
    }

    @Test
    void loginUser_alreadyLoggedIn_throwsException(){
        // given -> a first user has already been created and has already logged in
        userService.createUser(testUser);
        testUser.setStatus(UserStatus.ONLINE);

        String username = testUser.getUsername();
        String password = testUser.getPassword();

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(username, password));
    }

    @Test
    void logoutUser_success(){
        // given -> a first user has already been created and has already logged in
        userService.createUser(testUser);
        testUser.setStatus(UserStatus.ONLINE);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));

        userService.logoutUser(testUser.getId());

        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
    }

    @Test
    void logoutUser_nonExistingId_throwsException(){
        // given -> a first user has already been created and has already logged in
        userService.createUser(testUser);
        testUser.setStatus(UserStatus.ONLINE);

        assertThrows(ResponseStatusException.class, () -> userService.logoutUser(2L));

    }

    @Test
    void restrictPlayer(){
        // given -> a first user has already been created and has already logged in
        userService.createUser(testUser);
        // restrict player
        userService.restrictPlayer(testUser);

        //check if player is restricted
        assertTrue(testUser.getRestrictedMode());
    }

    @Test
    void isRestricted(){
        // given -> a first user has already been created and has already logged in
        userService.createUser(testUser);
        testUser.setRestrictedMode(false);

        assertFalse(userService.isRestricted(testUser));
    }


}
