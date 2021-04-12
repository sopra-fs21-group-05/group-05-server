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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }


 /*   //returns setNr (int)
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
    }*/

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


    //TODO: submit recreated picture for each player and extend the userRecreations in Game
    /*//save recreated pictures
    @PostMapping("/game/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<String> submitRecreatedPicture(@RequestBody GamePostDTO gamePostDTO, @PathVariable Long userId){
        // convert API game to internal representation
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);

        //call gameservice method
        User user = gameService.submitPicture(gameInput,userId);

        //create String of ResponseEntity in order to return it
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
    }*/



}
