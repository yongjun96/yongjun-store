package springboot.yongjunstore.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import springboot.yongjunstore.request.MemberLoginDto;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_ERR)
class JwtProviderTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;


    @Test
    void testLoginEndpoint() throws Exception {
        // 로그인 요청 페이로드 설정
        MemberLoginDto loginDto = MemberLoginDto.builder()
                .email("test@example.com")
                .password("password")
                .build();

        // 로그인 요청을 위한 API 엔드포인트 설정
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 응답에 accessToken과 refreshToken이 있는지 확인할 수 있습니다.
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").exists());
    }

}