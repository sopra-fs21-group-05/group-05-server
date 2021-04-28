package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GridCoordinates;
import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true, unique = true)
    private String token;

    @Column(nullable = false)
    private UserStatus status;

    @Column(columnDefinition = "int default 0")
    private int points;

    @Column(columnDefinition = "boolean default false")
    private boolean restrictedMode;

    @Column
    private MaterialSet materialSet;

    @Column
    private GridCoordinates coordinatesAssignedPicture;

    @Lob
    private String recreatedPicture;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
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

    public GridCoordinates getCoordinatesAssignedPicture() { return coordinatesAssignedPicture; }
    public void setCoordinatesAssignedPicture(GridCoordinates coordinatesAssignedPicture) { this.coordinatesAssignedPicture = coordinatesAssignedPicture; }

    public String getRecreatedPicture() {
        return recreatedPicture;
    }
    public void setRecreatedPicture(String recreatedPicture) {
        this.recreatedPicture = recreatedPicture;
    }
}
