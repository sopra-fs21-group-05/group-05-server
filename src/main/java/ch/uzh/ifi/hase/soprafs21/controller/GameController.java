package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Picture;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.GameroomService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to the game.
 * The controller will receive the request and delegate the execution to the GameService and finally return the result.
 */
@RestController
public class GameController {
    private final GameService gameService;
    private final GameroomService gameroomService;
    private final UserService userService;

    GameController(GameService gameService, GameroomService gameroomService, UserService userService) {
        this.gameService = gameService;
        this.gameroomService = gameroomService;
        this.userService = userService;
    }


    //returns setNr (int) for assigned Materialset per player
    @GetMapping("/game/{gameId}/{userId}/set")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Integer getAssignedMaterialset(@PathVariable ("userId")Long userId, @PathVariable ("gameId") Long gameId){

        //call game service methods
        User user = gameService.assignMaterialset(gameId,userId);

        return user.getMaterialSet().getSetNr();
    }

    //returns gridCoordinates and assigned picture as Base 64 encoded string per player
    @GetMapping("/game/{gameId}/{userId}/picture")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AssignedPictureDTO getAssignedPicture(@PathVariable ("userId")Long userId, @PathVariable ("gameId") Long gameId){
        //call game service methods
        Map<String,String> assignedCoordinates = gameService.assignPicture(gameId,userId);
        //get coordinate and picture
        String coordinate = "";
        String picture = "";
        for (Map.Entry<String, String> entry : assignedCoordinates.entrySet()) {
            coordinate = entry.getKey();
            picture = entry.getValue();
        }
        AssignedPictureDTO pic = new AssignedPictureDTO();
        pic.setCoordinate(coordinate);
        pic.setPicture(picture);

        return pic;
    }

    // get list of winners
    @GetMapping("/{gameId}/winner")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getWinner(@PathVariable Long gameId) {
        List<User> winnerList = gameService.getWinner(gameId);

        //store the last winner in the gameroom
        Gameroom gameroom = gameroomService.getGameroomByStartedGame(gameId);
        gameroomService.storeWinner(gameroom.getId(), winnerList);

        //convert winner list
        List<UserGetDTO> winners = new ArrayList<>();
        for (User user : winnerList) {
            winners.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return winners;

    }
    //get the 16 grid pictures
    @GetMapping("/game/setup/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<String> gameSetup(@PathVariable Long roomId){
        Gameroom gameroom = gameService.getGameroomById(roomId);
        //get 16 pictures
        List<Picture> pictureList = gameService.makePictureList();

        Game game = gameroom.getGame();
        gameService.assignGridPictures(game,pictureList);

        return game.getGridPicturesAsString();
    }


    //submit recreated picture for each player and extend the userRecreations in Game
    @PostMapping("/game/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<String> submitRecreatedPicture(@RequestBody GamePostDTO gamePostDTO, @PathVariable Long userId){
        String submittedPicture = gamePostDTO.getSubmittedPicture();
        // convert API game to internal representation
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);

        //call gameservice method
        gameService.submitPicture(gameInput,submittedPicture,userId);


        String locationAsString = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toString();


        URI locationAsUrl = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        //returns url as a string
        return ResponseEntity.created(locationAsUrl).body(locationAsString);
    }

    //get a list of all submitted pictures in the current round
    @GetMapping("/game/recreations/overview/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RecreationGetDTO getSubmittedPictures(@PathVariable("gameId") Long gameId){

        //get list of all submitted pictures
        Map<Long,String> submittedPictures = gameService.getSubmittedPictures(gameId);

        //get map of usernames
        Map<Long, String> userNames = new HashMap<>();
        for (Long id: submittedPictures.keySet()) {
            String name = userService.getExistingUser(id).getUsername();
            userNames.put(id,name);
        }

        RecreationGetDTO recreations = new RecreationGetDTO();
        recreations.setRecreations(submittedPictures);
        recreations.setUserNames(userNames);

        return recreations;
    }

    //submit guesses endpoint
    @PostMapping("/game/round/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<String> submitAndCheckGuesses(@RequestBody GamePostDTO gamePostDTO, @PathVariable ("userId") Long userId){

        Game currentGame = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);

        //get list of all submitted pictures
        gameService.submitAndCheckGuesses(currentGame.getGameId(), userId, gamePostDTO.getGuesses());

        String locationAsString = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toString();

        URI locationAsUrl = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        //returns url as a string
        return ResponseEntity.created(locationAsUrl).body(locationAsString);
    }

    //starts new game round
    @PutMapping("game/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public int updateGame(@PathVariable ("gameId") Long gameId){
        Game game = gameService.updateGame(gameId);
        return game.getRoundNr();
    }

    //returns the picture grid (coordinates with respective picture) as a hashmap
    @GetMapping("game/grid/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String,String> getPicturegrid(@PathVariable ("gameId") Long gameId){
        return gameService.getPictureGrid(gameId);
    }
    // get the current round nr
    @GetMapping("game/round/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public int getRoundNr(@PathVariable ("gameId") Long gameId){
        Game game = gameService.getExistingGame(gameId);
        return game.getRoundNr();
    }

    // get if all players submitted their guesses
    @GetMapping("game/guesses/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean getAllGuessesSubmitted(@PathVariable ("gameId") Long gameId){
        boolean allGuessed = false;
        Game game = gameService.getExistingGame(gameId);
        int noGuesses = game.getSubmittedGuesses();
        int noPlayers = game.getUserList().size();
        if(noGuesses == noPlayers){
            allGuessed = true;
        }
        return allGuessed;
    }


}
