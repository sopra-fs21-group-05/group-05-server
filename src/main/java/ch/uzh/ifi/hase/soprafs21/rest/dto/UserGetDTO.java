package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;

public class UserGetDTO {

    private Long id;
    private String username;
    private UserStatus status;
    private int points;
    private boolean restrictedMode;
    private MaterialSet materialSet;
    private String token;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }

    public boolean getRestrictedMode() {
        return restrictedMode;
    }
    public void setRestrictedMode(boolean restrictedMode) {
        this.restrictedMode = restrictedMode;
    }

    public MaterialSet getMaterialSet() {
        return materialSet;
    }
    public void setMaterialSet(MaterialSet materialSet) {
        this.materialSet = materialSet;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
