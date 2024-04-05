package springboot.yongjunstore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.RedisUtils;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.request.AuthCheckRequest;
import springboot.yongjunstore.request.SendEmail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.NoSuchElementException;

import static com.mysema.commons.lang.Assert.assertThat;
import static com.mysema.commons.lang.Assert.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@SpringBootTest
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private RedisUtils redisUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
        mailService = new MailService(javaMailSender, redisUtils);
    }

    @Test
    @DisplayName("메일 발송 성공")
    void sendMail() {
        // given
        SendEmail sendEmail = new SendEmail();
        sendEmail.setEmail("test@example.com");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        mailService.sendMail(sendEmail);

        // Then
        verify(javaMailSender).send(mimeMessage);
        verify(redisUtils).setDataExpire(eq(sendEmail.getEmail()), anyString(), eq(60 * 5L));
    }

    @Test
    @DisplayName("인증번호 입력 성공")
    void authNumCheck() {

        // given
        AuthCheckRequest authCheckRequest = mock(AuthCheckRequest.class);
        when(authCheckRequest.getEmail()).thenReturn("test@test.com");
        when(authCheckRequest.getAuthNumber()).thenReturn(123456);

        when(redisUtils.getData(authCheckRequest.getEmail())).thenReturn(String.valueOf(123456));

        // when
        mailService.authNumCheck(authCheckRequest);

        // then
        verify(redisUtils, times(1)).deleteData(any());
    }


    @Test
    @DisplayName("인증번호 체크 실패 : 인증번호가 틀린 경우")
    void authNumCheckAuthNumberFail() {

        // given
        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(123456);
        authCheckRequest.setEmail("test@test.com");

        when(redisUtils.getData(authCheckRequest.getEmail())).thenReturn(String.valueOf(741852));

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
        authCheckRequest.setAuthNumber(999);
        authCheckRequest.setEmail("test@test.com");

        when(redisUtils.getData(authCheckRequest.getEmail())).thenReturn(String.valueOf(741852));

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
        authCheckRequest.setEmail("test@test.com");

        when(redisUtils.getData(authCheckRequest.getEmail())).thenThrow(NoSuchElementException.class);

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