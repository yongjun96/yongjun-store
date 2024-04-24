package springboot.yongjunstore.response;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "practice960426@gmail.com")
    private String email;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "권한", example = "MEMBER")
    private Role role;

    // googleLogin
    @Schema(description = "회원 제공자", example = "google")
    private String provider;

    // googleLogin
    @Schema(description = "회원 제공자 ID", example = "123456")
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
