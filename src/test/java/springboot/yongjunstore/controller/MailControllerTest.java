package springboot.yongjunstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import springboot.yongjunstore.config.RedisUtils;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.AuthCheckRequest;
import springboot.yongjunstore.request.SendEmail;

import javax.crypto.SecretKey;
import javax.mail.internet.MimeMessage;
import java.util.Base64;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class MailControllerTest {

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    private String email = "practice960426@gmail.com";
    private Role role = Role.MEMBER;

    private GreenMail greenMail;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberRepository memberRepository;
    @Autowired private RedisUtils redisUtils;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        memberRepository.deleteAll();
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
                .email(email)
                .build();

        memberRepository.save(member);

        JwtDto jwt = jwtDto();

        SendEmail sendEmail = new SendEmail();
        sendEmail.setEmail(member.getEmail());

        //expected
        mockMvc.perform(post("/mail/mail-send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken())
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

        createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(post("/mail/mail-send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken())
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

        createMember(email, role);

        JwtDto jwt = jwtDto();


        //expected
        mockMvc.perform(post("/mail/mail-send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken())
                        .content(objectMapper.writeValueAsString(sendEmail))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    @DisplayName("인증번호 인증 성공")
    void authNumCheck() throws Exception {

        //given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(123456);
        authCheckRequest.setEmail(email);

        redisUtils.setDataExpire(email, "123456", 60 * 5L);

        //expected
        mockMvc.perform(post("/mail/auth-num-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken())
                        .content(objectMapper.writeValueAsString(authCheckRequest))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("인증번호 인증 실패 : 올바른 형식이 아닌 경우")
    void authNumCheckNumberFormat() throws Exception {

        //given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(1234564532);

        //expected
        mockMvc.perform(post("/mail/auth-num-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken())
                        .content(objectMapper.writeValueAsString(authCheckRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    @DisplayName("인증번호 인증 실패 : 인증 번호가 틀린 경우")
    void authNumCheckError() throws Exception {

        //given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        AuthCheckRequest authCheckRequest = new AuthCheckRequest();
        authCheckRequest.setAuthNumber(123456);
        authCheckRequest.setEmail(email);

        redisUtils.setDataExpire(email, "456789", 60 * 5L);

        //expected
        mockMvc.perform(post("/mail/auth-num-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken())
                        .content(objectMapper.writeValueAsString(authCheckRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    private Member createMember(String email, Role role){

        Member member = Member.builder()
                .name("홍길동")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(role)
                .email(email)
                .build();

        return memberRepository.save(member);
    }


    private JwtDto jwtDto(){

        long accessTime = 3600000L;       // accessToken 1시간
        long refreshTime = 2592000000L;   // refreshToken 30일


        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());

        long now = (new Date()).getTime();

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + accessTime);

        String accessToken = Jwts.builder()
                .setSubject(email)
                .claim("role", "ROLE_"+role)
                .setExpiration(accessTokenExpiresIn)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}