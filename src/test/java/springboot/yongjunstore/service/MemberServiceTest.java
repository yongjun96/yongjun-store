package springboot.yongjunstore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.PasswordEditRequest;
import springboot.yongjunstore.response.MemberResponse;
import springboot.yongjunstore.response.MyProfileResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
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
        memberService.passwordEdit(passwordEditRequest);

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
        Assertions.assertThatThrownBy( () -> memberService.passwordEdit(passwordEditRequest))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_PASSWORD_UNCHECKED.getMessage());

    }

}