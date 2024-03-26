package springboot.yongjunstore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.repository.RefreshTokenRepository;
import springboot.yongjunstore.request.MemberLoginDto;
import springboot.yongjunstore.request.SignUpDto;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {

        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .name("김용준")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        //expected
        mockMvc.perform(post("/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto))
        )
                .andExpect(status().isOk())
                .andDo(print());

        Member member = memberRepository.findByEmail(signUpDto.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        assertThat(member.getEmail()).isEqualTo(signUpDto.getEmail());
        assertThat(member.getName()).isEqualTo(signUpDto.getName());
        passwordEncoder.matches(signUpDto.getPassword(), member.getPassword());
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

        SignUpDto signUpDto = SignUpDto.builder()
                .name("김용준")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        //expected
        mockMvc.perform(post("/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertThat(saveMember.getEmail()).isEqualTo(signUpDto.getEmail());
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

        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .email(member.getEmail())
                .password("qwer!1234")
                .build();

        //expected
        mockMvc.perform(post("/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberLoginDto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", Matchers.notNullValue()))
                .andExpect(jsonPath("$.grantType", Matchers.notNullValue()))
                .andDo(print());

        assertThat(refreshTokenRepository.findByEmail(member.getEmail()).get().getRefreshToken()).isNotNull();
    }

}