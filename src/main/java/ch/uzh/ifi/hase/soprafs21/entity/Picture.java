package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal Picture Representation
 * This class composes the internal representation of the picture and defines how the picture is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "PICTURE")
public class Picture implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pictureId;

    @Lob
    private String encodedPicture;

    public Long getPictureId() {
        return pictureId;
    }
    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }

    public String getEncodedPicture() {
        return encodedPicture;
    }
    public void setEncodedPicture(String encodedPicture) {
        this.encodedPicture = encodedPicture;
    }
}
