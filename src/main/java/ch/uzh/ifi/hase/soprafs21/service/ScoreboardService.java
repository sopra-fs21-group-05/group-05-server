package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.ScoreboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScoreboardService {
    private final Logger log = LoggerFactory.getLogger(ScoreboardService.class);

    private final ScoreboardRepository scoreboardRepository;

    @Autowired
    public ScoreboardService(@Qualifier("scoreboardRepository") ScoreboardRepository scoreboardRepository) {
        this.scoreboardRepository = scoreboardRepository;
    }
}
