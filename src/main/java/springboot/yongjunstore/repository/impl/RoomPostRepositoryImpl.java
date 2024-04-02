package springboot.yongjunstore.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.QRoomPost;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.custom.RoomPostRepositoryCustom;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static springboot.yongjunstore.domain.QMember.member;
import static springboot.yongjunstore.domain.room.QImages.images;
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


    @Override
    public Page<RoomPost> searchRoomPostList(String searchOption, String searchContent, Pageable pageable){

        List<RoomPost> roomPostList = jpaQueryFactory
                .selectFrom(roomPost)
                .join(roomPost.member, member)
                .where(containsSearch(searchOption, searchContent))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(roomPost.createAt.desc())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(roomPost.count())
                .from(roomPost)
                .join(roomPost.member, member)
                .where(containsSearch(searchOption, searchContent));

        return PageableExecutionUtils.getPage(roomPostList, pageable, count::fetchOne);

    }


    private BooleanExpression containsSearch(String searchOption, String searchContent){

        //작성자 email
        if(searchOption.equals("email")){
            return StringUtils.hasText(searchContent) ? roomPost.member.email.contains(searchContent) : null;
        }

        //내용 + 제목
        else if (searchOption.equals("titleContent")){
            return StringUtils.hasText(searchContent) ?
                    roomPost.content.contains(searchContent)
                    .or(roomPost.title.contains(searchContent)): null;
        }

        //제목
        else if(searchOption.equals("title")){
            return StringUtils.hasText(searchContent) ? roomPost.title.contains(searchContent) : null;
        }

        // 주소
        else if(searchOption.equals("address")){
            return StringUtils.hasText(searchContent) ? roomPost.address.contains(searchContent) : null;
        }
        else {
            return null;
        }
    }
}
