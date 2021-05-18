package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal Scoreboard Representation
 * This class composes the internal representation of the scoreboard and defines how the scoreboard is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "SCOREBOARD")
public class Scoreboard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long scoreboardId;

    @OneToOne(fetch = FetchType.EAGER)
    private Game game;

    //Map with key=userId and points (int)
    @ElementCollection
    @MapKeyColumn(name="userId")
    @Column(name="points")
    private Map<Long,Integer> userPoints = new HashMap<>();

    public Map<Long,Integer> getUserPoints() { return userPoints; }
    public void setUserPoints(Map<Long,Integer> userPoints){
        this.userPoints = userPoints;
    }

    public Long getScoreboardId() { return scoreboardId; }
    public void setScoreboardId(Long scoreboardId) { this.scoreboardId = scoreboardId; }

    public Game getGame() { return game; }
    public void setGame(Game game) {
        if (game == null) {
            if (this.game != null) {
                this.game.setScoreboard(null);
            }
        }
        else {
            game.setScoreboard(this);}
        this.game = game; }

}
