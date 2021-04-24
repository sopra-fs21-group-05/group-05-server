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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class ScoreboardController {
    private final ScoreboardService scoreboardService;
    private final GameService gameService;

    ScoreboardController(ScoreboardService scoreboardService, GameService gameService){
        this.scoreboardService = scoreboardService;
        this.gameService = gameService;
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
