package springboot.yongjunstore.config.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.MemberLoginRequest;
import springboot.yongjunstore.request.PasswordEditRequest;
import springboot.yongjunstore.request.SignUpRequest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class AuthServiceTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private AuthService authService;


    @BeforeEach
    public void beforeEach() {
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("로그인 성공 : jwt 발급")
    void loginSuccess() {

        //given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);


        MemberLoginRequest loginDto = MemberLoginRequest.builder()
                .email("yongjun@gmail.com")
                .password("qwer!1234")
                .build();


        //when
        JwtDto result = authService.login(loginDto);

        //then
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
        assertThat(result.getGrantType()).isNotNull();
    }

    @Test
    @DisplayName("로그인 실패 : 존재하지 않는 email")
    void emailLoginFail() {

        //given

        MemberLoginRequest loginDto = MemberLoginRequest.builder()
                .email("yongjun@gmail.com")
                .password("1234")
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_EMAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 : 비밀번호 오류")
    void passwordLoginFail() {

        //given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);


        MemberLoginRequest loginDto = MemberLoginRequest.builder()
                .email("yongjun@gmail.com")
                .password("12345")
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_PASSWORD_ERROR.getMessage());
    }


    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {

        //given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("yongjun@gmail.com")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        //when
        authService.signup(signUpRequest);

        Member findMember = memberRepository.findByEmail("yongjun@gmail.com")
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        //then
        assertThat(signUpRequest.getEmail()).isEqualTo(findMember.getEmail());
        passwordEncoder.matches(signUpRequest.getPassword(), findMember.getPassword());
        assertThat(signUpRequest.getRole()).isEqualTo(findMember.getRole());
        assertThat(signUpRequest.getName()).isEqualTo(findMember.getName());
    }

    @Test
    @DisplayName("회원가입 실패 : 이미 존재하는 이메일입니다.")
    void signupFail() {

        //given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("yongjun@gmail.com")
                .password("1234")
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        //when
        Assertions.assertThatThrownBy( () -> authService.signup(signUpRequest))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_EMAIL_EXISTS.getMessage());
    }

    @Transactional
    @Rollback // 테스트 완료 후 롤백 지정
    @Test
    @DisplayName("패스워드 변경 성공 : 패스워드 체크 일치")
    void passwordEdit() {

        //given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);

        PasswordEditRequest passwordEditRequest = PasswordEditRequest.builder()
                .email("yongjun@gmail.com")
                .password("asdf!1234")
                .passwordCheck("asdf!1234")
                .build();


        // when
        authService.passwordEdit(passwordEditRequest);

        //then
        Member findMember = memberRepository.findByEmail("yongjun@gmail.com")
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        assertThat(findMember.getPassword()).isNotNull();

    }

    @Transactional
    @Rollback // 테스트 완료 후 롤백 지정
    @Test
    @DisplayName("패스워드 변경 실패 : 패스워드 체크 불일치")
    void passwordEditUnChecked() {

        //given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password("qwer!1234")
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);

        PasswordEditRequest passwordEditRequest = PasswordEditRequest.builder()
                .email("yongjun@gmail.com")
                .password("asdf!1235")
                .passwordCheck("asdf!1234")
                .build();


        // when
        Assertions.assertThatThrownBy( () -> authService.passwordEdit(passwordEditRequest))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_PASSWORD_UNCHECKED.getMessage());

    }

}