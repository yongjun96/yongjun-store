package springboot.yongjunstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.repository.RefreshTokenRepository;
import springboot.yongjunstore.request.MemberLoginRequest;
import springboot.yongjunstore.request.PasswordEditRequest;
import springboot.yongjunstore.request.SignUpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void delete(){
        refreshTokenRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {

        //given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .name("김용준")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        //expected
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
                .andExpect(status().isOk())
                .andDo(print());

        Member member = memberRepository.findByEmail(signUpRequest.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        assertThat(member.getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(member.getName()).isEqualTo(signUpRequest.getName());
        passwordEncoder.matches(signUpRequest.getPassword(), member.getPassword());
    }

    @Test
    @DisplayName("회윈가입 실패 : 이메일 중복")
    void signupEmailFail() throws Exception {

        //given

        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        Member saveMember = memberRepository.save(member);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .name("김용준")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        //expected
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertThat(saveMember.getEmail()).isEqualTo(signUpRequest.getEmail());
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {

        //given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        memberRepository.save(member);

        MemberLoginRequest memberLoginDto = MemberLoginRequest.builder()
                .email(member.getEmail())
                .password("qwer!1234")
                .build();

        //expected
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberLoginDto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", Matchers.notNullValue()))
                .andExpect(jsonPath("$.grantType", Matchers.notNullValue()))
                .andDo(print());

        assertThat(refreshTokenRepository.findByMember(member).get().getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("로그인 실패 : 비밀번호가 틀린 경우")
    void loginSuccessFail() throws Exception {

        //given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        memberRepository.save(member);

        MemberLoginRequest memberLoginDto = MemberLoginRequest.builder()
                .email(member.getEmail())
                .password("qwer!4567")
                .build();

        //expected
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberLoginDto))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertThat(refreshTokenRepository.count()).isEqualTo(0);
    }


    @Test
    @DisplayName("비밀번호 체크 성공")
    void passwordEdit() throws Exception {

        //given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        memberRepository.save(member);

        PasswordEditRequest passwordEditRequest = PasswordEditRequest.builder()
                .email("yongjun@gmail.com")
                .passwordCheck("qwer!1234")
                .password("qwer!1234")
                .build();

        //expected
        mockMvc.perform(patch("/auth/passwordEdit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordEditRequest)
                ))
                .andExpect(status().isOk())
                .andDo(print());

        assertThat(refreshTokenRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("비밀번호 체크 실패 : 회원이 존재하지 않는 경우")
    void passwordEditMemberNotFound() throws Exception {

        //given
        PasswordEditRequest passwordEditRequest = PasswordEditRequest.builder()
                .email("yongjun@gmail.com")
                .passwordCheck("qwer!1234")
                .password("qwer!1234")
                .build();

        //expected
        mockMvc.perform(patch("/auth/passwordEdit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordEditRequest)
                        ))
                .andExpect(status().isNotFound())
                .andDo(print());

        assertThat(refreshTokenRepository.count()).isEqualTo(0);
    }


    @Test
    @DisplayName("비밀번호 체크 실패 : 비밀번호가 일치하지 않는 경우")
    void passwordEditUnChecked() throws Exception {

        //given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        memberRepository.save(member);

        PasswordEditRequest passwordEditRequest = PasswordEditRequest.builder()
                .email("yongjun@gmail.com")
                .passwordCheck("qwer!1234")
                .password("qwer!4567")
                .build();

        //expected
        mockMvc.perform(patch("/auth/passwordEdit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordEditRequest)
                        ))
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertThat(refreshTokenRepository.count()).isEqualTo(0);
    }
}