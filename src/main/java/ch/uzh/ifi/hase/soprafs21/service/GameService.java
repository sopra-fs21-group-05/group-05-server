package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
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
    public User assignMaterialset(Game game, Long userId) {
        Long gameId = game.getGameId();
        User user = getPlayerInGame(userId, gameId);

        MaterialSet newSet;
        int newSetNr;
        int prevSetId = user.getMaterialSet().getSetNr();

        if(prevSetId != 4){
            newSetNr = prevSetId+1;
        }else{
            newSetNr = 0;
        }

        newSet = MaterialSet.nameOfSetNr(newSetNr);
        user.setMaterialSet(newSet);

        return user;
    }

    //assigns picture to recreate to specific player
    public User assignPicture(Game game, Long userId) {
        Long gameId = game.getGameId();
        User user = getPlayerInGame(userId, gameId);

        String coordinatesAssignedPicture = null;
        user.setCoordinatesAssignedPicture(coordinatesAssignedPicture);

        //TODO: implementation once game initial setup is done
        return user;
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

    //Get a list of all players in the game
    public List<User> getPlayers(Long gameId){
        Game game = getExistingGame(gameId);
        return game.getUserList();
    }

    public List<User> getWinner(Long gameId){
        List<User> winners = new ArrayList<>();
        int max = 0;
        for(User user: getPlayers(gameId)){
            if(user.getPoints() >= max){
                max = user.getPoints();
            }
        }
        for (User user: getPlayers(gameId)){
            if(user.getPoints() == max){
                winners.add(user);
                UserService.restrictPlayer(user);
            }
        }
        return winners;
    }


    /*//submits the recreated picture of one user --> adds to game.userRecreations
    public User submitPicture(Game gameInput, Long userId) {
        Game currentGame = getExistingGame(gameInput.getGameId());
        User user = getPlayerInGame(userId, currentGame.getGameId());

        //submit the picture

        return user;
    }*/


}
