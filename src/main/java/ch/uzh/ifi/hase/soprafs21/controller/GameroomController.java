package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameroomGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameroomPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.GameroomService;
import ch.uzh.ifi.hase.soprafs21.service.ScoreboardService;
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
    private final GameService gameService;
    private final ScoreboardService scoreboardService;

    GameroomController(GameroomService gameroomService, UserService userService, GameService gameService,
                       ScoreboardService scoreboardService) {
        this.gameroomService = gameroomService;
        this.userService = userService;
        this.gameService = gameService;
        this.scoreboardService = scoreboardService;
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

    @GetMapping("/gamerooms/overview/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameroomGetDTO getGameroom(@PathVariable("roomId") Long roomId) {
        Gameroom gameroom = gameroomService.getGameroomById(roomId);
        GameroomGetDTO foundGameroom = DTOMapper.INSTANCE.convertEntityToGameroomGetDTO(gameroom);

        return foundGameroom;
    }

    @PutMapping("/gamerooms/overview/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Long putGameroom(@PathVariable("roomId") Long roomId) {
        Gameroom gameroom = gameroomService.getGameroomById(roomId);
        // create and initialize new game
        Game newGame = gameService.createGame(gameroom);
        // create scoreboard
        Scoreboard scoreboard = scoreboardService.createScoreboard(newGame);
        // add game to gameroom
        gameroom = gameroomService.addGame(gameroom, newGame);

        return newGame.getGameId();
    }

    @GetMapping("/gamerooms/list")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameroomGetDTO> getGameroomList() {
        // fetch all gamerooms in the internal representation
        List<Gameroom> gamerooms = gameroomService.getGamerooms();
        List<GameroomGetDTO> gameroomGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Gameroom gameroom : gamerooms) {
            gameroomGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameroomGetDTO(gameroom));
        }
        return gameroomGetDTOs;
    }

    @PutMapping("/gamerooms/list/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void joinGameroom(@PathVariable("roomId") Long roomId, @RequestBody GameroomPostDTO gameroomPostDTO) {
        // get user that created the gameroom
        gameroomPostDTO.setRoomId(roomId);
        Long userId = gameroomPostDTO.getUserId();
        User user = userService.getExistingUser(userId);

        // convert API gameroom to internal representation
        Gameroom gameroomInput = DTOMapper.INSTANCE.convertGameroomPostDTOtoEntity(gameroomPostDTO);
        Gameroom updatedGameroom = gameroomService.joinGameroom(gameroomInput, user);
    }

    @PostMapping("/gamerooms/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void getWinner(@PathVariable("roomId") Long roomId) {
        Gameroom gameroom = gameroomService.getGameroomById(roomId);
        //TODO: get winner
    }
}
