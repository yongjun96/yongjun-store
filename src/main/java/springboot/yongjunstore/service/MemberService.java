package springboot.yongjunstore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public JwtDto login(MemberLoginDto memberLoginDto) {
        // 1. Login ID/PW 로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginDto.getEmail(), memberLoginDto.getPassword());

        // 2. password 체크를 하고 권한 정보 확인
        // authenticate 매서드가 실행될 때 userDetailsService 에서 UserPrincipal 반환
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증 정보로 JWT 토큰 생성
        JwtDto tokenDto = jwtProvider.generateToken(authentication);

        return tokenDto;
    }

    public SignUpDto signup(SignUpDto signUpDto) {

        Optional<Member> optionalUser = memberRepository.findByEmail(signUpDto.getEmail());

        if(optionalUser.isPresent()){
            throw new GlobalException(ErrorCode.MEMBER_EMAIL_ALREAD_EXISTS);
        }

        String password = passwordEncoder.encode(signUpDto.getPassword());

        Member member = Member.builder()
                .name(signUpDto.getName())
                .password(password)
                .email(signUpDto.getEmail())
                .role(signUpDto.getRole())
                .build();

        Member findMember = memberRepository.save(member);

        return SignUpDto.builder()
                .name(findMember.getName())
                .build();
    }
}
