package springboot.yongjunstore.config.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.RefreshToken;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.repository.RefreshTokenRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void saveRefreshToken(JwtDto jwtDto) {

        // 계정 존재 유무 getAuthentication에서 체크함
        Authentication authentication = jwtProvider.getAuthentication(jwtDto.getAccessToken());
        String email = authentication.getName();

        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(findMember);

        //RT 존재하지 않을 경우
        if (optionalRefreshToken.isEmpty()) {

            RefreshToken refreshToken = RefreshToken.builder()
                    .refreshToken(jwtDto.getRefreshToken())
                    .member(findMember)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }else {
            // RT 존재하는 경우
            refreshTokenRepository.updateRefreshToken(jwtDto.getRefreshToken(), findMember.getId());
        }
    }

    @Transactional
    public JwtDto reissueAccessToken(String accessToken){

        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(findMember);


        if(jwtProvider.validateRefreshToken(optionalRefreshToken.get().getRefreshToken())) {

            JwtDto jwtDto = jwtProvider.generateToken(authentication);

            //해당 이메일의 새로 발급 받은 RT로 update 해준다.
            refreshTokenRepository.updateRefreshToken(jwtDto.getRefreshToken(), findMember.getId());

            return jwtDto;

        } else {
            // RT가 만료된 경우 DB에서 해당 유저의 RT를 삭제
            refreshTokenRepository.delete(optionalRefreshToken.get());
            return null;
        }
    }
}
