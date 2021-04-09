package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameroomGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameroomIdGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameroomPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameroomService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Gameroom Controller
 * This class is responsible for handling all REST request that are related to the gameroom.
 * The controller will receive the request and delegate the execution to the GameroomService and finally return the result.
 */
@RestController
public class GameroomController {

    private final GameroomService gameroomService;
    private final UserService userService;

    GameroomController(GameroomService gameroomService, UserService userService) {
        this.gameroomService = gameroomService;
        this.userService = userService;
    }

    @PostMapping("/gamerooms")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<String> createGameroom(@RequestBody GameroomPostDTO gameroomPostDTO) {
        // get user that created the gameroom
        Long userId = gameroomPostDTO.getUserId();
        User user = userService.getExistingUser(userId);

        // convert API gameroom to internal representation
        Gameroom gameroomInput = DTOMapper.INSTANCE.convertGameroomPostDTOtoEntity(gameroomPostDTO);

        //create gameroom
        Gameroom createdGameroom = gameroomService.createGameroom(gameroomInput);

        //add user that created the gameroom to that room
        Gameroom joinedGameroom = gameroomService.joinGameroom(createdGameroom, user);

        //create String of ResponseEntity in order to return it
        String locationAsString = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(String.format("%d", joinedGameroom.getId()))
                .toString();

        URI locationAsUrl = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(String.format("%d", joinedGameroom.getId()))
                .toUri();

        //returns url as a string
        return ResponseEntity.created(locationAsUrl).body(locationAsString);
    }

    @GetMapping("/gamerooms/{roomname}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameroomIdGetDTO getGameroomId(@PathVariable("roomname") String roomname) {
        Gameroom gameroom = gameroomService.getGameroomByName(roomname);
        GameroomIdGetDTO foundGameroom = DTOMapper.INSTANCE.convertEntityToGameroomIdGetDTO(gameroom);
        return foundGameroom;
    }

    /*@GetMapping("/gamerooms/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameroomGetDTO getGameroom(@PathVariable("roomId") Long roomId) {
        Gameroom gameroom = gameroomService.getGameroomById(roomId);
        GameroomGetDTO foundGameroom = DTOMapper.INSTANCE.convertEntityToGameroomGetDTO(gameroom);
        return foundGameroom;
    }*/

    @PutMapping("/gamerooms/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void getGameroom(@PathVariable("roomId") Long roomId) {
        Gameroom gameroom = gameroomService.getGameroomById(roomId);
        //TODO: start the game
    }
}
