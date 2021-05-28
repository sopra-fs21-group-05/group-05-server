package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.ScoreboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScoreboardServiceTest {

    @Mock
    private ScoreboardRepository scoreboardRepository;

    @InjectMocks
    private ScoreboardService scoreboardService;
    private Scoreboard testScoreboard;
    private Game game;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // create a UserList to be added to the game
        User user1 = new User();
        user1.setId(1L);
        user1.setPoints(4);

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        //userList.add(user2);

        //create a game to be added to the scoreboard and add the userList
        Game testGame = new Game();
        testGame.setUserList(userList);
        this.game = testGame;
        //create
        // create a scoreboard
        testScoreboard = new Scoreboard();


        //
        Map<Long, Integer> userPoints = new HashMap<>();
        userPoints.put(1L, 4);

        testScoreboard.setUserPoints(userPoints);
        testScoreboard.setGame(testGame);

        testScoreboard.setUserPoints(userPoints);

        Mockito.when(scoreboardRepository.save(Mockito.any())).thenReturn(testScoreboard);


    }

    @Test
    public void createScoreboard_success(){
        //create the scoreboard fro the setup
        Scoreboard createdScoreboard = scoreboardService.createScoreboard(this.game);

        Mockito.verify(scoreboardRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testScoreboard.getScoreboardId(), createdScoreboard.getScoreboardId());
        assertEquals(testScoreboard.getGame(), createdScoreboard.getGame());
        assertEquals(testScoreboard.getUserPoints(), createdScoreboard.getUserPoints());
    }

    @Test
    public void findScoreboardByGame_success(){
        // create the scoreboard from the setup
        Scoreboard expectedScoreboard = scoreboardService.createScoreboard(this.game);

        //ensure the method call to the repository returns the scoreboard from the setup
        Mockito.when(scoreboardRepository.getScoreboardByGame(Mockito.any())).thenReturn(this.testScoreboard);
        // call the method we want to test
        Scoreboard thisScoreboard = scoreboardService.findScoreboardByGame(game);

        assertEquals(expectedScoreboard,thisScoreboard);
    }

    @Test
    public void findScoreboardByGame_throwsException(){

        //ensure the call to the repository returns null
        Mockito.when(scoreboardRepository.getScoreboardByGame(Mockito.any())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> scoreboardService.findScoreboardByGame(this.game));
    }

    @Test
    public void updateScoreboard_success(){
        //create the scoreboard from the setup
        Scoreboard scoreboard = scoreboardService.createScoreboard(this.game);

        // get the userlist of the game and change the points of the first user
        List<User> userList = this.game.getUserList();
        User changingUser = userList.get(0);
        changingUser.setPoints(7);

        // make e new hashmap with the updated user and points
        Map <Long, Integer> newUserPoints = new HashMap<>();
        newUserPoints.put(changingUser.getId(),changingUser.getPoints());
        scoreboard.setUserPoints(newUserPoints);

        //ensure that this time the method call returns the changed scoreboard
        Mockito.when(scoreboardRepository.save(Mockito.any())).thenReturn(scoreboard);

        //call the method that we test
        scoreboardService.updateScoreboard(userList, scoreboard);

        // ensure the change has been saved
        assertEquals(7, scoreboard.getUserPoints().get(changingUser.getId()));
    }
}

