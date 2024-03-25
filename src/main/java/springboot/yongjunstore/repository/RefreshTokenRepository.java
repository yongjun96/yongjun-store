package springboot.yongjunstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.RefreshToken;
import springboot.yongjunstore.repository.custom.RefreshTokenRepositoryCustom;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepositoryCustom {

    Optional<RefreshToken> findByEmail(String email);

    RefreshToken findByRefreshToken(String refreshToken);

}
