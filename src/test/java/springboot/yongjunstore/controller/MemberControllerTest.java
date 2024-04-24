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
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.PasswordEditRequest;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    private String email = "test@test.com";
    private Role role = Role.MEMBER;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;


    @BeforeEach
    void beforeSetUp(){
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("email로 회원 찾기 성공")
    void findMember() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();


        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());

    }


    @Test
    @DisplayName("email로 회원 찾기 실패 : 회원을 찾지 못했을 경우")
    void findMemberNotFound() throws Exception {

        // given
        createMember(email, role);

        JwtDto jwt = jwtDto();


        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/{email}", "test@test.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isNotFound())
                .andDo(print());

    }


    @Test
    @DisplayName("email로 회원 프로필 찾기 성공 : 구글 회원")
    void myProfileFindMemberGoogle() throws Exception {

        // given
        Member member = Member.builder()
                .name("홍길동")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("test@test.com")
                .provider("google")
                .providerId("googleId")
                .build();

        memberRepository.save(member);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/myProfile/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.role").value(String.valueOf(member.getRole())))
                .andExpect(jsonPath("$.provider").value(member.getProvider()))
                .andExpect(jsonPath("$.providerId").value(member.getProviderId()))
                .andDo(print());
    }


    @Test
    @DisplayName("email로 회원 프로필 찾기 성공 : 일반 회원")
    void myProfileFindMember() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/myProfile/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.role").value(String.valueOf(member.getRole())))
                .andDo(print());
    }


    @Test
    @DisplayName("email로 회원 프로필 찾기 실패 : 회원을 찾지 못했을 경우")
    void myProfileFindMemberNotFound() throws Exception {

        // given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/myProfile/{email}", "test@test.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteMemberAndRoomPostAndImages() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/member/delete/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("회원 탈퇴 실패 : 회원이 존재하지 않는 경우")
    void deleteMemberAndRoomPostAndImagesMemberNotFound() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/member/delete/{email}", "test@test.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    @DisplayName("비밀번호 변경 성공 : 패스워드 체크 일치")
    void passwordEdit() throws Exception {

        // given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        PasswordEditRequest passwordEditRequest = PasswordEditRequest.builder()
                .email(email)
                .password("asdf!1234")
                .passwordCheck("asdf!1234")
                .build();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/member/passwordEdit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken())
                        .content(objectMapper.writeValueAsString(passwordEditRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("비밀번호 변경 실패 : 패스워드 불일치")
    void passwordEditUnChecked() throws Exception {

        // given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        PasswordEditRequest passwordEditRequest = PasswordEditRequest.builder()
                .email(email)
                .password("asdf!1234")
                .passwordCheck("zxcv!1234")
                .build();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/member/passwordEdit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken())
                        .content(objectMapper.writeValueAsString(passwordEditRequest)))
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