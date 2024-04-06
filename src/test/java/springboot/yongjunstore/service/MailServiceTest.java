package springboot.yongjunstore.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.RedisUtils;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.AuthCheckRequest;
import springboot.yongjunstore.request.SendEmail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class MailServiceTest {

    private GreenMail greenMail;

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    void setUp() {
        redisUtils.deleteAllKeys();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("메일 발송 성공")
    void sendMail() throws MessagingException {
        // given

        greenMail = new GreenMail(ServerSetup.SMTP);
        greenMail.start();


        Member member = Member.builder()
                .name("김용준")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .email("practice960426@gmail.com")
                .build();

        memberRepository.save(member);


        SendEmail sendEmail = new SendEmail();
        sendEmail.setEmail("practice960426@gmail.com");

        // When
        mailService.sendMail(sendEmail);

        // Then
        assertThat(redisUtils.getData(member.getEmail())).isNotNull();


        // 전송된 메일 확인
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        Assertions.assertThat(receivedMessages.length).isEqualTo(1);
        Assertions.assertThat(receivedMessages[0].getSubject()).isEqualTo("비밀번호 변경 인증 메일입니다.");

        greenMail.stop();
    }

    @Test
    @DisplayName("인증번호 입력 성공")
    void authNumCheck() {

        // given
        int authNumber = (int)(Math.random() * (90000)) + 100000;

        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setEmail("practice960426@gmail.com");
        authCheckRequest.setAuthNumber(authNumber);

        redisUtils.setDataExpire(authCheckRequest.getEmail(), String.valueOf(authNumber), 60 * 5L);


        // when
        mailService.authNumCheck(authCheckRequest);

        // then
        assertThat(redisUtils.getData(authCheckRequest.getEmail())).isNull();
    }


    @Test
    @DisplayName("인증번호 체크 실패 : 인증번호가 틀린 경우")
    void authNumCheckAuthNumberFail() {

        // given
        int authNumber = (int)(Math.random() * (90000)) + 100000;

        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(123456);
        authCheckRequest.setEmail("practice960426@gmail.com");

        redisUtils.setDataExpire(authCheckRequest.getEmail(), String.valueOf(authNumber), 60 *5L);


        // when
        assertThatThrownBy(() -> mailService.authNumCheck(authCheckRequest))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.GOOGLE_EMAIL_AUTH_NUMBER_ERROR.getMessage());
    }


    @Test
    @DisplayName("인증번호 체크 실패 : 인증번호의 형식이 올바르지 않은 경우")
    void authNumCheckAuthNumberFormatFail() {

        // given
        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(9991245);
        authCheckRequest.setEmail("practice960426@gmail.com");

        // when
        assertThatThrownBy(() -> mailService.authNumCheck(authCheckRequest))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.GOOGLE_INVALID_AUTH_NUMBER_FORMAT.getMessage());
    }


    @Test
    @DisplayName("인증번호 체크 실패 : 인증번호가 만료되었거나 발급되지 않은 경우")
    void authNumCheckAuthNumberNotFound() {

        // given
        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(123456);
        authCheckRequest.setEmail("practice960426@gmail.com");

        // when
        assertThatThrownBy(() -> mailService.authNumCheck(authCheckRequest))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.GOOGLE_EMAIL_AUTH_NUMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("인증번호 정규식 형식 체크 성공")
    void isValidAuthNumber(){
        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(123456);

        boolean result = mailService.isValidAuthNumber(String.valueOf(authCheckRequest.getAuthNumber()));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("인증번호 정규식 형식 체크 실패")
    void isValidAuthNumberFail(){
        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(1234);

        boolean result = mailService.isValidAuthNumber(String.valueOf(authCheckRequest.getAuthNumber()));

        assertThat(result).isFalse();
    }


}