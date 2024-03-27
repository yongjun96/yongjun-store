package springboot.yongjunstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.room.Images;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {
}
