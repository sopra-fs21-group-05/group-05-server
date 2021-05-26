package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameroomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Gameroom Service
 * This class is the "worker" and responsible for all functionality related to the gameroom
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameroomService {

    private final Logger log = LoggerFactory.getLogger(GameroomService.class);

    private final GameroomRepository gameroomRespository;

    @Autowired
    public GameroomService(@Qualifier("gameroomRepository") GameroomRepository gameroomRespository) {
        this.gameroomRespository = gameroomRespository;
    }

    //returns a list with all gamerooms
    public List<Gameroom> getGamerooms() {
        return this.gameroomRespository.findAll();
    }

    //gets a gameroom by its id
    public Gameroom getGameroomById(Long id){
        Optional<Gameroom> gameroomById = gameroomRespository.findById(id);
        Gameroom fetchedgameroom = gameroomById.orElse(null);

        if(fetchedgameroom == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gameroom was not found.");
        }
        return fetchedgameroom;
    }

    //add a game to a gameroom
    public Gameroom addGame(Gameroom gameroom, Game game){
        gameroom.setGame(game);
        gameroom = gameroomRespository.save(gameroom);
        gameroomRespository.flush();
        return gameroom;
    }

    //create a new gameroom
    public Gameroom createGameroom(Gameroom newGameroom) {

        //check if gameroom already exists
        checkIfGameroomExists(newGameroom);

        // saves the given entity but data is only persisted in the database once flush() is called
        newGameroom = gameroomRespository.save(newGameroom);
        gameroomRespository.flush();

        log.debug("Created Information for Gameroom: {}", newGameroom);
        return newGameroom;
    }

    //adds user to the gameroom
    public Gameroom joinGameroom(Gameroom gameroom, User user){

        Gameroom gameroomById = checkGameroomCredentials(gameroom);

        List<User> currentUsers = gameroomById.getUsers();
        for (User u:currentUsers) {
            if (u.getId().equals(user.getId())){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User already joined this gameroom!");
            }
        }

        currentUsers.add(user);
        gameroomById.setUsers(currentUsers);

        gameroomById = gameroomRespository.save(gameroomById);
        gameroomRespository.flush();

        log.debug("{} added to gameroom: {}", user.getUsername(), gameroomById.getRoomname());

        return gameroomById;
    }

    //removes user from a gameroom
    public Gameroom leaveGameroom(Long roomId, User user){
        Gameroom gameroom = getGameroomById(roomId);

        List<User> userList = gameroom.getUsers();

        if (!userList.contains(user)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't leave a gameroom you never joined!");
        }
        else{
            userList.remove(user);
            if(userList.isEmpty()){
                gameroomRespository.delete(gameroom);
                gameroom = null;
            }
            else {
                gameroom.setUsers(userList);
                gameroomRespository.save(gameroom);
            }
            gameroomRespository.flush();
        }

        log.debug("User {} left gameroom {}",user, gameroom);
        return gameroom;
    }

    //saves winners of last game to gameroom
    public Gameroom storeWinner(Long roomId, List<User> winners){
        Gameroom gameroom = getGameroomById(roomId);
        gameroom.setLastWinner(winners);
        gameroomRespository.save(gameroom);
        gameroomRespository.flush();
        return gameroom;
    }

    //get a gameroom by the corresponding game
    public Gameroom getGameroomByGameId(Long gameId){
        List<Gameroom> allGamerooms = gameroomRespository.findAll();
        for(Gameroom gameroom : allGamerooms){
            if(gameroom.getGame().getGameId().equals(gameId)){
                return gameroom;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game was found in this gameroom");
    }

    //throws exception if a gameroom name already exists
    private void checkIfGameroomExists(Gameroom gameroom) {
        Gameroom gameroomByName = gameroomRespository.findByRoomname(gameroom.getRoomname());

        String baseErrorMessage = "The roomname provided already exists. Therefore, the gameroom could not be created!";

        if (gameroomByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
    }

    //checks if gameroom credentials are correct
    private Gameroom checkGameroomCredentials(Gameroom gameroom){

        Gameroom fetchedgameroom = getGameroomById(gameroom.getId());

        //throw exception if no user with this username is found
        String baseErrorMessage = "Credentials are invalid.";
        if (fetchedgameroom == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, baseErrorMessage);
        }

        //throw exception if the password doesn't match for the username
        if(!fetchedgameroom.getPassword().equals(gameroom.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, baseErrorMessage);
        }

        return fetchedgameroom;
    }

    //end game for a provided gameroom
    public Gameroom endGame(Long roomId){
        Gameroom gameroom = getGameroomById(roomId);
        gameroom.setGame(null);
        gameroom.setStartedGame(null);
        gameroomRespository.save(gameroom);
        gameroomRespository.flush();
        return gameroom;
    }

    //checks if creator is still in gameroom, assigns new one if not
    public Gameroom checkCreator(Gameroom gameroom){
        Long creator = gameroom.getCreator();
        boolean creatorExists = false;
        for (User u: gameroom.getUsers()) {
            if(u.getId().equals(creator)){
                creatorExists = true;
                break;
            }
        }
        if(!creatorExists){
            gameroom.setCreator(gameroom.getUsers().get(0).getId());
        }
        gameroomRespository.save(gameroom);
        gameroomRespository.flush();

        return gameroom;
    }
}
