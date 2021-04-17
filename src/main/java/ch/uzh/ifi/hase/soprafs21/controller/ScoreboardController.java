package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.ScoreboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    ScoreboardController(ScoreboardService scoreboardService){ this.scoreboardService = scoreboardService; }

    @PostMapping("/scoreboards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void createScoreboard(@RequestBody String s){

    }

    @GetMapping("/scoreboards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void getScoreboard(@RequestBody String s){


    }
}
