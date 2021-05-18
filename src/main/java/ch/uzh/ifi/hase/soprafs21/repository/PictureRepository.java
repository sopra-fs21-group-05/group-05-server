package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("pictureRepository")
public interface PictureRepository extends JpaRepository<Picture, Long> {
    Picture getOne(Long id);

}
