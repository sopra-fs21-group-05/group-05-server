package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;

public class GameGetDTO {

    private Long gameId;
    private MaterialSet materialSet;
    private String coordinatesAssignedPicture;

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public MaterialSet getMaterialSet() { return materialSet; }
    public void setMaterialSet(MaterialSet materialSet) { this.materialSet = materialSet; }

    public String getCoordinatesAssignedPicture() { return coordinatesAssignedPicture; }
    public void setCoordinatesAssignedPicture(String coordinatesAssignedPicture) { this.coordinatesAssignedPicture = coordinatesAssignedPicture; }
}
