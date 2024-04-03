package springboot.yongjunstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.request.MemberLoginRequest;
import springboot.yongjunstore.request.PasswordEditRequest;
import springboot.yongjunstore.request.SignUpRequest;
import springboot.yongjunstore.config.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

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

    @PatchMapping("/passwordEdit")
    public ResponseEntity passwordEdit(@RequestBody PasswordEditRequest passwordEditRequest){

        authService.passwordEdit(passwordEditRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/admin")
    public String adminLoginTest(){
        return "정상적으로 로그인 됨.";
    }

}
