package springboot.yongjunstore.request;

import lombok.*;
import springboot.yongjunstore.domain.Role;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class SignUpDto {

    private String name;
    private String password;
    private String email;
    private Role role;

    @Builder
    public SignUpDto(String name, String password, String email, Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
