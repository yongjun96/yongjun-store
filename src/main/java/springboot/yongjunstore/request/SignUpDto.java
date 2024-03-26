package springboot.yongjunstore.request;

import jakarta.validation.constraints.*;
import lombok.*;
import springboot.yongjunstore.domain.Role;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class SignUpDto {

    @NotBlank(message = "email은 필수값 입니다.")
    @Email
    private String email;

    @NotBlank(message = "password는 필수값 입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "최소 8자 이상, 하나 이상의 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "사용자 이름은 필수값 입니다.")
    @Pattern(regexp = "^[가-힣]{2,10}$", message = "한글 2~8자로 입력해 주세요.")
    private String name;

    @NotNull(message = "귄한은 필수값 입니다.")
    private Role role;

    @Builder
    public SignUpDto(String name, String password, String email, Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;

    }
}
