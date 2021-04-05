package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameroomRepository")
public interface GameroomRespository extends JpaRepository<Gameroom, Long>{
    Gameroom getOne(Long id);

    Gameroom findByRoomname(String roomName);
}
