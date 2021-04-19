package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Picture;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.PictureRepository;
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


    //returns setNr (int) for assigned Materialset (per player)
    //TODO: test once initial game setup is done
    @GetMapping("/game/set/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Integer getAssignedMaterialset(@RequestBody GamePostDTO gamePostDTO, @PathVariable Long userId){
        // convert API game to internal representation
        Game game = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);

        //call game service methods
        User user = gameService.assignMaterialset(game,userId);

        return user.getMaterialSet().getSetNr();
    }

    //returns coordinatesAssignedPicture for all players
    //TODO: fix & test once initial game setup is done
    @GetMapping("/game/picture/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getAssignedPicture(@RequestBody GamePostDTO gamePostDTO, @PathVariable Long userId){
        // convert API game to internal representation
        Game game = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);
        //call game service methods
        String assignedCoordinates = gameService.assignPicture(game,userId);
        return assignedCoordinates;
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

        Game game = gameService.createGame(gameroom);
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


   /* @GetMapping("/game/round/{roundNr}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<Long,String> getSubmittedPictures(@PathVariable Long roomId){
        //get 16 pictures via api call to pixabay
        Map<Long,String> submittedPictures = gameService.getSubmittedPictures();

        return submittedPictures;
    }*/


}
