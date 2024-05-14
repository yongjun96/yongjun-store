package springboot.yongjunstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.ChatRoomRepository;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.ChatRoomCreateRequest;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class ChatRoomControllerTest {

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    private String email = "test@test.com";
    private Role role = Role.MEMBER;

    @Autowired private MockMvc mockMvc;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        chatRoomRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅방 생성 성공")
    void createChatRoom() throws Exception {

        // given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        ChatRoomCreateRequest chatRoomCreateRequest = new ChatRoomCreateRequest();
        chatRoomCreateRequest.setChatRoomName("테스트 채팅방");


        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/chat-room/create")
                        .content(objectMapper.writeValueAsString(chatRoomCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
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