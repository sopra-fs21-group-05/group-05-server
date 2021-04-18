package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.ScoreboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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

    public Scoreboard createScoreboard(Game game){
        Scoreboard newScoreboard = new Scoreboard();
        newScoreboard.setGame(game);

        //append userPoints Map in Scoreboard

        //TODO: update scoreboard entity to save points per round
        //newScoreboard = addRound();

        newScoreboard = scoreboardRepository.save(newScoreboard);
        scoreboardRepository.flush();

        return newScoreboard;
    }

    public Scoreboard addRound(Long[] userIds, int[] points, Scoreboard scoreboard){
        Map<Long,Integer> pointsPerUser = new HashMap<>();
         for(int i = 0; i < userIds.length; i++){
             pointsPerUser.put(userIds[i], points[i]);
         }

         scoreboard.setUserPoints(pointsPerUser);
         return scoreboard;
    }
}
