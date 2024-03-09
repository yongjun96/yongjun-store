package springboot.yongjunstore.request;

import lombok.Builder;
import lombok.Data;

@Data

public class MemberLoginDto {

    private String email;
    private String password;

    @Builder
    public MemberLoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
