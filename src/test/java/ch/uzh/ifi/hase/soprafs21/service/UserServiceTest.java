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

import java.util.ArrayList;
import java.util.List;
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

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser.getUsername(), "notPassword"));
    }

    @Test
    void loginUser_alreadyLoggedIn_throwsException(){
        // given -> a first user has already been created and has already logged in
        userService.createUser(testUser);
        testUser.setStatus(UserStatus.ONLINE);

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser.getUsername(), testUser.getPassword()));
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
    void getWinner_success(){
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

        Mockito.when(userService.getUsers()).thenReturn(users);

        //call to the function
        List <User> winners = userService.getWinner();
        // mocking the expected response
        List <User> expectedWinner = new ArrayList<>();
        expectedWinner.add(user2);

        assertEquals(expectedWinner, winners);
        assertFalse(user1.getRestrictedMode());
        assertTrue(user2.getRestrictedMode());
    }

    @Test
    void getWinner_two_winners(){
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

        Mockito.when(userService.getUsers()).thenReturn(users);

        //call to the function
        List <User> winners = userService.getWinner();
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
