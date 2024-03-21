package springboot.yongjunstore.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.repository.custom.RefreshTokenRepositoryCustom;

import static springboot.yongjunstore.domain.QRefreshToken.*;


@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void updateRefreshToken(String refreshToken, String email){

        jpaQueryFactory
                .update(refreshToken1)
                .set(refreshToken1.refreshToken, refreshToken)
                .where(refreshToken1.email.eq(email))
                .execute();
    }
}
