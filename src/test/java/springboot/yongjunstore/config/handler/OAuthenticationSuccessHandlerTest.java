package springboot.yongjunstore.config.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.CustomOAuth2User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OAuthenticationSuccessHandlerTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private OAuthenticationSuccessHandler oAuthenticationSuccessHandler;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() {
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("구글 회원가입 성공")
    void googleSignup() {

        // given
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_MEMBER");

        CustomOAuth2User customOAuth2User = getCustomOAuth2User(authority, "yongjun@email.com");


        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")),
                customOAuth2User.getAttributes(), "email");


        // when
        oAuthenticationSuccessHandler.googleSignup(oAuth2User);

        String email = memberRepository.findByEmail(oAuth2User.getAttribute("email")).get().getEmail();


        // then
        assertThat(memberRepository.count()).isEqualTo(1);
        assertThat(email).isEqualTo(oAuth2User.getAttribute("email"));

    }


    @Test
    @DisplayName("구글 회원가입 실패 : 회원이 존재하는 경우")
    void googleSignupFail() {

        // given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_MEMBER");

        CustomOAuth2User customOAuth2User = getCustomOAuth2User(authority, "yongjun@email.com");


        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")),
                customOAuth2User.getAttributes(), "email");


        // when
        Assertions.assertThatThrownBy(() -> oAuthenticationSuccessHandler.googleSignup(oAuth2User))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_EMAIL_EXISTS.getMessage());

    }



    private static CustomOAuth2User getCustomOAuth2User(GrantedAuthority authority, String email) {

        OAuth2User defaultoAuth2User = new DefaultOAuth2User(
                Collections.singleton(authority), // 권한 목록
                Collections.singletonMap("email", email), // 사용자 정보
                "email"
        );

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(defaultoAuth2User);
        customOAuth2User.addAttribute("email", "yongjun@gmail.com");
        customOAuth2User.addAttribute("provider", "google");
        customOAuth2User.addAttribute("name", "김용준");
        customOAuth2User.addAttribute("sub", "subTest");
        return customOAuth2User;
    }

}