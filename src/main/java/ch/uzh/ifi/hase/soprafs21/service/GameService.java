package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;


    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }


    //assigns the "next" materialset to specific player
    //TODO: modify once MaterialSet (entity or enum) is created
    public int assignMaterialset(Game game, Long userId) {
        Long gameId = game.getGameId();
        User user = getPlayerInGame(userId, gameId);

        int prevSetId = 0;
        int currentSetId;
        /*
        int prevSetId = user.getMaterialset().setId;
         */
        if(prevSetId != 4){
            currentSetId = prevSetId+1;
        }else{
            currentSetId = 0;
        }
        return currentSetId;
    }

    //assigns picture to recreate to specific player
    public String assignPicture(Game game, Long userId) {
        String coordinatesAssignedPicture = "";
        //TODO: implementation once game initial setup is done
        return coordinatesAssignedPicture;
    }


    //returns an existing game
    public Game getExistingGame(Long gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        String baseErrorMessage = "The %s provided %s not found.";
        if(optionalGame.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "gameId", "was"));
        }
        return optionalGame.get();
    }


    //returns a specific player/user in an existing game
    public User getPlayerInGame(Long userId, Long gameId){
        Game game = getExistingGame(gameId);
        User targetUser = null;
        for (User user: game.getUserList()){
            if(user.getId().equals(userId)){
                targetUser = user;
            }
        }

        String baseErrorMessage = "The %s provided %s not found.";
        if(targetUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "userId", "was"));
        }
        return targetUser;
    }

}
