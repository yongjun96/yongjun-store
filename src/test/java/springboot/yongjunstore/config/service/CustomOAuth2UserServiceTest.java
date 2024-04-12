package springboot.yongjunstore.config.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.test.context.ActiveProfiles;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.request.CustomOAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private DefaultOAuth2UserService defaultOAuth2UserService;

    @Test
    @DisplayName("로그인 성공 : 구글 회원을 서비스에 회원가입 시키고 DefaultOAuth2User 생성")
    public void testLoadUser() {

        // Given
        OAuth2UserRequest request = mock(OAuth2UserRequest.class);

        // ------------------------------ OAuth2User ----------------------------------
        CustomOAuth2User customOAuth2User = mock(CustomOAuth2User.class);

        Map<String, Object> attributes = customOAuth2User.getAttributes();
        attributes.put("email", "test@gmail.com");

        when(customOAuth2User.getAttributes()).thenReturn(attributes);

        // ------------------------- ClientRegistration -----------------------------
        ClientRegistration clientRegistration = mock(ClientRegistration.class);

        when(request.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("google");

        // ------------------------------- member ---------------------------------------
        when(defaultOAuth2UserService.loadUser(request)).thenReturn(customOAuth2User);

        //Member Entity에 회원 정보가 존재하면 안되는 경우
        when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());

        // When
        customOAuth2UserService.loadUser(request);

        // Then
        verify(memberRepository, times(1)).findByEmail(any());
    }

    @Test
    @DisplayName("로그인 실패 : 서비스에 구글로 가입하려는 이메일이 존재하는 경우")
    public void testLoadUser2() {

        // Given
        OAuth2UserRequest request = mock(OAuth2UserRequest.class);

        // ------------------------------ OAuth2User ----------------------------------
        CustomOAuth2User customOAuth2User = mock(CustomOAuth2User.class);

        Map<String, Object> attributes = customOAuth2User.getAttributes();
        attributes.put("email", "test@gmail.com");

        when(customOAuth2User.getAttributes()).thenReturn(attributes);

        // ------------------------- ClientRegistration -----------------------------
        ClientRegistration clientRegistration = mock(ClientRegistration.class);

        when(request.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("google");

        // ------------------------------- member ---------------------------------------
        when(defaultOAuth2UserService.loadUser(request)).thenReturn(customOAuth2User);

        //Member Entity에 회원 정보가 존재하면 안되는 경우
        when(memberRepository.findByEmail(any())).thenThrow(new BadCredentialsException(ErrorCode.OAUTH_EMAIL_EXISTS.getMessage()));

        // When
        Assertions.assertThatThrownBy(() -> customOAuth2UserService.loadUser(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining(ErrorCode.OAUTH_EMAIL_EXISTS.getMessage());

        // Then
        verify(memberRepository, times(1)).findByEmail(any());
    }
}


