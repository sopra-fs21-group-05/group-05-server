package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserAuthDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
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

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setStatus(UserStatus.OFFLINE);
        
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }

    public User loginUser(String username, String password){
        //checks if the provided username is in the userRepository
        User userByUsername = userRepository.findByUsername(username);

        String baseErrorMessage = "Invalid %s, make sure that username and password are correct.";
        String loggedInErrorMessage = "The user %s is already logged in.";

        if(userByUsername==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format(baseErrorMessage, "username"));
        }

        if (!userByUsername.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format(baseErrorMessage, "password"));
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

    public void restrictPlayer(User user){
        user.setRestrictedMode(true);
    }

    public boolean isRestricted(User user){
        return user.getRestrictedMode();
    }

    /*
    public List<User> getWinner(){
        List<User> winners = new ArrayList<>();
        int max = 0;
        for(User user: getUsers()){
            if(user.getPoints() >= max){
                max = user.getPoints();
            }
        }
        for (User user: getUsers()){
            if(user.getPoints() == max){
                winners.add(user);
                restrictPlayer(user);
            }
        }
        return winners;
    }
     */

}
