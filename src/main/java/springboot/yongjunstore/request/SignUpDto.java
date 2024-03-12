package springboot.yongjunstore.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import springboot.yongjunstore.domain.Role;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class SignUpDto {

    private String email;
    private String password;
    private String name;
    private Role role;

    @Builder
    public SignUpDto(String name, String password, String email, Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
