package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("scoreboardRepository")
public interface ScoreboardRepository extends JpaRepository<Scoreboard, Long> {
    Scoreboard getOne(Long scoreboardId);
    Scoreboard getScoreboardByGame(Game game);
}
