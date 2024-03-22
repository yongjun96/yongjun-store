package springboot.yongjunstore.config.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import springboot.yongjunstore.repository.MemberRepository;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CustomOAuth2UserService CustomOAuth2UserService;

    @Test
    @DisplayName("로그인 성공 : OAuth2.0")
    void testLoadUser() {

    }
}