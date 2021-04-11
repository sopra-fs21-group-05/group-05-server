package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }


    //returns setNr (int)
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

    //returns coordinatesAssignedPicture (String)
    //TODO: fix & test once initial game setup is done
    @GetMapping("/game/set/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getAssignedPicture(@RequestBody GamePostDTO gamePostDTO, @PathVariable Long userId){
        // convert API game to internal representation
        Game game = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);
        //call game service methods
        String coordinatesAssignedPicture = gameService.assignPicture(game, userId);
        return coordinatesAssignedPicture;
    }


    //TODO: submit recreated picture for each player and extend the userRecreations in Game
    /*//save recreated pictures
    @PostMapping("/game/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<String> submitRecreatedPicture(@RequestBody GamePostDTO gamePostDTO, @PathVariable Long userId){
        // convert API game to internal representation
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);

        //call gameservice method
        String submittedPicture = gameService.submitPicture(gameInput,userId);
    }*/



}
