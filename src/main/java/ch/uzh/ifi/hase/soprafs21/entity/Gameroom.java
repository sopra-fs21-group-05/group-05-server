package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Gameroom Representation
 * This class composes the internal representation of the gameroom and defines how the gameroom is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "GAMEROOM")
public class Gameroom implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String roomname;

    @OneToMany
    private List<User> users = new ArrayList<>();

    @OneToMany
    private List<User> lastWinner = new ArrayList<>();

    @OneToOne
    private Game game;

    @Column
    private Long startedGame;

    @PostLoad
    public void postLoad() {
        if(game != null) {
            this.startedGame = game.getGameId();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRoomname() { return roomname; }
    public void setRoomname(String roomname) { this.roomname = roomname; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public Long getStartedGame() { return startedGame; }
    public void setStartedGame(Long startedGame) { this.startedGame = startedGame; }

    public List<User> getLastWinner() {
        return lastWinner;
    }
    public void setLastWinner(List<User> lastWinner) {
        this.lastWinner = lastWinner;
    }
}
