package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserAuthDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;



/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    //gets a list of all users
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    //creates a new user
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<String> createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        //create String of ResponseEntity in order to return it
        String locationAsString = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(String.format("%d", createdUser.getId()))
                .toString();

        URI locationAsUrl = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(String.format("%d", createdUser.getId()))
                .toUri();

        //returns url as a string
        return ResponseEntity.created(locationAsUrl).body(locationAsString);
    }

    //login endpoint
    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserAuthDTO login(@RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User user = userService.loginUser(userInput.getUsername(),userInput.getPassword());
        return DTOMapper.INSTANCE.convertEntityToUserAuthDTO(user);
    }

    //logout endpoint
    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserAuthDTO logout(@RequestBody UserAuthDTO userAuthDTO){
        User userInput = DTOMapper.INSTANCE.convertUserAuthDTOToEntity(userAuthDTO);
        User user= userService.logoutUser(userInput.getId());
        return DTOMapper.INSTANCE.convertEntityToUserAuthDTO(user);
    }

    //checks if a user is restricted
    @GetMapping("/users/{userId}/restricted")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean isRestricted(@PathVariable Long userId){
        User user = userService.getExistingUser(userId);
        return userService.isRestricted(user);
    }

}
