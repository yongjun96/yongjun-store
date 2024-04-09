package springboot.yongjunstore.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;

import javax.crypto.SecretKey;
import javax.lang.model.type.NullType;
import java.util.Base64;
import java.util.Date;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
class RestDocsMemberControllerTest {

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    private String email = "test@test.com";
    private Role role = Role.MEMBER;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;


    @AfterEach
    void afterSetUp(){
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("email로 회원 찾기 성공")
    void findMember() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();


        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/member/find/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("회원 찾기 성공",
                                resourceDetails().description("회원 찾기"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰").getAttributes()),
                                //requestFields(fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("ID"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                )
                        )

                );

    }


    @Test
    @DisplayName("email로 회원 찾기 실패 : 회원을 찾지 못했을 경우")
    void findMemberNotFound() throws Exception {

        // given
        createMember(email, role);

        JwtDto jwt = jwtDto();


        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/member/find/{email}", "test@test.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("회원이 없을 경우",
                                resourceDetails().description("회원 찾기"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰").getAttributes()),
                                responseFields(
                                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Http코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("에러코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러메시지")
                                )
                        )

                );

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
        mockMvc.perform(RestDocumentationRequestBuilders.get("/member/find/myProfile/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.role").value(String.valueOf(member.getRole())))
                .andExpect(jsonPath("$.provider").value(member.getProvider()))
                .andExpect(jsonPath("$.providerId").value(member.getProviderId()))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("프로필 찾기 성공 (구글 회원)",
                                resourceDetails().description("회원 프로필 찾기"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰").getAttributes()),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                        fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한"),
                                        fieldWithPath("provider").type(JsonFieldType.STRING).description("구글 회원 확인"),
                                        fieldWithPath("providerId").type(JsonFieldType.STRING).description("구글 회원 ID")
                                )
                        )

                );
    }


    @Test
    @DisplayName("email로 회원 프로필 찾기 성공 : 일반 회원")
    void myProfileFindMember() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/member/find/myProfile/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.role").value(String.valueOf(member.getRole())))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("프로필 찾기 성공 (일반 회원)",
                        resourceDetails().description("회원 프로필 찾기"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰").getAttributes()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한"),
                                fieldWithPath("provider").type(JsonFieldType.NULL).description("구글 회원 확인"),
                                fieldWithPath("providerId").type(JsonFieldType.NULL).description("구글 회원 ID")
                        )
                )

        );
    }


    @Test
    @DisplayName("email로 회원 프로필 찾기 실패 : 회원을 찾지 못했을 경우")
    void myProfileFindMemberNotFound() throws Exception {

        // given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/member/find/myProfile/{email}", "test@test.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("프로필 찾기 실패",
                                resourceDetails().description("회원 프로필 찾기"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰").getAttributes()),
                                responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Http코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태코드"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("에러코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러메시지")
                                )
                        )

                );
    }


    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteMemberAndRoomPostAndImages() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/member/delete/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("회원 탈퇴 성공",
                                resourceDetails().description("회원 탈퇴"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰").getAttributes())
                        )

                );

        Assertions.assertThat(memberRepository.count()).isEqualTo(0);
    }


    @Test
    @DisplayName("회원 탈퇴 실패 : 회원이 존재하지 않는 경우")
    void deleteMemberAndRoomPostAndImagesMemberNotFound() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/member/delete/{email}", "test@test.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("회원 탈퇴 실패",
                                resourceDetails().description("회원 탈퇴"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰").getAttributes()),
                                responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Http코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태코드"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("에러코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러메시지")
                                )
                        )

                );
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