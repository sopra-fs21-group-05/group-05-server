package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("gameroomRepository")
public interface GameroomRepository extends JpaRepository<Gameroom, Long>{
    Gameroom getOne(Long id);
    Optional<Gameroom> findById(Long id);
    Gameroom findByRoomname(String roomName);
}
