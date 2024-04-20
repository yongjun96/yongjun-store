package springboot.yongjunstore.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "일반 회원의 로그인 Request")
@Data
public class MemberLoginRequest {

    @Schema(description = "이메일", example = "practice960426@gmail.com")
    private String email;
    @Schema(description = "비밀번호", example = "qwer!1234")
    private String password;

    @Builder
    public MemberLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
