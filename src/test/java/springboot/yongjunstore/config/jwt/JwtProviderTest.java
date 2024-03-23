package springboot.yongjunstore.config.jwt;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.repository.MemberRepository;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class JwtProviderTest {

    @Mock private JwtProvider jwtProvider;

    @Mock private MemberRepository memberRepository;

    @Autowired JwtProvider getJwtProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 목 객체 주입
    }

    @Test
    @DisplayName("secretKeyPlain Base64 변환")
    void jwtSecretKey(){

        SecretKey secretKey = getJwtProvider.jwtSecretKey();

        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("토큰 유효성 검사 성공")
    void validateToken(){
        // given
        String token = "정상 토큰입니다";

        when(jwtProvider.validateToken(token)).thenReturn(true);

        boolean result =  jwtProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 : 변조된 토큰")
    void validateTokenSignatureException(){
        // given
        String token = "변조된 토큰입니다";

        when(jwtProvider.validateToken(token)).thenThrow(new JwtException(ErrorCode.JWT_SIGNATURE_EXCEPTION.getMessage()));

        assertThatThrownBy(() -> jwtProvider.validateToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(ErrorCode.JWT_SIGNATURE_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 : 잘못된 구조의 지원되지 않는 토큰")
    void validateTokenMalformedJwtException(){
        // given
        String token = "잘못된 구조의 지원되지 않는 토큰입니다";

        when(jwtProvider.validateToken(token)).thenThrow(new JwtException(ErrorCode.JWT_MALFORMED_JWT_EXCEPTION.getMessage()));

        assertThatThrownBy(() -> jwtProvider.validateToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(ErrorCode.JWT_MALFORMED_JWT_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 : 만료된 토큰")
    void validateTokenExpiredJwtException(){
        // given
        String token = "만료된 토큰입니다";

        when(jwtProvider.validateToken(token)).thenThrow(new JwtException(ErrorCode.JWT_EXPIRED_JWT_EXCEPTION.getMessage()));

        assertThatThrownBy(() -> jwtProvider.validateToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(ErrorCode.JWT_EXPIRED_JWT_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 : 잘못된 구조의 지원되지 않는 토큰")
    void validateTokenUnsupportedJwtException(){
        // given
        String token = "다른 형식의 토큰입니다";

        when(jwtProvider.validateToken(token)).thenThrow(new JwtException(ErrorCode.JWT_UNSUPPORTED_JWT_EXCEPTION.getMessage()));

        assertThatThrownBy(() -> jwtProvider.validateToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(ErrorCode.JWT_UNSUPPORTED_JWT_EXCEPTION.getMessage());
    }

}