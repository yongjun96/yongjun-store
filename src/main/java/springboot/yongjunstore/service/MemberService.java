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
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.MemberLoginDto;
import springboot.yongjunstore.request.SignUpDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public JwtDto login(MemberLoginDto memberLoginDto) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginDto.getEmail(), memberLoginDto.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtDto tokenDto = jwtProvider.generateToken(authentication);

        return tokenDto;
    }

    public Member signup(SignUpDto signUpDto) throws Exception {

        Optional<Member> optionalUser = memberRepository.findByEmail(signUpDto.getEmail());

        if(optionalUser.isPresent()){
            throw new Exception("이미 존재하는 회원");
        }

        String password = passwordEncoder.encode(signUpDto.getPassword());

        Member member = Member.builder()
                .name(signUpDto.getName())
                .password(password)
                .email(signUpDto.getEmail())
                .role(signUpDto.getRole())
                .build();

        return memberRepository.save(member);
    }
}
