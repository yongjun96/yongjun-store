package springboot.yongjunstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.room.RoomPost;

@Repository
public interface RoomPostRepository extends JpaRepository<RoomPost, Long> {

}
