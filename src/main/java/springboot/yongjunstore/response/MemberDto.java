package springboot.yongjunstore.response;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberDto {

    private Long id;

    private String email;

    private String name;

    @Builder
    public MemberDto(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
