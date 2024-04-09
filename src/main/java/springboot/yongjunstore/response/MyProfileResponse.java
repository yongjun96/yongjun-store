package springboot.yongjunstore.response;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;

@Data
public class MyProfileResponse {

    private Long id;

    private String email;

    private String name;

    private Role role;

    // googleLogin
    private String provider;

    // googleLogin
    private String providerId;


        @Builder
        public MyProfileResponse(Member member){
            this.id = member.getId();
            this.email = member.getEmail();
            this.name = member.getName();
            this.role = member.getRole();
            this.provider = member.getProvider();
            this.providerId = member.getProviderId();
        }

}
