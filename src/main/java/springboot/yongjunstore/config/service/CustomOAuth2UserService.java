package springboot.yongjunstore.config.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.CustomOAuth2User;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final DefaultOAuth2UserService defaultOAuth2UserService; // 기존의 Super Class

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth2UserService를 사용하여 OAuth2User 정보를 가져온다.
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        // ex). provider = google
        String provider = userRequest.getClientRegistration().getRegistrationId();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User);

        // CustomOAuth2User Attributes -> provider 추가
        customOAuth2User.addAttribute("provider", provider);

        // 이메일로 가입된 회원인지 조회한다.
        Optional<Member> findMember = memberRepository.findByEmail((String) customOAuth2User.getAttributes().get("email"));


        if (findMember.isEmpty()) {
            // 회원이 존재하지 않을 경우
            customOAuth2User.addAttribute("exist", false);

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")),
                    customOAuth2User.getAttributes(), "email");
        }else{
            //회원이 존재할 경우

            // Oauth2.0 가입 회원이 아닌 경우
            if(findMember.get().getProvider() == null && findMember.get().getProviderId() == null){
                throw new BadCredentialsException(ErrorCode.OAUTH_EMAIL_EXISTS.getMessage());
            }

            customOAuth2User.addAttribute("exist", true);

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_"+findMember.get().getRole())),
                    customOAuth2User.getAttributes(), "email");
        }
    }
}
