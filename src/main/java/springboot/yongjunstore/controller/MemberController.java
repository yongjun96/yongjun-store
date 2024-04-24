package springboot.yongjunstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.yongjunstore.common.annotation.SwaggerErrorCodes;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.request.PasswordEditRequest;
import springboot.yongjunstore.response.MemberResponse;
import springboot.yongjunstore.response.MyProfileResponse;
import springboot.yongjunstore.service.MemberService;

@SecurityRequirement(name = "JWT")
@Tag(name = "MemberController", description = "회원 정보를 조회, 조작 관련 명세를 제공합니다.")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;


    @Operation(summary = "회원 정보 조회", description = "회원의 정보를 조회하는 기능을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponse.class))
            )
    })
    @SwaggerErrorCodes({
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.SERVER_FORBIDDEN,
            ErrorCode.SERVER_UNAUTHORIZED
    })
    @GetMapping("/find/{email}")
    public ResponseEntity findMember(@PathVariable("email") String email){

        MemberResponse findMember = memberService.findMember(email);

        return ResponseEntity.status(HttpStatus.OK).body(findMember);
    }



    @Operation(summary = "회원 프로필 조회", description = "회원의 프로필를 조회하는 기능을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 프로필 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MyProfileResponse.class))
            )
    })
    @SwaggerErrorCodes({
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.SERVER_FORBIDDEN,
            ErrorCode.SERVER_UNAUTHORIZED
    })
    @GetMapping("/find/myProfile/{email}")
    public ResponseEntity myProfileFindMember(@PathVariable("email") String email){

        MyProfileResponse findMember = memberService.myProfileFindMember(email);

        return ResponseEntity.status(HttpStatus.OK).body(findMember);
    }



    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴하는 기능을 제공합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = @Content)})
    @SwaggerErrorCodes({
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.SERVER_FORBIDDEN,
            ErrorCode.SERVER_UNAUTHORIZED
    })
    @DeleteMapping("/delete/{email}")
    public ResponseEntity deleteMember(@PathVariable("email") String email){

        memberService.deleteMemberAndRoomPostAndImages(email);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "일반 회원 비밀번호 변경", description = "회원의 비밀번호 변경 기능을 제공합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", content = @Content)})
    @SwaggerErrorCodes({
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.MEMBER_PASSWORD_UNCHECKED,
            ErrorCode.SERVER_FORBIDDEN,
            ErrorCode.SERVER_UNAUTHORIZED
    })
    @PatchMapping("/passwordEdit")
    public ResponseEntity passwordEdit(@RequestBody @Valid PasswordEditRequest passwordEditRequest){

        memberService.passwordEdit(passwordEditRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
