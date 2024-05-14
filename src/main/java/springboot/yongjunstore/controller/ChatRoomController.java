package springboot.yongjunstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.yongjunstore.common.annotation.SwaggerErrorCodes;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.request.ChatRoomCreateRequest;
import springboot.yongjunstore.service.ChatRoomService;

@Tag(name = "ChatRoomController", description = "채팅방 관련 명세를 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "채팅방 생성", description = "채팅방 생성 기능을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtDto.class))
            )
    })
    @SwaggerErrorCodes({
            ErrorCode.SERVER_FORBIDDEN,
            ErrorCode.SERVER_UNAUTHORIZED
    })
    @PostMapping("/create")
    public ResponseEntity createChatRoom(@RequestBody ChatRoomCreateRequest chatRoomCreateRequest){

            chatRoomService.createChatRoom(chatRoomCreateRequest);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

}
