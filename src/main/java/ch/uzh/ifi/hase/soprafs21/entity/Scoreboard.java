package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Scoreboard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long scoreboardId;

    @OneToOne
    private Game game;

    @OneToMany
    private final List<User> userList = game.getUserList();

    //TODO: needed? else remove
    @ElementCollection
    private List<Integer> previousRounds= new ArrayList<>();


    public Long getScoreboardId() { return scoreboardId; }
    public void setScoreboardId(Long scoreboardId) { this.scoreboardId = scoreboardId; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public List<User> getUserList() { return userList; }

    public List<Integer> getPreviousRounds() { return previousRounds; }
    public void setPreviousRounds(List<Integer> previousRounds) { this.previousRounds = previousRounds; }
}
