package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            this.userList = new ArrayList<>(game.getUserList());
        }
    }

    //Map with key=userId and points (int)
    @MapKeyColumn(name="userId")
    @Column(name="pointsOfUsers")
    @ElementCollection
    private Map<Long,Integer> userPoints = new HashMap<>();

    public Map<Long,Integer> getUserPoints() { return userPoints; }
    public void setUserPoints(Map<Long,Integer> userPoints){
        this.userPoints = userPoints;
    }

    public Long getScoreboardId() { return scoreboardId; }
    public void setScoreboardId(Long scoreboardId) { this.scoreboardId = scoreboardId; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public List<User> getUserList() { return userList; }
}
