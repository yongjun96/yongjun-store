package springboot.yongjunstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.yongjunstore.domain.room.RoomStatus;
import springboot.yongjunstore.request.RoomPostDto;
import springboot.yongjunstore.service.RoomPostService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/room-post")
public class RoomPostController {

    private final RoomPostService roomPostService;

    @GetMapping("/create")
    public ResponseEntity roomCreate(@RequestBody @Valid RoomPostDto roomPostDto){

        RoomPostDto returnRoomDto = roomPostService.createRoom(roomPostDto);

        return ResponseEntity.status(HttpStatus.OK).body(returnRoomDto);
    }
}
