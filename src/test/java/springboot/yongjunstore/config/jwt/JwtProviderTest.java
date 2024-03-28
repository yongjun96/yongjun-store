package springboot.yongjunstore.config.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class JwtProviderTest {

    @Mock private JwtProvider mockJwtProvider;

    @Mock private MemberRepository mockMemberRepository;

    @Autowired private MemberRepository getMemberRepository;

    @Autowired JwtProvider getJwtProvider;

    @BeforeEach
    void setUp() {
        getMemberRepository.deleteAll();
        MockitoAnnotations.openMocks(this); // 목 객체 주입
    }

    @Test
    @DisplayName("secretKeyPlain Base64 변환")
    void jwtSecretKey(){

        SecretKey secretKey = getJwtProvider.jwtSecretKey();

        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("토큰 생성 성공")
    void generateToken(){

        // given
        String username = "user";
        String authorities = "ROLE_MEMBER";
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(authorities)));

        // when
        JwtDto jwtDto = getJwtProvider.generateToken(authentication);

        // then
        assertThat(jwtDto).isNotNull();
        assertThat("Bearer").isEqualTo(jwtDto.getGrantType());
        assertThat(jwtDto.getAccessToken()).isNotNull();
        assertThat(jwtDto.getRefreshToken()).isNotNull();

    }

    @Test
    @DisplayName("OAuth google 토큰 생성 성공")
    void googleLoginGenerateToken(){

        // given
        String username = "user";
        String authorities = "ROLE_MEMBER";

        // when
        JwtDto jwtDto = getJwtProvider.googleLoginGenerateToken(username, authorities);

        // then
        assertThat(jwtDto).isNotNull();
        assertThat("Bearer").isEqualTo(jwtDto.getGrantType());
        assertThat(jwtDto.getAccessToken()).isNotNull();
        assertThat(jwtDto.getRefreshToken()).isNotNull();

    }

    @Test
    @DisplayName("토큰을 복호화 후 정보 조회 성공")
    void getAuthentication() {

        // given
        Long accessTime = 3000000L;

        Long now = (new Date()).getTime();

        Member member = Member.builder()
                .email("test@gmail.com")
                .password("qwer!1234")
                .role(Role.MEMBER)
                .build();

        getMemberRepository.save(member);

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + accessTime);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getEmail()))
                .claim("role", member.getRole())
                .setExpiration(accessTokenExpiresIn)
                .signWith(getJwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtDto jwtDto = JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();

        // when
        Authentication authentication = getJwtProvider.getAuthentication(jwtDto.getAccessToken());

        // then
        assertThat(authentication).isNotNull();
        assertThat(UsernamePasswordAuthenticationToken.class).isEqualTo(authentication.getClass());
        assertThat("test@gmail.com").isEqualTo(authentication.getName());
        assertThat("MEMBER").isEqualTo(authentication.getAuthorities().iterator().next().getAuthority());
    }


    @Test
    @DisplayName("토큰을 복호 실패 : 해당 계정을 찾을 수 없습니다.")
    void getAuthenticationMemberNotFound() {

        // given
        Long accessTime = 3000000L;

        Long now = (new Date()).getTime();

        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1234")
                .role(Role.MEMBER)
                .build();

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + accessTime);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getEmail()))
                .claim("role", member.getRole())
                .setExpiration(accessTokenExpiresIn)
                .signWith(getJwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtDto jwtDto = JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();

        // expected
        assertThatThrownBy(() -> getJwtProvider.getAuthentication(jwtDto.getAccessToken()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("해당 계정을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("권한 정보가 없습니다.")
    void getAuthenticationClaimsNotFound() {

        // given
        Long accessTime = 3000000L;

        Long now = (new Date()).getTime();

        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1234")
                .role(null)
                .build();

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + accessTime);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getEmail()))
                .claim("role", member.getRole())
                .setExpiration(accessTokenExpiresIn)
                .signWith(getJwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtDto jwtDto = JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();

        // expected
        assertThatThrownBy(() -> getJwtProvider.getAuthentication(jwtDto.getAccessToken()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("권한 정보가 없습니다.");
    }


    @Test
    @DisplayName("토큰을 복호화 후 google회원 정보 조회 성공")
    void getAuthenticationGoogle() {

        // given
        Long accessTime = 3000000L;

        Long now = (new Date()).getTime();

        Member member = Member.builder()
                .email("test@gmail.com")
                .password("qwer!1234")
                .role(Role.MEMBER)
                .provider("google")
                .build();

        Member findMember = getMemberRepository.save(member);

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + accessTime);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(findMember.getEmail()))
                .claim("role", findMember.getRole())
                .setExpiration(accessTokenExpiresIn)
                .signWith(getJwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtDto jwtDto = JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();

        // when
        Authentication authentication = getJwtProvider.getAuthentication(jwtDto.getAccessToken());

        // then
        assertThat(authentication).isNotNull();
        assertThat(OAuth2AuthenticationToken.class).isEqualTo(authentication.getClass());
        assertThat("test@gmail.com").isEqualTo(authentication.getName());
        assertThat("MEMBER").isEqualTo(authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("토큰 유효성 검사 성공")
    void validateToken(){
        // given
        String token = "정상 토큰입니다";

        when(mockJwtProvider.validateToken(token)).thenReturn(true);

        boolean result =  mockJwtProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 : 변조된 토큰")
    void validateTokenSignatureException(){
        // given
        String token = "변조된 토큰입니다";

        when(mockJwtProvider.validateToken(token)).thenThrow(new JwtException(ErrorCode.JWT_SIGNATURE_EXCEPTION.getMessage()));

        assertThatThrownBy(() -> mockJwtProvider.validateToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(ErrorCode.JWT_SIGNATURE_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 : 잘못된 구조의 지원되지 않는 토큰")
    void validateTokenMalformedJwtException(){
        // given
        String token = "잘못된 구조의 지원되지 않는 토큰입니다";

        when(mockJwtProvider.validateToken(token)).thenThrow(new JwtException(ErrorCode.JWT_MALFORMED_JWT_EXCEPTION.getMessage()));

        assertThatThrownBy(() -> mockJwtProvider.validateToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(ErrorCode.JWT_MALFORMED_JWT_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 : 만료된 토큰")
    void validateTokenExpiredJwtException(){
        // given
        String token = "만료된 토큰입니다";

        when(mockJwtProvider.validateToken(token)).thenThrow(new JwtException(ErrorCode.JWT_EXPIRED_JWT_EXCEPTION.getMessage()));

        assertThatThrownBy(() -> mockJwtProvider.validateToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(ErrorCode.JWT_EXPIRED_JWT_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 : 잘못된 구조의 지원되지 않는 토큰")
    void validateTokenUnsupportedJwtException(){
        // given
        String token = "다른 형식의 토큰입니다";

        when(mockJwtProvider.validateToken(token)).thenThrow(new JwtException(ErrorCode.JWT_UNSUPPORTED_JWT_EXCEPTION.getMessage()));

        assertThatThrownBy(() -> mockJwtProvider.validateToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(ErrorCode.JWT_UNSUPPORTED_JWT_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("토큰 정보 확인 성공 : Request Header 에서 토큰 정보 추출")
    void resolveToken() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String tokenValue = "testToken";
        request.addHeader("Authorization", "Bearer " + tokenValue);

        // When
        String resolvedToken = getJwtProvider.resolveToken(request);

        // Then
        assertThat(tokenValue).isEqualTo(resolvedToken);
    }

    @Test
    @DisplayName("토큰 정보 확인 실패 : Bearer가 아닌 잘 못된 값인 경우 null 리턴")
    void resolveTokenNull() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String tokenValue = "testToken";
        request.addHeader("Authorization", "잘못된 값 " + tokenValue);

        // When
        String resolvedToken = getJwtProvider.resolveToken(request);

        // Then
        assertThat(resolvedToken).isNull();
    }

    @Test
    @DisplayName("RefreshToken 만료되지 않은 토큰 성공")
    void validateRefreshToken() {

        // given
        Long refreshTime = 3000000L;

        Long now = (new Date()).getTime();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTime))
                .signWith(getJwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtDto jwtDto = JwtDto.builder()
                .refreshToken(refreshToken)
                .build();

        // when
        boolean result = getJwtProvider.validateRefreshToken(jwtDto.getRefreshToken());

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("RefreshToken 만료된 토큰 false 리턴")
    void validateRefreshTokenFail() throws InterruptedException {

        // given
        Long refreshTime = 1L;

        Long now = (new Date()).getTime();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTime))
                .signWith(getJwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtDto jwtDto = JwtDto.builder()
                .refreshToken(refreshToken)
                .build();

        // when
        boolean result = getJwtProvider.validateRefreshToken(jwtDto.getRefreshToken());

        assertThat(result).isFalse();
    }

}