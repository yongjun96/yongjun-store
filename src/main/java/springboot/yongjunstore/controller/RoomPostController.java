package springboot.yongjunstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springboot.yongjunstore.common.annotation.SwaggerErrorCodes;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.request.RoomPostRequest;
import springboot.yongjunstore.response.RoomPostResponse;
import springboot.yongjunstore.service.RoomPostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/roomPost")
public class RoomPostController {

    private final RoomPostService roomPostService;


    @SecurityRequirement(name = "JWT")
    @Operation(summary = "글 생성 (local)", description = "글 생성을 위해 local에 업로드할 파일을 포함한 요청을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 생성 성공", content = @Content)
    })
    @SwaggerErrorCodes({
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.ROOM_POST_NOT_FOUND,
            ErrorCode.IMAGE_FILE_NOT_FOUND,
            ErrorCode.IMAGE_FILE_EXTENSION_NOT_FOUND,
            ErrorCode.IMAGE_FILE_NOT_UPLOAD,
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity roomPostCreate(@ModelAttribute @Valid RoomPostRequest roomPostRequest,
                                         @RequestParam(value = "uploadImages") List<MultipartFile> uploadImages){

        roomPostService.createRoom(roomPostRequest, uploadImages);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @SecurityRequirement(name = "JWT")
    @Operation(summary = "글 생성 (S3 사용)", description = "글 생성을 위해 S3에 업로드할 파일을 포함한 요청을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 생성 성공", content = @Content)
    })
    @SwaggerErrorCodes({
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.ROOM_POST_NOT_FOUND,
            ErrorCode.IMAGE_FILE_NOT_FOUND,
            ErrorCode.IMAGE_FILE_EXTENSION_NOT_FOUND,
            ErrorCode.IMAGE_FILE_NOT_UPLOAD,
    })
    @PostMapping(value = "/createS3", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity roomPostCreateS3(@Valid @ModelAttribute RoomPostRequest roomPostRequest,
                                         @Parameter(description = "업로드할 이미지 파일 목록", content = @Content(mediaType = "multipart/form-data"))
                                         @RequestPart(value = "uploadImages") List<MultipartFile> uploadImages){

        roomPostService.createRoomS3(roomPostRequest, uploadImages);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "방 번호로 방찾기", description = "방 번호로 방을 찾는 기능을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 방 찾기 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomPostResponse.class))
            )
    })
    @SwaggerErrorCodes({
            ErrorCode.ROOM_POST_NOT_FOUND,
            ErrorCode.IMAGE_FILE_NOT_FOUND
    })
    @GetMapping("/posts/{roomPostId}")
    public ResponseEntity searchRoomPost(@PathVariable("roomPostId") Long roomPostId){

       RoomPostResponse roomPostResponse = roomPostService.getRoomPost(roomPostId);

        return ResponseEntity.status(HttpStatus.OK).body(roomPostResponse);
    }


    @Operation(summary = "방 목록 조회", description = "방 목록을 조회하는 기능을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class, anyOf = RoomPostResponse.class))
            )
    })
    @SwaggerErrorCodes(ErrorCode.ROOM_POST_SEARCH_OPTION_NOT_FOUND)
    @GetMapping("/posts")
    public ResponseEntity searchRoomPostList(@RequestParam("searchOption") String searchOption,
                                             @RequestParam("searchContent") String searchContent,
                                             Pageable pageable){

        Page<RoomPostResponse> roomPostResponseList = roomPostService.searchRoomPostList(searchOption, searchContent, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(roomPostResponseList);
    }

}


