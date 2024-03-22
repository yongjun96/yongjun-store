package springboot.yongjunstore.config.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OAuth2UserServiceTest {

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @InjectMocks
    private OAuth2UserService OAuth2UserService;

    @Test
    void testLoadUser() {

    }
}