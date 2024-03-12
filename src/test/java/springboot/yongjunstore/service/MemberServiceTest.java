package springboot.yongjunstore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.MemberLoginDto;
import springboot.yongjunstore.request.SignUpDto;

@SpringBootTest
class MemberServiceTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberService memberService;

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtProvider jwtProvider;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @AfterEach
    public void afterEach() {
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("로그인 jwt 발급 성공 테스트")
    void loginSuccess() {

        //given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);

        MemberService memberService = new MemberService(authenticationManager, jwtProvider, memberRepository, passwordEncoder);

        MemberLoginDto loginDto = MemberLoginDto.builder()
                .email("yongjun@gmail.com")
                .password("1234")
                .build();

        //when
        JwtDto result = memberService.login(loginDto);

        //then
        Assertions.assertThat(result.getAccessToken()).isNotNull();
        Assertions.assertThat(result.getRefreshToken()).isNotNull();
        Assertions.assertThat(result.getGrantType()).isNotNull();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {

        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .email("yongjun@gmail.com")
                .password("1234")
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        //when
        memberService.signup(signUpDto);

        Member findMember = memberRepository.findByEmail("yongjun@gmail.com")
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        //then
        Assertions.assertThat(signUpDto.getEmail()).isEqualTo(findMember.getEmail());
        passwordEncoder.matches(signUpDto.getPassword(), findMember.getPassword());
        Assertions.assertThat(signUpDto.getRole()).isEqualTo(findMember.getRole());
        Assertions.assertThat(signUpDto.getName()).isEqualTo(findMember.getName());
    }

}