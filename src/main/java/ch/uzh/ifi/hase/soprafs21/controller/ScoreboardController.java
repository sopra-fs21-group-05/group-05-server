package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameroomGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.ScoreboardGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.ScoreboardService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ScoreboardController {
    private final ScoreboardService scoreboardService;
    private final GameService gameService;
    private final UserService userService;

    ScoreboardController(ScoreboardService scoreboardService, GameService gameService, UserService userService){
        this.scoreboardService = scoreboardService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/scoreboards/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ScoreboardGetDTO getScoreboard(@PathVariable("gameId") Long gameId){
        // get Game
        Game game = gameService.getExistingGame(gameId);
        //get scoreboard
        Scoreboard scoreboard = scoreboardService.findScoreboardByGame(game);
        ScoreboardGetDTO foundScoreboard = DTOMapper.INSTANCE.convertEntityToScoreboardGetDTO(scoreboard);
        //add map with usernames
        Map<Long, String> userNames = new HashMap<>();
        for (Long id: foundScoreboard.getUserPoints().keySet()) {
            String name = userService.getExistingUser(id).getUsername();
            userNames.put(id,name);
        }
        foundScoreboard.setUserNames(userNames);

        return foundScoreboard;

    }

    @PostMapping("/scoreboards/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updateScoreboard(@PathVariable("gameId") Long gameId){
        Game game = gameService.getExistingGame(gameId);
        Scoreboard scoreboard = game.getScoreboard();
        List<User> userList = game.getUserList();

        scoreboardService.updateScoreboard(userList,scoreboard);
    }
}
