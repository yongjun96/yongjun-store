package springboot.yongjunstore.request;

import jakarta.validation.constraints.*;
import lombok.*;
import springboot.yongjunstore.domain.Role;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class SignUpDto {

    @NotBlank(message = "email은 필수값 입니다.")
    private String email;
    @NotBlank(message = "password는 필수값 입니다.")
    private String password;
    @NotBlank(message = "사용자 이름은 필수값 입니다.")
    private String name;
    @NotNull
    private Role role;

    @Builder
    public SignUpDto(String name, String password, String email, Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
