package springboot.yongjunstore.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import springboot.yongjunstore.domain.Member;

@Getter
@ToString
public class MemberResponse {

    @Schema(description = "방 주인 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "practice960426@gmail.com")
    private String email;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Builder
    public MemberResponse(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    @Builder
    public MemberResponse(Member member){
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
    }
}
