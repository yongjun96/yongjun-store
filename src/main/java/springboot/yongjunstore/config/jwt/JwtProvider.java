package springboot.yongjunstore.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.UserPrincipal;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.repository.MemberRepository;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final MemberRepository memberRepository;

    //public static final long ACCESS_TIME = 3600000L;       // accessToken 1시간
    //public static final long REFRESH_TIME = 2592000000L;   // refreshToken 30일

    public static final long ACCESS_TIME = 3000000L;       // accessToken 1시간
    public static final long REFRESH_TIME = 6000000L;   // refreshToken 30일

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
    public JwtDto googleLoginGenerateToken(String email, String role, OAuth2User oAuth2User) {

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


        if(member.getProvider() != null && member.getProvider().equals("google")){

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("email", member.getEmail()); // 예시: 사용자의 이메일을 추가 정보로 설정

            return new OAuth2AuthenticationToken(
                    new DefaultOAuth2User(authorities, attributes, "email"),
                    authorities,
                    "google"
            );

        }else {

            // UserDetails 객체를 만들어서 Authentication 리턴
            UserDetails principal = new UserPrincipal(member);
            return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        }
    }

    // Request Header 에서 토큰 정보 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    // 유효성, 만료 일자 검증
    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey()).build().parseClaimsJws(jwtToken);
            return true;
        }

        catch (SignatureException e) {
            log.info("SignatureException : 검증에 실패한 변조된 accessToken입니다.");
            throw new JwtException(ErrorCode.JWT_SIGNATURE_EXCEPTION.getMessage());
        }

        catch (MalformedJwtException e) {
            log.info("MalformedJwtException : 잘못된 구조의 지원되지 않는 accessToken입니다.");
            throw new JwtException(ErrorCode.JWT_MALFORMED_JWT_EXCEPTION.getMessage());
        }

        catch (ExpiredJwtException e) {
            log.info("ExpiredJwtException : 만료된 accessToken입니다.");
            throw new JwtException(ErrorCode.JWT_EXPIRED_JWT_EXCEPTION.getMessage());
        }

        catch (UnsupportedJwtException e) {
            log.info("UnsupportedJwtException : 원하는 accessToken과 다른 형식의 accessToken입니다.");
            throw new JwtException(ErrorCode.JWT_UNSUPPORTED_JWT_EXCEPTION.getMessage());
        }
    }


    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey()).build().parseClaimsJws(refreshToken);
            return true;
        }
        catch (ExpiredJwtException e) {
            log.info("ExpiredJwtException : 만료된 refreshToken입니다.");
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

