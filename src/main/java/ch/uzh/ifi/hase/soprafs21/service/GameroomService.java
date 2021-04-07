package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameroomRespository;
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

    private final GameroomRespository gameroomRespository;

    @Autowired
    public GameroomService(@Qualifier("gameroomRepository") GameroomRespository gameroomRespository) {
        this.gameroomRespository = gameroomRespository;
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

    public Gameroom joinGameroom(Gameroom gameroom){

        //TODO: check gameroom credentials

        //TODO: add user to list un gameroom and return it

        return gameroom;
    }

    private void checkIfGameroomExists(Gameroom gameroom) {
        Gameroom gameroomByName = gameroomRespository.findByRoomname(gameroom.getRoomname());

        String baseErrorMessage = "The roomname provided already exists. Therefore, the gameroom could not be created!";

        if (gameroomByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
    }
}
