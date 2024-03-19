package springboot.yongjunstore.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.common.exceptioncode.ErrorCodeResponse;
import springboot.yongjunstore.config.authservice.CustomOAuth2UserService;
import springboot.yongjunstore.config.authservice.JwtAuthService;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final CustomOAuth2UserService customOAuth2UserService;

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

        // 회원이 존재할 경우
        if (isExist) {

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.OK.value());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), token);

        //회원이 존재하지 않을 경우 DB에 회원가입 시키고 토큰 발급
        } else {

            customOAuth2UserService.googleSignup(oAuth2User);

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.OK.value());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), token);

        }
    }

}