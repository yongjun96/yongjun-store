package springboot.yongjunstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springboot.yongjunstore.request.RoomPostRequest;
import springboot.yongjunstore.service.RoomPostService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/roomPost")
public class RoomPostController {

    private final RoomPostService roomPostService;

    @PostMapping(value = "/create")
    public ResponseEntity roomCreate(@Valid @ModelAttribute RoomPostRequest roomPostRequest,
                                            @RequestPart(value = "uploadImages") List<MultipartFile> uploadImages){

        roomPostService.createRoom(roomPostRequest, uploadImages);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}


