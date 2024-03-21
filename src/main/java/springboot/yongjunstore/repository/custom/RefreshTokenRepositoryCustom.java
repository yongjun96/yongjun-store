package springboot.yongjunstore.repository.custom;

public interface RefreshTokenRepositoryCustom {

    void updateRefreshToken(String refreshToken, String email);

}
