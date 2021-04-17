package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Picture")
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
