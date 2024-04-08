package springboot.yongjunstore.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private MemberRepository memberRepository;

    @BeforeEach
    void setUp(){
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("email로 회원 찾기 성공")
    void findMember() throws Exception {

        // given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        memberRepository.save(member);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/{email}", member.getEmail())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("email로 회원 찾기 실패 : 회원을 찾지 못했을 경우")
    void findMemberNotFound() throws Exception {

        // given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

    }


    @Test
    @DisplayName("email로 회원 프로필 찾기 성공 : 일반 회원")
    void myProfileFindMember() throws Exception {

        // given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        memberRepository.save(member);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/myProfile/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.role").value(String.valueOf(member.getRole())))
                .andDo(print());
    }

    @Test
    @DisplayName("email로 회원 프로필 찾기 성공 : 구글 회원")
    void myProfileFindMemberGoogle() throws Exception {

        // given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .provider("google")
                .providerId("googleId")
                .build();

        memberRepository.save(member);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/myProfile/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.role").value(String.valueOf(member.getRole())))
                .andExpect(jsonPath("$.provider").value(member.getProvider()))
                .andDo(print());
    }

    @Test
    @DisplayName("email로 회원 프로필 찾기 실패 : 회원을 찾지 못했을 경우")
    void myProfileFindMemberNotFound() throws Exception {

        // given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/member/find/myProfile/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteMemberAndRoomPostAndImages() throws Exception {

        // given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        memberRepository.save(member);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/member/delete/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        Assertions.assertThat(memberRepository.count()).isEqualTo(0);
    }


    @Test
    @DisplayName("회원 탈퇴 실패 : 회원이 존재하지 않는 경우")
    void deleteMemberAndRoomPostAndImagesMemberNotFound() throws Exception {

        // given
        Member member = Member.builder()
                .name("김용준")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .email("yongjun@gmail.com")
                .build();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/member/delete/{email}", member.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}