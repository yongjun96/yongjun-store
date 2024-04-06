package springboot.yongjunstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.RedisUtils;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.AuthCheckRequest;
import springboot.yongjunstore.request.SendEmail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtils redisUtils;
    private final MemberRepository memberRepository;


    // 인증번호 랜덤 생성
    public int createNumber() {
        return (int)(Math.random() * (90000)) + 100000;
    }


    @Transactional
    public void sendMail(SendEmail sendEmail) {

        memberRepository.findByEmail(sendEmail.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        String authNumber = String.valueOf(createNumber());
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom();
            message.setRecipients(MimeMessage.RecipientType.TO, sendEmail.getEmail());
            message.setSubject("비밀번호 변경 인증 메일입니다.");

            String body = "";
            body += "<h3>" + "비밀번호 변경에 필요한 인증번호입니다." + "</h3>";
            body += "<h1>" + authNumber + "</h1>";
            body += "<h3>" + "비밀번호 변경을 위해 입력해 주세요." + "</h3>";


            message.setText(body, "UTF-8", "html");

        }catch (MessagingException e){
            //메일 생성에 실패한 경우
            throw new GlobalException(ErrorCode.GOOGLE_EMAIL_MESSAGE_EXCEPTION);
        }

        javaMailSender.send(message);

        // 유효 시간(5분)동안 email, authNumber 저장
        redisUtils.setDataExpire(sendEmail.getEmail(), authNumber, 60 * 5L);

    }

    @Transactional
    public void authNumCheck(AuthCheckRequest authCheckRequest) {

        if (!isValidAuthNumber(String.valueOf(authCheckRequest.getAuthNumber()))) {
            // 인증 번호가 올바른 형식이 아닌 경우.
            throw new GlobalException(ErrorCode.GOOGLE_INVALID_AUTH_NUMBER_FORMAT);
        }

        String authNum = redisUtils.getData(authCheckRequest.getEmail());

        if (authNum == null) {
            // null인 경우
            throw new GlobalException(ErrorCode.GOOGLE_EMAIL_AUTH_NUMBER_NOT_FOUND);
        }

        // 인증되면 인증번호 제거
        if (authNum.equals(String.valueOf(authCheckRequest.getAuthNumber()))) {

            redisUtils.deleteData(authCheckRequest.getEmail());
        } else {
            // 인증번호가 틀린 경우.
            throw new GlobalException(ErrorCode.GOOGLE_EMAIL_AUTH_NUMBER_ERROR);
        }
    }

    public boolean isValidAuthNumber(String authNumber) {
        // 인증번호가 6자리의 숫자로 이루어진 문자열인지 검증
        return authNumber.matches("\\d{6}");
    }

}
