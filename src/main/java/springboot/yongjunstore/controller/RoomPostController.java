package springboot.yongjunstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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


    @PostMapping(value = "/create")
    public ResponseEntity roomPostCreate(@Valid @ModelAttribute RoomPostRequest roomPostRequest,
                                            @RequestPart(value = "uploadImages") List<MultipartFile> uploadImages){

        roomPostService.createRoom(roomPostRequest, uploadImages);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/createS3")
    public ResponseEntity roomPostCreateS3(@Valid @ModelAttribute RoomPostRequest roomPostRequest,
                                         @RequestPart(value = "uploadImages") List<MultipartFile> uploadImages){

        roomPostService.createRoomS3(roomPostRequest, uploadImages);

        return ResponseEntity.status(HttpStatus.OK).build();
    }




    @GetMapping("/posts/{roomPostId}")
    public ResponseEntity searchRoomPost(@PathVariable("roomPostId") Long roomPostId){

       RoomPostResponse roomPostResponse = roomPostService.getRoomPost(roomPostId);

        return ResponseEntity.status(HttpStatus.OK).body(roomPostResponse);
    }

    @GetMapping("/posts")
    public ResponseEntity searchRoomPostList(@RequestParam("searchOption") String searchOption,
                                             @RequestParam("searchContent") String searchContent,
                                             Pageable pageable){

        Page<RoomPostResponse> roomPostResponseList = roomPostService.searchRoomPostList(searchOption, searchContent, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(roomPostResponseList);
    }

    @GetMapping("/test")
    public String test(){

        return "test 반영되었나 확인";
    }
}


