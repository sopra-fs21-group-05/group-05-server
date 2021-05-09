package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.ScoreboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ScoreboardService {
    private final Logger log = LoggerFactory.getLogger(ScoreboardService.class);

    private final ScoreboardRepository scoreboardRepository;

    @Autowired
    public ScoreboardService(@Qualifier("scoreboardRepository") ScoreboardRepository scoreboardRepository) {
        this.scoreboardRepository = scoreboardRepository;
    }

    public Scoreboard findScoreboardByGame(Game game){
        Scoreboard scoreboard = scoreboardRepository.getScoreboardByGame(game);
        if(scoreboard == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scoreboard was not found.");
        }
        return scoreboard;
    }

    public Scoreboard createScoreboard(Game game){
        Scoreboard newScoreboard = new Scoreboard();
        newScoreboard.setGame(game);
        Map<Long,Integer> pointsPerUser = new HashMap<>();

        //append userPoints Map in Scoreboard where all have 0 points
        for (User u: game.getUserList()) {
            pointsPerUser.put(u.getId(), 0);
        }

        newScoreboard.setUserPoints(pointsPerUser);

        newScoreboard = scoreboardRepository.save(newScoreboard);
        scoreboardRepository.flush();

        log.debug("Created Information for Scoreboard: {}", newScoreboard);

        return newScoreboard;
    }

    public void updateScoreboard(List<User> users, Scoreboard scoreboard){
        Map<Long,Integer> newPoints = new HashMap<>();
        
        for (User u: users) {
            newPoints.put(u.getId(),u.getPoints());
        }

        scoreboard.setUserPoints(newPoints);

        scoreboard = scoreboardRepository.save(scoreboard);
        scoreboardRepository.flush();
    }

    public void endGame(Game game){
        Scoreboard scoreboard = scoreboardRepository.getScoreboardByGame(game);
        scoreboardRepository.delete(scoreboard);
        scoreboardRepository.flush();
    }
}
