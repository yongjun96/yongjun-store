package springboot.yongjunstore.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.common.exceptioncode.ErrorCodeResponse;
import springboot.yongjunstore.config.service.RefreshTokenService;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

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

                String accessToken = jwtProvider.resolveToken(request);

                // 만료된 AT를 RT를 이용해서 재발급
                reissueAccessToken(response, accessToken);
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



    private void reissueAccessToken(HttpServletResponse response, String accessToken) throws RuntimeException, IOException {

        JwtDto jwtDto = refreshTokenService.reissueAccessToken(accessToken);

        response.setContentType("application/json;charset=UTF-8");

        if(jwtDto == null){

            response.setStatus(ErrorCode.JWT_REFRESH_EXPIRED_JWT_EXCEPTION.getStatusCode());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), new ErrorCodeResponse(ErrorCode.JWT_REFRESH_EXPIRED_JWT_EXCEPTION));

        }else {

            response.setStatus(HttpStatus.OK.value());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), jwtDto);
            log.info("만료된 accessToken이 refreshToken의 의해 재발급 되었습니다.");
            log.info("accessToken : "+jwtDto.getAccessToken());
        }
    }
}
