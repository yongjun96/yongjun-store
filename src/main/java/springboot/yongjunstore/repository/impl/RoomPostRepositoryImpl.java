package springboot.yongjunstore.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.QMember;
import springboot.yongjunstore.domain.room.QRoomPost;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.custom.RoomPostRepositoryCustom;

import static springboot.yongjunstore.domain.QMember.member;
import static springboot.yongjunstore.domain.room.QRoomPost.roomPost;

@RequiredArgsConstructor
public class RoomPostRepositoryImpl implements RoomPostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public RoomPost SelectRoomPostPosts(Long roomPostId){

        return jpaQueryFactory
                .selectFrom(roomPost)
                .join(roomPost.member, member).fetchJoin()
                .where(roomPost.id.eq(roomPostId))
                .fetchOne();
    }
}
