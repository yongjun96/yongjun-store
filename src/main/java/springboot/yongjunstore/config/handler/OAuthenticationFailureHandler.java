package springboot.yongjunstore.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.common.exceptioncode.ErrorCodeResponse;

import java.io.IOException;

@Component
@Slf4j
public class OAuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("[인증실패] 인증에 실패했습니다.");

        if (ErrorCode.OAUTH_EMAIL_EXISTS.getMessage().equals(exception.getMessage())) {

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(ErrorCode.OAUTH_EMAIL_EXISTS.getStatusCode());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), new ErrorCodeResponse(ErrorCode.OAUTH_EMAIL_EXISTS));
        }
    }
}
