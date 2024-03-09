package springboot.yongjunstore.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

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
                //.collect(Collectors.joining(","));

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

    // 토큰을 복호화 후, 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없습니다.");
        }

        // 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    // 유효성, 만료 일자 검증
    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey()).build().parseClaimsJws(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
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

