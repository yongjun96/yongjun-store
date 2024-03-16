package springboot.yongjunstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import springboot.yongjunstore.domain.base.BaseTimeEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Size(min = 3, max = 50)
    @Email
    private String email;

    @NotNull
    private String password;

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
