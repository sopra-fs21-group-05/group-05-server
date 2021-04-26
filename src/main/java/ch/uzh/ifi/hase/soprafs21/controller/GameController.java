package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Picture;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.PictureRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.AssignedPictureDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class GameController {
    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
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
            System.out.println("Key: " + coordinate + ", Value: " + picture);
        }
        AssignedPictureDTO pic = new AssignedPictureDTO();
        pic.setCoordinate(coordinate);
        pic.setPicture(picture);

        return pic;
    }


    @GetMapping("/winner")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getWinner(@RequestBody GameGetDTO gameGetDTO) {
        List<User> winnerList = gameService.getWinner(gameGetDTO.getGameId());
        List<UserGetDTO> winners = new ArrayList<>();

        for (User user : winnerList) {
            winners.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return winners;

    }

    @GetMapping("/game/setup/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<String> gameSetup(@PathVariable Long roomId){
        //get 16 pictures via api call to pixabay
        Gameroom gameroom = gameService.getGameroomById(roomId);
        List<Picture> pictureList = gameService.makePictureList();

        Game game = gameroom.getGame();
        gameService.assignGridPictures(game,pictureList);
        List<String> pictures = game.getGridPictures();

        return pictures;
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
        Map<Long,String> userRecreations = gameService.submitPicture(gameInput,submittedPicture,userId);


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
        //return userRecreations;
    }

    //get a list of all submitted pictures in the current round
    @GetMapping("/game/recreations/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<String> getSubmittedPictures(@PathVariable("gameId") Long gameId){
        //commit for referencing respective task
        //get list of all submitted pictures
        List<String> submittedPictures = gameService.getSubmittedPictures(gameId);

        return submittedPictures;
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

    @PutMapping("game/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public int updateGame(@PathVariable ("gameId") Long gameId){
        Game game = gameService.updateGame(gameId);
        return game.getRoundNr();
    }





}
