package springboot.yongjunstore.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import springboot.yongjunstore.domain.Member;

@Getter
@ToString
public class MemberResponse {

    private Long id;

    private String email;

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
