package springboot.yongjunstore.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendEmail {

    @Email(message = "이메일 형식이 알맞지 않습니다.")
    @NotBlank(message = "이메일은 필수값 입니다.")
    private String email;
}
