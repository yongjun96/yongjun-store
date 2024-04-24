package springboot.yongjunstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.yongjunstore.common.annotation.SwaggerErrorCodes;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.request.AuthCheckRequest;
import springboot.yongjunstore.request.SendEmail;
import springboot.yongjunstore.service.MailService;

@SecurityRequirement(name = "JWT")
@Tag(name = "MailController", description = "회원 인증을 위한 메일 인증 관련 명세를 제공합니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    // 인증 이메일 전송
    @Operation(summary = "인증 메일 전송", description = "회원의 비밀번호 변경 시 인증 메일 전송을 제공합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "메일 전송 성공", content = @Content))
    @SwaggerErrorCodes({
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.GOOGLE_EMAIL_MESSAGE_EXCEPTION,
            ErrorCode.SERVER_FORBIDDEN,
            ErrorCode.SERVER_UNAUTHORIZED
    })
    @PostMapping("/mailSend")
    public ResponseEntity mailSend(@RequestBody @Valid SendEmail sendEmail) {

        mailService.sendMail(sendEmail);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 인증번호 체크
    @Operation(summary = "인증 번호 체크", description = "전송된 이메일의 인증 번호를 체크하는 기능을 제공합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "인증 번호 체크 성공", content = @Content))
    @SwaggerErrorCodes({
            ErrorCode.GOOGLE_INVALID_AUTH_NUMBER_FORMAT,
            ErrorCode.GOOGLE_EMAIL_AUTH_NUMBER_NOT_FOUND,
            ErrorCode.GOOGLE_EMAIL_AUTH_NUMBER_ERROR,
            ErrorCode.SERVER_FORBIDDEN,
            ErrorCode.SERVER_UNAUTHORIZED
    })
    @PostMapping("/authNumCheck")
    public ResponseEntity authNumCheck(@RequestBody AuthCheckRequest authCheckRequest){

        mailService.authNumCheck(authCheckRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
