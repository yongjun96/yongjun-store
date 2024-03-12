package springboot.yongjunstore.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.common.exceptioncode.ErrorCodeResponse;
import java.io.IOException;

public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (JwtException e) {

            String message = e.getMessage();

            // 원하는 토큰과 다른 형식의 토큰
            if(ErrorCode.JWT_UNSUPPORTED_JWT_EXCEPTION.getMessage().equals(message)) {
                setResponse(response, ErrorCode.JWT_UNSUPPORTED_JWT_EXCEPTION);
            }

            // 잘못된 구조의 지원되지 않는 토큰
            else if(ErrorCode.JWT_MALFORMED_JWT_EXCEPTION.getMessage().equals(message)) {
                setResponse(response, ErrorCode.JWT_MALFORMED_JWT_EXCEPTION);
            }

            // 만료된 토큰
            else if(ErrorCode.JWT_EXPIRED_JWT_EXCEPTION.getMessage().equals(message)) {
                setResponse(response, ErrorCode.JWT_EXPIRED_JWT_EXCEPTION);
            }

            // 검증에 실패한 변조된 토큰
            else if(ErrorCode.JWT_SIGNATURE_EXCEPTION.getMessage().equals(message)) {
                setResponse(response, ErrorCode.JWT_SIGNATURE_EXCEPTION);
            }
        }
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws RuntimeException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), new ErrorCodeResponse(errorCode));
    }

}
