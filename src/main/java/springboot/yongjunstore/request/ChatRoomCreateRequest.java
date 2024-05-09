package springboot.yongjunstore.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChatRoomCreateRequest {

    @Schema(description = "채팅방 이름", example = "practice960426@gmail.com의 채팅방")
    private String chatRoomName;
}
