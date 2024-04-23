package springboot.yongjunstore.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthCheckRequest {

    @Email
    @NotBlank(message = "이메일이 필요합니다.")
    @Schema(description = "이메일", example = "practice960426@gmail.com")
    private String email;

    @NotBlank(message = "인증번호가 필요합니다.")
    @Schema(description = "인증번호", example = "인증번호 입력")
    private int authNumber;

}
