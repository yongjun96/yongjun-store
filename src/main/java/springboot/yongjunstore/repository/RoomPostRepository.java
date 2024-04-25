package springboot.yongjunstore.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.custom.RoomPostRepositoryCustom;

import java.util.Optional;

@Repository
public interface RoomPostRepository extends JpaRepository<RoomPost, Long>, RoomPostRepositoryCustom {

    @Query("select rp from RoomPost rp where rp.id = :roomPostId")
    Optional<RoomPost> findById(@Param("roomPostId") Long roomPostId);

    @Modifying
    @Query("update RoomPost rp set rp.roomStatus = '종료' WHERE rp.id = :roomPostId")
    void deleteByRoomPostId(@Param("roomPostId") Long roomPostId);

}
