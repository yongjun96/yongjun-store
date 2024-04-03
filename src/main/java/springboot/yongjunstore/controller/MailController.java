package springboot.yongjunstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.yongjunstore.request.AuthCheckRequest;
import springboot.yongjunstore.request.SendEmail;
import springboot.yongjunstore.service.MailService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailService mailService;

    // 인증 이메일 전송
    @PostMapping("/mailSend")
    public ResponseEntity mailSend(@RequestBody @Valid SendEmail sendEmail) {

        mailService.sendMail(sendEmail);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 인증번호 체크
    @PostMapping("/authCheck")
    public ResponseEntity authNumCheck(@RequestBody AuthCheckRequest authCheckRequest){

        mailService.authNumCheck(authCheckRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
