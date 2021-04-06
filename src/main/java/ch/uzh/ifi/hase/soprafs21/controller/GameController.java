package ch.uzh.ifi.hase.soprafs21.controller;

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



    //TODO: fix DTOMapper
    /*
    @GetMapping("/game/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getPictureAndMaterialset(@RequestBody GameGetDTO gameGetDTO, @PathVariable Long userId){
        // convert API game to internal representation
        Game game = DTOMapper.INSTANCE.convertGameGetDTOToEntity(gameGetDTO);

        //call game service methods
        int setNr = gameService.assignMaterialset(game,userId);
        String coordinatesAssignedPicture = gameService.assignPicture(game, userId);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(setNr, coordinatesAssignedPicture);
    }


     */

    //TODO: submit all recreated pictures or one by one?
    /*
    //save recreated pictures
    @PostMapping("/game/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<String> submitRecreatedPictures(@RequestBody GamePostDTO gamePostDTO, @PathVariable Long userId){
        // convert API game to internal representation
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);
        String submittedPicture = gameService.submitPicture(gameInput,userId);
    }
     */


}
