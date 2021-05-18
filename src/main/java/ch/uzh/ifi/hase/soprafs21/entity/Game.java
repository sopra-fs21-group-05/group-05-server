package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.uzh.ifi.hase.soprafs21.constant.GridCoordinates;

import static java.util.Collections.emptyList;

/**
 * Internal Game Representation
 * This class composes the internal representation of the game and defines how the game is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
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
    private List<User> userList = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    private List<Picture> gridPictures;

    @ElementCollection
    private List<GridCoordinates> gridCoordinates1 = Arrays.asList(GridCoordinates.values());

    @ElementCollection
    private List<GridCoordinates> gridCoordinates2 = Arrays.asList(GridCoordinates.values());

    @ElementCollection
    private List<GridCoordinates> gridCoordinates = Stream.concat(gridCoordinates1.stream(),gridCoordinates2.stream()).collect(Collectors.toList());


    @OneToOne(mappedBy = "game")
    private Scoreboard scoreboard;

    @OneToOne(mappedBy = "game")
    private Gameroom gameroom;

    @Column
    private int submittedGuesses;

    public int getSubmittedGuesses() { return submittedGuesses; }
    public void setSubmittedGuesses(int submittedGuesses) { this.submittedGuesses = submittedGuesses; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public int getRoundNr() { return roundNr; }
    public void setRoundNr(int roundNr) { this.roundNr = roundNr; }

    public List<User> getUserList() { return userList; }
    public void setUserList(List<User> userList) { this.userList = userList; }

    public List<GridCoordinates> getGridCoordinates() {
        return gridCoordinates;
    }
    public void setGridCoordinates(List<GridCoordinates> gridCoordinates){ this.gridCoordinates=gridCoordinates;}


    public Scoreboard getScoreboard() { return scoreboard; }
    public void setScoreboard(Scoreboard scoreboard) { this.scoreboard = scoreboard; }


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

    public List<String> getGridPicturesAsString() {
        List<String> picturesAsStrings= new ArrayList<>();
        for(Picture picture: gridPictures){
            picturesAsStrings.add(picture.getEncodedPicture());
        }
        return picturesAsStrings;
    }

    public List<Picture> getGridPictures(){
        return gridPictures;
    }

    public void setGridPictures(List<Picture> gridPictures) {
        this.gridPictures = gridPictures;
    }



    @Converter
    public class StringListConverter implements AttributeConverter<List<String>, String> {
        private static final String SPLIT_CHAR = ";";

        @Override
        public String convertToDatabaseColumn(List<String> stringList) {
            return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
        }

        @Override
        public List<String> convertToEntityAttribute(String string) {
            return string != null ? Arrays.asList(string.split(SPLIT_CHAR)) : emptyList();
        }
    }
}
