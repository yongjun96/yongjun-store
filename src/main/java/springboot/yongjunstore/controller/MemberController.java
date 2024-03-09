package springboot.yongjunstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.request.MemberLoginDto;
import springboot.yongjunstore.request.SignUpDto;
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
    public void signup(@RequestBody SignUpDto signUpDto) throws Exception {
        memberService.signup(signUpDto);
    }

    @PostMapping("/member/admin")
    public String adminLoginTest(){
        return "정상적으로 로그인 됨.";
    }

}
