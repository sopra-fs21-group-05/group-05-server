package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameroomRepository;
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
import java.util.UUID;

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

    public List<Gameroom> getGamerooms() {
        return this.gameroomRespository.findAll();
    }

    public Gameroom getGameroomById(Long id){
        Gameroom gameroomById = gameroomRespository.getOne(id);
        if(gameroomById == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gameroom was not found.");
        }
        return gameroomById;
    }

    public Gameroom addGame(Gameroom gameroom, Game game){
        gameroom.setGame(game);
        gameroom = gameroomRespository.save(gameroom);
        gameroomRespository.flush();
        return gameroom;
    }

    public Gameroom createGameroom(Gameroom newGameroom) {

        //check if gameroom already exists
        checkIfGameroomExists(newGameroom);

        // saves the given entity but data is only persisted in the database once flush() is called
        newGameroom = gameroomRespository.save(newGameroom);
        gameroomRespository.flush();

        log.debug("Created Information for Gameroom: {}", newGameroom);
        return newGameroom;
    }

    public Gameroom joinGameroom(Gameroom gameroom, User user){

        Gameroom gameroomById = checkGameroomCredentials(gameroom);

        List<User> currentUsers = gameroomById.getUsers();
        if (currentUsers.contains(user)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already joined this gameroom!");
        }
        currentUsers.add(user);
        gameroomById.setUsers(currentUsers);

        gameroomById = gameroomRespository.save(gameroomById);
        gameroomRespository.flush();

        log.debug("{} added to gameroom: {}", user.getUsername(), gameroomById.getRoomname());

        return gameroomById;
    }

    private void checkIfGameroomExists(Gameroom gameroom) {
        Gameroom gameroomByName = gameroomRespository.findByRoomname(gameroom.getRoomname());

        String baseErrorMessage = "The roomname provided already exists. Therefore, the gameroom could not be created!";

        if (gameroomByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
    }

    private Gameroom checkGameroomCredentials(Gameroom gameroom){
        Gameroom gameroomById = gameroomRespository.getOne(gameroom.getId());

        //throw exception if no user with this username is found
        String baseErrorMessage = "Credentials are invalid.";
        if (gameroomById == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, baseErrorMessage);
        }

        //throw exception if the password doesn't match for the username
        if(!gameroomById.getPassword().equals(gameroom.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, baseErrorMessage);
        }

        return gameroomById;
    }
}
