package springboot.yongjunstore.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import springboot.yongjunstore.domain.Role;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class SignUpRequest {

    @NotBlank
    @Email(message = "email 양식을 지켜야 합니다.")
    @Schema(description = "이메일", example = "practice960426@gmail.com")
    private String email;

    @Schema(description = "비밀번호", example = "qwer!1234")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "최소 8자 이상, 하나 이상의 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @Pattern(regexp = "^[가-힣]{2,10}$", message = "한글 2~8자로 입력해 주세요.")
    @Schema(description = "이름", example = "홍길동")
    private String name;

    @NotNull(message = "귄한은 필수값 입니다.")
    @Schema(description = "권한", example = "MEMBER")
    private Role role;

    @Builder
    public SignUpRequest(String name, String password, String email, Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;

    }
}
