package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

import ch.uzh.ifi.hase.soprafs21.constant.GridCoordinates;
import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.User;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long gameId;

    @Column(nullable = false)
    private int roundNr;

    @OneToMany
    private List<User> userList = new ArrayList<User>();

    //Map with key=userId and value=(Base64 encoded) string recreated picture
    @ElementCollection
    private Map<Long,String> userRecreations;


    @ElementCollection
    private final List<GridCoordinates> gridCoordinates = Arrays.asList(GridCoordinates.values());

    @ElementCollection
    private final List<MaterialSet> setList = Arrays.asList(MaterialSet.values());

    @OneToOne(mappedBy = "game")
    private Scoreboard scoreboard;

    @OneToOne(mappedBy = "game")
    private Gameroom gameroom;


    //TODO: uncomment if needed, else delete
    /*
    @OneToMany
    private List<User> previousWinnersList = null;
     */


    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public int getRoundNr() { return roundNr; }
    public void setRoundNr(int roundNr) { this.roundNr = roundNr; }

    public List<User> getUserList() { return userList; }
    public void setUserList(List<User> userList) { this.userList = userList; }

    public List<GridCoordinates> getGridCoordinates() { return gridCoordinates; }

    public Scoreboard getScoreboard() { return scoreboard; }
    public void setScoreboard(Scoreboard scoreboard) {
        if (scoreboard == null) {
            if (this.scoreboard != null) {
                this.scoreboard.setGame(null);
            }
        }
        else {
            scoreboard.setGame(this);}
        this.scoreboard = scoreboard; }

    public List<MaterialSet> getSetList() { return setList; }


    public Gameroom getGameroom() { return gameroom; }
    public void setGameroom(Gameroom gameroom) {
        if (gameroom == null) {
            if (this.gameroom != null) {
                this.gameroom.setGame(null);
            }
        }
        else {
            gameroom.setGame(this);}
        this.gameroom = gameroom; }

}
