package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //get list with all users
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    //create a new user
    public User createUser(User newUser) {
        newUser.setStatus(UserStatus.OFFLINE);
        
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    //throws exception if a username is not unique
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The provided username is already taken, please choose a different username!");
        }
    }

    //login user with given credentials
    public User loginUser(String username, String password){
        //checks if the provided username is in the userRepository
        User userByUsername = userRepository.findByUsername(username);
        String loggedInErrorMessage = "The user %s is already logged in.";

        if(userByUsername==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials!");
        }

        if (!userByUsername.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials!");
        }
        //If user is already logged in, it is not possible to login again
        if (userByUsername.getStatus() == UserStatus.ONLINE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format(loggedInErrorMessage, userByUsername.getUsername()));
        }

        //Setting the user ONLINE & saving the information in the userRepository
        userByUsername.setStatus(UserStatus.ONLINE);
        userByUsername.setToken(UUID.randomUUID().toString());
        userRepository.save(userByUsername);
        userRepository.flush();
        return userByUsername;
    }

    //logout user
    public User logoutUser(Long userId) {
        User user = getExistingUser(userId);
        user.setStatus(UserStatus.OFFLINE);
        user.setToken(null);
        userRepository.save(user);
        userRepository.flush();
        return user;
    }


    //returns user that is saved in the userRepository
    public User getExistingUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        String baseErrorMessage = "The %s provided %s not found.";
        if(optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "userId", "was"));
        }
        return optionalUser.get();
    }

    //restrict a user for the next game
    public void restrictPlayer(User user){
        user.setRestrictedMode(true);
    }

    //check if user is restricted
    public boolean isRestricted(User user){
        return user.getRestrictedMode();
    }

}
