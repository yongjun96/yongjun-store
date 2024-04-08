package springboot.yongjunstore.repository.custom;

import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springboot.yongjunstore.domain.room.RoomPost;

public interface RoomPostRepositoryCustom {

    RoomPost SelectRoomPostPosts(Long roomPostId);

    Page<RoomPost> searchRoomPostList(String searchOption, String searchContent, Pageable pageable);

}
