package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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

    //TODO: create Map with key=userId and value=Base64 encoded recreated picture
    /*@OneToMany
    private Map<Long,Base64> userRecreations;*/

    //TODO: grid pictures column with input from Pixabay API


    @ElementCollection
    private final List<MaterialSet> setList = new ArrayList<>();

    @OneToOne
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

    public Scoreboard getScoreboard() { return scoreboard; }
    public void setScoreboard(Scoreboard scoreboard) { this.scoreboard = scoreboard; }

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
