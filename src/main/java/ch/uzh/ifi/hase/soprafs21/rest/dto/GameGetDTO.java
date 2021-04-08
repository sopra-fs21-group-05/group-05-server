package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;

public class GameGetDTO {
    private int gameId;
    private MaterialSet materialSet;
    private String coordinatesAssignedPicture;


    public String getCoordinatesAssignedPicture() { return coordinatesAssignedPicture; }
    public void setCoordinatesAssignedPicture(String coordinatesAssignedPicture) { this.coordinatesAssignedPicture = coordinatesAssignedPicture; }

    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }

    public MaterialSet getMaterialSet() { return materialSet; }
    public void setMaterialSet(MaterialSet materialSet) { this.materialSet = materialSet; }
}
