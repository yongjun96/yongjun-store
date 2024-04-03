package springboot.yongjunstore.repository.custom;

import springboot.yongjunstore.domain.Member;

public interface RefreshTokenRepositoryCustom {

    void updateRefreshToken(String token, Long memberId);

}
