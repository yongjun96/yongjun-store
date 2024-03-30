package springboot.yongjunstore.request;

import lombok.Builder;
import lombok.Data;

@Data
public class MemberLoginRequest {

    private String email;
    private String password;

    @Builder
    public MemberLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
