package springboot.yongjunstore.config.authservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.MemberLoginDto;
import springboot.yongjunstore.request.SignUpDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public JwtDto login(MemberLoginDto memberLoginDto) {

        Member member = memberRepository.findByEmail(memberLoginDto.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_EMAIL_NOT_FOUND));

        if(passwordEncoder.matches(memberLoginDto.getPassword(), member.getPassword()) == false){
            throw new GlobalException(ErrorCode.MEMBER_PASSWORD_ERROR);
        }

        // Login ID/PW 로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginDto.getEmail(), memberLoginDto.getPassword());

        // password 체크를 하고 권한 정보 확인
        // authenticate 매서드가 실행될 때 userDetailsService 에서 UserPrincipal 반환
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 인증 정보로 JWT 토큰 생성
        JwtDto tokenDto = jwtProvider.generateToken(authentication);

        return tokenDto;
    }

    public void signup(SignUpDto signUpDto) {

        Optional<Member> optionalUser = memberRepository.findByEmail(signUpDto.getEmail());

        if(optionalUser.isPresent()){
            throw new GlobalException(ErrorCode.MEMBER_EMAIL_EXISTS);
        }

        String password = passwordEncoder.encode(signUpDto.getPassword());

        Member member = Member.builder()
                .name(signUpDto.getName())
                .password(password)
                .email(signUpDto.getEmail())
                .role(signUpDto.getRole())
                .build();

        memberRepository.save(member);
    }
}
