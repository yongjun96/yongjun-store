package springboot.yongjunstore.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.yongjunstore.request.PasswordEditRequest;
import springboot.yongjunstore.response.MemberResponse;
import springboot.yongjunstore.response.MyProfileResponse;
import springboot.yongjunstore.service.MemberService;

@SecurityRequirement(name = "JWT")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/find/{email}")
    public ResponseEntity findMember(@PathVariable("email") String email){

        MemberResponse findMember = memberService.findMember(email);

        return ResponseEntity.status(HttpStatus.OK).body(findMember);
    }


    @GetMapping("/find/myProfile/{email}")
    public ResponseEntity myProfileFindMember(@PathVariable("email") String email){

        MyProfileResponse findMember = memberService.myProfileFindMember(email);

        return ResponseEntity.status(HttpStatus.OK).body(findMember);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity deleteMember(@PathVariable("email") String email){

        memberService.deleteMemberAndRoomPostAndImages(email);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/passwordEdit")
    public ResponseEntity passwordEdit(@RequestBody @Valid PasswordEditRequest passwordEditRequest){

        memberService.passwordEdit(passwordEditRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
