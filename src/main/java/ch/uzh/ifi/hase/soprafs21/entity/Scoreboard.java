package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SCOREBOARD")
public class Scoreboard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long scoreboardId;

    @OneToOne(fetch = FetchType.EAGER)
    private Game game;

    @OneToMany
    private List<User> userList= new ArrayList<>();

    @PostLoad
    public void postLoad() {
        if(game != null) {
            this.userList = game.getUserList();
        }
    }

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
