package springboot.yongjunstore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.yongjunstore.response.MemberDto;
import springboot.yongjunstore.service.MemberService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/find/{email}")
    public ResponseEntity findMember(@PathVariable("email") String email){

        MemberDto findMember = memberService.findMember(email);

        return ResponseEntity.status(HttpStatus.OK).body(findMember);
    }
}
