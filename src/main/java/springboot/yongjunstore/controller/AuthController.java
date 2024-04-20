package springboot.yongjunstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.common.exceptioncode.ErrorCodeResponse;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.service.AuthService;
import springboot.yongjunstore.request.MemberLoginRequest;
import springboot.yongjunstore.request.SignUpRequest;

@Tag(name = "authController", description = "일반 회원 가입과 로그인 관련 명세를 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "일반 회원 로그인", description = "일반 회원의 로그인을 제공합니다. 로그인 성공 시, 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "비밀번호가 틀렸습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorCodeResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 이메일입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorCodeResponse.class)))
    })
    @PostMapping("/login")
    public JwtDto login(@RequestBody MemberLoginRequest memberLoginDto) {
        JwtDto jwtDto = authService.login(memberLoginDto);
        return jwtDto;
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody SignUpRequest signUpRequest) {

        authService.signup(signUpRequest);

       return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("/admin")
    public String adminLoginTest(){
        return "정상적으로 로그인 됨.";
    }

}
