package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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

    @Column(nullable = false)
    @OneToMany
    private List<User> userList;

    @ElementCollection
    private List<String> userRecreations = new ArrayList<String>();

    //TODO: grid pictures column with input from Pixabay API

    //TODO: uncomment & create getters/setters when the respective
    // entities (MaterialSet, Scoreboard etc.) have been created
    /*
    @JoinColumn(name = "materialSets")
    @OneToMany
    private final List<Materialset> setList = new ArrayList<Materialset>();

    @JoinColumn(name = "scoreboard")
    @OneToOne
    private Scoreboard scoreboard;
     */

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
}
