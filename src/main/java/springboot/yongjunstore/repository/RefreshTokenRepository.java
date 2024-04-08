package springboot.yongjunstore.repository;

import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.RefreshToken;
import springboot.yongjunstore.repository.custom.RefreshTokenRepositoryCustom;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepositoryCustom {

    Optional<RefreshToken> findByMember(Member member);

    RefreshToken findByRefreshToken(String refreshToken);

}
