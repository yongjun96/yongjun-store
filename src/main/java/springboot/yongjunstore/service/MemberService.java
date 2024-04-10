package springboot.yongjunstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.PasswordEditRequest;
import springboot.yongjunstore.response.MemberResponse;
import springboot.yongjunstore.response.MyProfileResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public MemberResponse findMember(String email){

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }

    public MyProfileResponse myProfileFindMember(String email){

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        return new MyProfileResponse(member);
    }

    @Transactional
    public void deleteMemberAndRoomPostAndImages(String email) {

        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(findMember);
    }


    @Transactional
    public void passwordEdit(PasswordEditRequest passwordEditRequest) {

        Member findMember = memberRepository.findByEmail(passwordEditRequest.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        if(passwordEditRequest.getPasswordCheck()
                .equals(passwordEditRequest.getPassword())){

            String encodePassword = passwordEncoder.encode(passwordEditRequest.getPassword());

            memberRepository.updateMemberPassword(findMember.getEmail(), encodePassword);
        }else {

            // 비밀번호가 일치하지 않는 경우.
            throw new GlobalException(ErrorCode.MEMBER_PASSWORD_UNCHECKED);
        }
    }
}
