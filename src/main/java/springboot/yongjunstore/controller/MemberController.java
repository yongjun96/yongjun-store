package springboot.yongjunstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.request.MemberLoginDto;
import springboot.yongjunstore.request.SignUpDto;
import springboot.yongjunstore.response.ErrorResponse;
import springboot.yongjunstore.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public JwtDto login(@RequestBody MemberLoginDto memberLoginDto) {
        JwtDto jwtDto = memberService.login(memberLoginDto);
        return jwtDto;
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody SignUpDto signUpDto) {

        memberService.signup(signUpDto);

       return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/admin")
    public String adminLoginTest(){
        return "정상적으로 로그인 됨.";
    }

    @PostMapping("/test")
    public ResponseEntity<ErrorResponse> exceptionTest() throws GlobalException{
        throw new GlobalException(ErrorCode.MEMBER_EMAIL_ALREAD_EXISTS);
    }

}
