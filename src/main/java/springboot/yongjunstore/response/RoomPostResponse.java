package springboot.yongjunstore.response;

import jakarta.persistence.*;
import lombok.*;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.room.Deposit;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.domain.room.RoomStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomPostResponse {

    private Long id;

    private String name; // 방 이름

    private String monthlyPrice; // 방 월세

    private Deposit deposit; // 보증금 및 전세

    private String depositPrice; // 보증금 및 전세 가격

    private String description; // 방 설명

    private String roomOwner; // 방 주인

    private String detail; // 방 세부 사항

    private String squareFootage; // 방 평수(면적)

    private String content; // 글 내용

    private String address; // 방 주소

    private RoomStatus roomStatus; // 방 상태

    private MemberResponse member; // 방주인 정보

    private List<ImagesResponse> imagesList = new ArrayList<>(); // 이미지 리스트

    @Builder
    public RoomPostResponse(RoomPost roomPost, List<ImagesResponse> imagesResponseList) {
        this.id = roomPost.getId();
        this.name = roomPost.getName();
        this.monthlyPrice = roomPost.getMonthlyPrice();
        this.deposit = roomPost.getDeposit();
        this.depositPrice = roomPost.getDepositPrice();
        this.description = roomPost.getDescription();
        this.roomOwner = roomPost.getRoomOwner();
        this.detail = roomPost.getDetail();
        this.squareFootage = roomPost.getSquareFootage();
        this.content = roomPost.getContent();
        this.address = roomPost.getAddress();
        this.roomStatus = roomPost.getRoomStatus();
        this.member = new MemberResponse(roomPost.getMember());
        this.imagesList = imagesResponseList;
    }
}
