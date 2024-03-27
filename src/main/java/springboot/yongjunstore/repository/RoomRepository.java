package springboot.yongjunstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.room.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

}
