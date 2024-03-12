package springboot.yongjunstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotBlank(message = "email은 필수값 입니다.")
    @Size(min = 3, max = 50)
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank(message = "사용자 이름은 필수값 입니다.")
    @Size(min = 1, max = 30)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

}
