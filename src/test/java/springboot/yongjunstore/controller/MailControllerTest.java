package springboot.yongjunstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import springboot.yongjunstore.config.RedisUtils;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.AuthCheckRequest;
import springboot.yongjunstore.request.SendEmail;

import javax.mail.internet.MimeMessage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class MailControllerTest {


    private GreenMail greenMail;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberRepository memberRepository;
    @Autowired private RedisUtils redisUtils;

    @BeforeEach
    void setUp(){
        redisUtils.deleteAllKeys();
    }

    @Test
    @DisplayName("인증 메일 보내기 성공")
    void signupSuccess() throws Exception {

        greenMail = new GreenMail(ServerSetup.SMTP);
        greenMail.start();

        //given
        Member member = Member.builder()
                .name("김용준")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .email("practice960426@gmail.com")
                .build();

        memberRepository.save(member);

        SendEmail sendEmail = new SendEmail();
        sendEmail.setEmail(member.getEmail());

        //expected
        mockMvc.perform(post("/mailSend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendEmail))
                )
                .andExpect(status().isOk())
                .andDo(print());

        // 전송된 메일 확인
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        Assertions.assertThat(receivedMessages.length).isEqualTo(1);
        Assertions.assertThat(receivedMessages[0].getSubject()).isEqualTo("비밀번호 변경 인증 메일입니다.");

        greenMail.stop();
    }

    @Test
    @DisplayName("인증 메일 보내기 실패 : 회원이 존재하지 않는 경우")
    void signupSuccessMemberNotFound() throws Exception {

        //given
        SendEmail sendEmail = new SendEmail();
        sendEmail.setEmail("yongjun@gmail.com");

        //expected
        mockMvc.perform(post("/mailSend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendEmail))
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("인증 메일 보내기 실패 : 이메일 형식이 맞지 않는 경우")
    void signupSuccessEmailFormFail() throws Exception {

        //given
        SendEmail sendEmail = new SendEmail();
        sendEmail.setEmail("yongjungmail.com");


        //expected
        mockMvc.perform(post("/mailSend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendEmail))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    @DisplayName("인증번호 인증 성공")
    void authNumCheck() throws Exception {

        //given
        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(123456);
        authCheckRequest.setEmail("yongjun@gmail.com");

        redisUtils.setDataExpire("yongjun@gmail.com", "123456", 60 * 5L);

        //expected
        mockMvc.perform(post("/authCheck")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authCheckRequest))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("인증번호 인증 실패 : 올바른 형식이 아닌 경우")
    void authNumCheckNumberFormat() throws Exception {

        //given
        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(1234564532);

        //expected
        mockMvc.perform(post("/authCheck")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authCheckRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    @DisplayName("인증번호 인증 실패 : 인증 번호가 틀린 경우")
    void authNumCheckError() throws Exception {

        //given
        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(123456);
        authCheckRequest.setEmail("yongjun@gmail.com");

        redisUtils.setDataExpire("yongjun@gmail.com", "456789", 60 * 5L);

        //expected
        mockMvc.perform(post("/authCheck")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authCheckRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}