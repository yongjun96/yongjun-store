package springboot.yongjunstore.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.QRefreshToken;
import springboot.yongjunstore.repository.custom.RefreshTokenRepositoryCustom;

import static springboot.yongjunstore.domain.QRefreshToken.refreshToken1;


@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void updateRefreshToken(String token, Long memberId){

        jpaQueryFactory
                .update(refreshToken1)
                .set(refreshToken1.refreshToken, token)
                .where(refreshToken1.member.id.eq(memberId))
                .execute();
    }
}
