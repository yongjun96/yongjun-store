package springboot.yongjunstore.config.authservice;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.domain.RefreshToken;
import springboot.yongjunstore.repository.RefreshTokenRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public void saveRefreshToken(JwtDto jwtDto) {

        Authentication authentication = jwtProvider.getAuthentication(jwtDto.getAccessToken());
        String email = authentication.getName();

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByEmail(email);

        //RT 존재하지 않을 경우
        if (optionalRefreshToken.isEmpty()) {

            RefreshToken refreshToken = RefreshToken.builder()
                    .refreshToken(jwtDto.getRefreshToken())
                    .email(email)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
    }


    public void saveGoogleRefreshToken(JwtDto jwtDto, String email) {

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByEmail(email);

        //RT 존재하지 않을 경우
        if (optionalRefreshToken.isEmpty()) {

            RefreshToken refreshToken = RefreshToken.builder()
                    .refreshToken(jwtDto.getRefreshToken())
                    .email(email)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
    }


    public JwtDto reissueAccessToken(String accessToken){

        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByEmail(email);

        //
        if(jwtProvider.validateRefreshToken(optionalRefreshToken.get().getRefreshToken())) {

            JwtDto jwtDto = jwtProvider.generateToken(authentication);

            //해당 이메일의 새로 발급 받은 RT로 update 해준다.
            refreshTokenRepository.updateRefreshToken(jwtDto.getRefreshToken(), email);

            return jwtDto;

        } else {
            // RT가 만료된 경우 DB에서 해당 유저의 RT를 삭제
            refreshTokenRepository.delete(optionalRefreshToken.get());
            return null;
        }
    }
}
