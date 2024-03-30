package springboot.yongjunstore.repository.custom;

import springboot.yongjunstore.domain.room.RoomPost;

public interface RoomPostRepositoryCustom {

    RoomPost SelectRoomPostPosts(Long roomPostId);

}
