package springboot.yongjunstore.request;

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
    private String email;

    @NotBlank(message = "인증번호가 필요합니다.")
    private int authNumber;

}
