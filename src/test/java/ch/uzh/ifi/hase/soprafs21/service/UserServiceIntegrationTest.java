package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

    }

    @Test
    public void createUser_validInputs_success() {
        assertNull(userRepository.findByUsername("testUsername"));
        // Create a user
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();
        testUser2.setPassword("password");
        testUser2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    public void loginUser_success() {
        //create user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //login user
        User loggedInUser = userService.loginUser(testUser.getUsername(),testUser.getPassword());

        assertEquals(testUser.getId(), loggedInUser.getId());
        assertNotNull(loggedInUser.getToken());
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());


    }

    @Test
    public void loginUser_nonExistingUser_fail(){
        //make a user, but don't save it to the repository
        User notExisting = new User();
        notExisting.setUsername("noUsername");
        notExisting.setPassword("noPassword");

        String username = notExisting.getUsername();
        String password = notExisting.getPassword();
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(username,password));
    }

    @Test
    void loginUser_falsePassword_fail(){
        //create user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        String username = testUser.getUsername();

        //try to login with wrong password
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(username, "notPassword"));
    }

    @Test
    void logoutUser_success(){
        //create user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //login user
        userService.loginUser(testUser.getUsername(),testUser.getPassword());

        //logout user
        User loggedOut = userService.logoutUser(testUser.getId());

        assertEquals(UserStatus.OFFLINE, loggedOut.getStatus());
        assertNull(loggedOut.getToken());
    }

    @Test
    public void getExistingUser_success() {
        //create user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        User existingUser = userService.getExistingUser(testUser.getId());

        //test if all fields that were set are equal
        assertEquals(testUser.getId(), existingUser.getId());
        assertEquals(testUser.getToken(), existingUser.getToken());

    }

    @Test
    void getAllUser_success(){
        //create user
        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //try to get all users
        List<User> users = userService.getUsers();

        //assert created user is in list
        assertEquals(testUser.getId(), users.get(0).getId());
    }
}
