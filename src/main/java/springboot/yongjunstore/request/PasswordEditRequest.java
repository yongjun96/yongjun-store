package springboot.yongjunstore.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
public class PasswordEditRequest {

    @Schema(description = "이메일", example = "practice960426@gmail.com")
    @Email(message = "이메일 형식이 알맞지 않습니다.")
    @NotBlank(message = "이메일은 필수값 입니다.")
    private String email;

    @Schema(description = "변경할 비밀번호", example = "asdf!1234")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "최소 8자 이상, 하나 이상의 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @Schema(description = "변경할 비밀번호 확인", example = "asdf!1234")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "최소 8자 이상, 하나 이상의 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String passwordCheck;

    @Builder
    public PasswordEditRequest(String email, String password, String passwordCheck) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }
}
