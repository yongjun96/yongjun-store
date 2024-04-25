package springboot.yongjunstore.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "글 삭제 Request")
@Data
public class DeleteRoomPostRequest {

    @Schema(description = "글 ID", example = "1")
    private Long roomPostId;
    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

}
