package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.service.ScoreboardService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    ScoreboardController(ScoreboardService scoreboardService){ this.scoreboardService = scoreboardService; }
}
