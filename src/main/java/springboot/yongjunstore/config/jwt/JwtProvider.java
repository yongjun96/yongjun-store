package springboot.yongjunstore.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.UserPrincipal;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final MemberRepository memberRepository;

    public static final long ACCESS_TIME = 3600000L;       // accessToken 1시간
    public static final long REFRESH_TIME = 2592000000L;   // refreshToken 30일

    // application custom secretKey
    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    // secretKeyPlain Base64 변환
    public SecretKey jwtSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public JwtDto generateToken(Authentication authentication) {

        // Authentication 정보
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        long now = (new Date()).getTime();

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + ACCESS_TIME);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TIME))
                .signWith(jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        return JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public JwtDto googleLoginGenerateToken(String email, String role) {

        long now = (new Date()).getTime();

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + ACCESS_TIME);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(email))
                .claim("role", role)
                .setExpiration(accessTokenExpiresIn)
                .signWith(jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TIME))
                .signWith(jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        return JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰을 복호화 후, 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없습니다.");
        }

        // 권한 정보 저장
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Member member = memberRepository.findByEmail((String) claims.get("sub"))
                .orElseThrow(() -> new UsernameNotFoundException("해당 계정을 찾을 수 없습니다."));

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new UserPrincipal(member);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    // 유효성, 만료 일자 검증
    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey()).build().parseClaimsJws(jwtToken);
            return true;
        }

        catch (SignatureException e) {
            log.info("SignatureException : 검증에 실패한 변조된 토큰입니다.");
            throw new JwtException(ErrorCode.JWT_SIGNATURE_EXCEPTION.getMessage());
        }

        catch (MalformedJwtException e) {
            log.info("MalformedJwtException : 잘못된 구조의 지원되지 않는 토큰입니다.");
            throw new JwtException(ErrorCode.JWT_MALFORMED_JWT_EXCEPTION.getMessage());
        }

        catch (ExpiredJwtException e) {
            log.info("ExpiredJwtException : 만료된 토큰입니다.");
            throw new JwtException(ErrorCode.JWT_EXPIRED_JWT_EXCEPTION.getMessage());
        }

        catch (UnsupportedJwtException e) {
            log.info("UnsupportedJwtException : 원하는 토큰과 다른 형식의 토큰입니다.");
            throw new JwtException(ErrorCode.JWT_UNSUPPORTED_JWT_EXCEPTION.getMessage());
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(jwtSecretKey()).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}

