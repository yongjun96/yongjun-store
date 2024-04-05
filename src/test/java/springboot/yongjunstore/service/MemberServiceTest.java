package springboot.yongjunstore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.CustomOAuth2User;
import springboot.yongjunstore.response.MemberResponse;
import springboot.yongjunstore.response.MyProfileResponse;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
class MemberServiceTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberService memberService;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
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
        memberService.googleSignup(oAuth2User);

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
        Assertions.assertThatThrownBy(() -> memberService.googleSignup(oAuth2User))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_EMAIL_EXISTS.getMessage());

    }


    @Test
    @DisplayName("회원 찾기 성공")
    void findMember() {

        // given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);


        // when
        MemberResponse memberResponse = memberService.findMember(member.getEmail());


        // then
        assertThat(member.getEmail()).isEqualTo(memberResponse.getEmail());
        assertThat(member.getId()).isEqualTo(memberResponse.getId());
        assertThat(member.getName()).isEqualTo(memberResponse.getName());
    }

    @Test
    @DisplayName("회원 찾기 실패 : 존재하지 않는 회원")
    void findMemberFail() {

        // given
        String email = "testEmail@test.com";

        // when
        Assertions.assertThatThrownBy(() -> memberService.findMember(email))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());

    }

    //MyProfileResponse

    @Test
    @DisplayName("회원 프로필 찾기 실패 : 존재하지 않는 회원")
    void myProfileResponse() {

        // given
        String email = "testEmail@test.com";

        // when
        Assertions.assertThatThrownBy(() -> memberService.myProfileFindMember(email))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("회원 프로필 찾기 성공")
    void myProfileResponseFail() {

        // given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);

        // when
        MyProfileResponse myProfileResponse = memberService.myProfileFindMember(member.getEmail());

        // then
        assertThat(myProfileResponse.getEmail()).isEqualTo(member.getEmail());


    }



    @Test
    @DisplayName("회원 삭제 성공")
    void deleteMemberAndRoomPostAndImages() {

        // given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        memberRepository.save(member);

        // when
        memberService.deleteMemberAndRoomPostAndImages(member.getEmail());

        // then
        assertThat(memberRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("회원 삭제 실패 : 회원을 찾지 못한 경우")
    void deleteMemberAndRoomPostAndImagesNotFound() {

        // given
        String email = "testEmail@test.com";

        // when
        Assertions.assertThatThrownBy(() -> memberService.deleteMemberAndRoomPostAndImages(email))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("회원 삭제 실패 : 회원을 삭제하지 못한 경우")
    void deleteMemberAndRoomPostAndImagesFail() {

        // given
        String email = "testEmail@test.com";

        assertThrows(GlobalException.class, () -> memberService.deleteMemberAndRoomPostAndImages(email));

        // when
        Assertions.assertThatThrownBy(() -> memberService.deleteMemberAndRoomPostAndImages(email))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
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