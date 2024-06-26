package springboot.yongjunstore.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.config.service.RefreshTokenService;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class OAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final String frontEndUrl;
    private final MemberRepository memberRepository;


    public OAuthenticationSuccessHandler(@Value("${custom.url.frontend-url}") String frontEndUrl,
                                         JwtProvider jwtProvider,
                                         MemberRepository memberRepository,
                                         RefreshTokenService refreshTokenService) {

        this.memberRepository = memberRepository;
        this.frontEndUrl = frontEndUrl;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 사용자 이메일을 가져온다.
        String email = oAuth2User.getAttribute("email");

        // CustomOAuth2UserService에서 로그인한 회원 존재 여부를 가져온다.
        boolean isExist = oAuth2User.getAttribute("exist");

        // OAuth2User로 부터 Role을 얻어온다.
        String role = oAuth2User.getAuthorities().stream().
                findFirst() // 첫번째 Role을 찾아온다.
                .orElseThrow(IllegalAccessError::new) // 존재하지 않을 시 예외를 던진다.
                .getAuthority(); // Role을 가져온다.


        // jwt token 발행을 시작한다.
        JwtDto token = jwtProvider.googleLoginGenerateToken(email, role);
        log.info("jwtToken = {}", token.getAccessToken());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());

        //회원이 존재하지 않을 경우 DB에 회원가입 시키고 토큰 발급
        if (!isExist) {

            googleSignup(oAuth2User);
        }

        // refreshToken 저장
        refreshTokenService.saveRefreshToken(token);

        // 클라이언트로 리디렉션하여 토큰 정보를 전달
        response.sendRedirect(frontEndUrl+"?accessToken=" + token.getAccessToken());
    }


    @Transactional
    public void googleSignup(OAuth2User oAuth2User) {

        // ex). MEMBER
        String role = oAuth2User.getAuthorities().stream().
                findFirst() // 첫번째 Role을 찾아온다.
                .orElseThrow(IllegalAccessError::new) // 존재하지 않을 시 예외를 던진다.
                .toString().substring(5).trim(); // ROLE_ 부분 자르고 가져온다.


        Optional<Member> optionalUser = memberRepository.findByEmail(oAuth2User.getAttribute("email"));

        if(optionalUser.isPresent()){
            throw new GlobalException(ErrorCode.MEMBER_EMAIL_EXISTS);
        }

        Member member = Member.builder()
                .name(oAuth2User.getAttribute("name"))
                .email(oAuth2User.getAttribute("email"))
                .provider(oAuth2User.getAttribute("provider")) // ex). google
                .providerId(oAuth2User.getAttribute("sub"))
                .role(Role.valueOf(role))
                .build();

        memberRepository.save(member);
    }

}
