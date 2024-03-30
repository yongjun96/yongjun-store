package springboot.yongjunstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.response.MemberResponse;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void googleSignup(OAuth2User oAuth2User) {

        // ex). MEMBER
        String role = oAuth2User.getAuthorities().stream().
                findFirst() // 첫번째 Role을 찾아온다.
                .orElseThrow(IllegalAccessError::new) // 존재하지 않을 시 예외를 던진다.
                .toString().substring(5).trim(); // ROLE_ 부분 자르고 가져온다.


        Optional<Member> optionalUser = memberRepository.findByEmail(oAuth2User.getAttribute("email"));

        if(optionalUser.isPresent()){
            throw new GlobalException(ErrorCode.MEMBER_EMAIL_EXISTS);
        }

        Member member = Member.builder()
                .name(oAuth2User.getAttribute("name"))
                .email(oAuth2User.getAttribute("email"))
                .provider(oAuth2User.getAttribute("provider")) // ex). google
                .providerId(oAuth2User.getAttribute("sub"))
                .role(Role.valueOf(role))
                .build();

        memberRepository.save(member);
    }

    public MemberResponse findMember(String email){

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }

}
