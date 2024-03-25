package springboot.yongjunstore.config.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

        Long accessTime = 3000000L;       // accessToken 1시간
        Long refreshTime = 6000000L;   // refreshToken 30일

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
                .password("1234")
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
                .password("1234")
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



}