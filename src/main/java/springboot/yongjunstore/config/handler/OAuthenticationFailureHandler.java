package springboot.yongjunstore.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;

import java.io.IOException;

@Component
@Slf4j
public class OAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final String frontEndUrl;

    public OAuthenticationFailureHandler(@Value("${custom.url.frontend-url}") String frontEndUrl) {
        this.frontEndUrl = frontEndUrl;
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("[인증실패] 인증에 실패했습니다.");

        if (ErrorCode.OAUTH_EMAIL_EXISTS.getMessage().equals(exception.getMessage())) {

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(ErrorCode.OAUTH_EMAIL_EXISTS.getStatusCode());

            response.sendRedirect(frontEndUrl+"?errorMessage=OAUTH_EMAIL_EXISTS");
        }
    }
}
