package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class GameGetDTO {
    private int gameId;
    private int setNr;
    private String coordinatesAssignedPicture;


    public int getSetNr() { return setNr; }
    public void setSetNr(int setNr) { this.setNr = setNr; }


    public String getCoordinatesAssignedPicture() { return coordinatesAssignedPicture; }
    public void setCoordinatesAssignedPicture(String coordinatesAssignedPicture) { this.coordinatesAssignedPicture = coordinatesAssignedPicture; }

    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }
}
