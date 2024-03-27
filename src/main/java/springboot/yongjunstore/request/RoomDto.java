package springboot.yongjunstore.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import springboot.yongjunstore.domain.Post;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.Room;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class RoomDto {

    private Long id;

    private String name; // 방 이름

    private String price; // 방 가격

    private String description; // 방 설명

    private String mainPhoto; // 방 메인 사진

    private String roomOwner; // 방 주인

    private String detail; // 방 세부 사항

    private String squareFootage; // 방 평수(면적)

    private List<Images> images;

    private Post post;

    @Builder
    public RoomDto(Room room) {
        this.name = room.getName();
        this.price = room.getPrice();
        this.description = room.getDescription();
        this.mainPhoto = room.getMainPhoto();
        this.roomOwner = room.getRoomOwner();
        this.detail = room.getDetail();
        this.squareFootage = room.getSquareFootage();
        this.images = room.getImages();
        this.post = room.getPost();
    }
}
