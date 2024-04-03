package springboot.yongjunstore.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.custom.RoomPostRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomPostRepository extends JpaRepository<RoomPost, Long>, RoomPostRepositoryCustom {

    Optional<RoomPost> findById(Long id);

    List<RoomPost> findByMemberEmail(String email);

    @Modifying
    @Query("DELETE FROM RoomPost rp WHERE rp.member.email = :email")
    void deleteByMemberEmail(@Param("email") String email);

}
