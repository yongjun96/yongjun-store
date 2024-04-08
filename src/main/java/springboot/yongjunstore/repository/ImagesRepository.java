package springboot.yongjunstore.repository;

import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.room.Images;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {

    List<Images> findByRoomPostId(Long roomPostId);

    @Modifying
    @Query("DELETE FROM Images i WHERE i.roomPost.id = :roomPostId")
    void deleteImagesByRoomPostId(@Param("roomPostId") Long roomPostId);
}
