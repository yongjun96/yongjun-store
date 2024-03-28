package springboot.yongjunstore.config.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.RefreshToken;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.repository.RefreshTokenRepository;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RefreshTokenServiceTest {

    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private JwtProvider jwtProvider;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private MemberRepository memberRepository;

    @BeforeEach
    void setUp(){
        memberRepository.deleteAll();
        refreshTokenRepository.deleteAll();
    }

    JwtDto beforeJwt(Member member){

        Long accessTime = 3000000L;
        Long refreshTime = 6000000L;

        long now = (new Date()).getTime();

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + accessTime);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getEmail()))
                .claim("role", member.getRole())
                .setExpiration(accessTokenExpiresIn)
                .signWith(jwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTime))
                .signWith(jwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        return JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Test
    @DisplayName("refreshToken 저장 성공 : accessToken에서 정보를 조회하고 RefreshToken 정보가 없다면 새로 저장")
    void saveRefreshToken(){

        // given
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("qwer!1234")
                .role(Role.MEMBER)
                .provider("google")
                .build();

        Member findMember = memberRepository.save(member);

        JwtDto jwtDto = beforeJwt(findMember);

        // when
        refreshTokenService.saveRefreshToken(jwtDto);

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(jwtDto.getRefreshToken());

        //then
        assertThat(refreshToken.getEmail()).isEqualTo(findMember.getEmail());
        assertThat(refreshToken.getRefreshToken()).isEqualTo(jwtDto.getRefreshToken());
    }

    @Test
    @DisplayName("refreshToken update 성공 : accessToken에서 정보를 조회하고 RefreshToken 정보가 있다면 기존 정보에 업데이트")
    void saveRefreshTokenUpdate(){

        // given
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("qwer!1234")
                .role(Role.MEMBER)
                .provider("google")
                .build();

        Member findMember = memberRepository.save(member);

        JwtDto jwtDto = beforeJwt(findMember);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(jwtDto.getRefreshToken())
                .email(member.getEmail())
                .build();

        refreshTokenRepository.save(refreshToken);


        // when
        refreshTokenService.saveRefreshToken(jwtDto);

        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(jwtDto.getRefreshToken());

        //then
        assertThat(findRefreshToken.getEmail()).isEqualTo(findMember.getEmail());
        assertThat(findRefreshToken.getRefreshToken()).isEqualTo(jwtDto.getRefreshToken());
    }

    @Test
    @DisplayName("refreshToken 저장 실패 : 해당 계정 정보가 저장되지 않았을 경우")
    void saveRefreshTokenMemberNotFound(){

        // given
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1234")
                .role(Role.MEMBER)
                .provider("google")
                .build();

        JwtDto jwtDto = beforeJwt(member);

        // then
        assertThatThrownBy(() -> refreshTokenService.saveRefreshToken(jwtDto))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("해당 계정을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("토큰 재발급 성공 : accessToken이 만료되면 refreshToken을 확인해서 만료 전이라면 accessToken과 refreshToken을 재발급")
    void reissueAccessToken(){

        // given
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("qwer!1234")
                .role(Role.MEMBER)
                .provider("google")
                .build();

        memberRepository.save(member);

        Long accessTime = 3000000L;
        Long refreshTime = 6000000L;

        long now = (new Date()).getTime();

        // AccessToken
        // 현재 시간에서 accessTime을 빼서 과거 시간을 설정
        Date accessTokenExpiresIn = new Date(now - accessTime);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getEmail()))
                .claim("role", member.getRole())
                .setExpiration(accessTokenExpiresIn)
                .signWith(jwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTime))
                .signWith(jwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtDto createJwtDto = JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        RefreshToken saveRefreshToken = RefreshToken.builder()
                .refreshToken(createJwtDto.getRefreshToken())
                .email(member.getEmail())
                .build();

        refreshTokenRepository.save(saveRefreshToken);

        // when
        JwtDto updateJwtDto = refreshTokenService.reissueAccessToken(createJwtDto.getAccessToken());

        // then
        // accessTime 만료로 JwtDto을 새로 발급해서 기본 토큰과 업데이트 된 토큰이 달라야 함.
        assertThat(updateJwtDto.getGrantType()).isEqualTo(createJwtDto.getGrantType());
        assertThat(updateJwtDto.getAccessToken()).isNotEqualTo(createJwtDto.getAccessToken());
        assertThat(updateJwtDto.getRefreshToken()).isNotEqualTo(createJwtDto.getRefreshToken());
    }


    @Test
    @DisplayName("토큰 재발급 실패 : accessToken과 refreshToken 모두 만료로 RefreshToken Entity 정보 삭제")
    void reissueAccessTokenExpiredJwtException() {

        // given
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("qwer!1234")
                .role(Role.MEMBER)
                .provider("google")
                .build();

        memberRepository.save(member);

        Long accessTime = 1L;
        Long refreshTime = 1L;

        long now = (new Date()).getTime();

        // AccessToken
        Date accessTokenExpiresIn = new Date(now - accessTime);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getEmail()))
                .claim("role", member.getRole())
                .setExpiration(accessTokenExpiresIn)
                .signWith(jwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now - refreshTime))
                .signWith(jwtProvider.jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtDto createJwtDto = JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();


        RefreshToken saveRefreshToken = RefreshToken.builder()
                .refreshToken(createJwtDto.getRefreshToken())
                .email(member.getEmail())
                .build();

        refreshTokenRepository.save(saveRefreshToken);

        // when
        refreshTokenService.reissueAccessToken(createJwtDto.getAccessToken());

        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(saveRefreshToken.getRefreshToken());

        //then
        assertThat(findRefreshToken).isNull();
    }



}