package springboot.yongjunstore.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.room.Deposit;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.domain.room.RoomStatus;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class RoomPostDto {

    private Long id;

    private String name; // 방 이름

    private String monthlyPrice; // 방 윌세

    private Deposit deposit; // 보증금 및 전세

    private String depositPrice; // 보증금 및 전세 가격

    private String description; // 방 설명

    private String mainPhoto; // 방 메인 사진

    private String roomOwner; // 방 주인

    private String detail; // 방 세부 사항

    private String squareFootage; // 방 평수(면적)

    private String content; // 글 내용

    private String address; // 방 주소

    private RoomStatus roomStatus; // 방 임대, 매매 상태

    private List<Images> images; // 글 사진들

    private Member member; // 방 주인 정보

    @Builder
    public RoomPostDto(RoomPost roomPost) {
        this.name = roomPost.getName();
        this.monthlyPrice = roomPost.getMonthlyPrice();
        this.deposit = roomPost.getDeposit();
        this.depositPrice = roomPost.getDepositPrice();
        this.description = roomPost.getDescription();
        this.mainPhoto = roomPost.getMainPhoto();
        this.roomOwner = roomPost.getRoomOwner();
        this.detail = roomPost.getDetail();
        this.squareFootage = roomPost.getSquareFootage();
        this.content = roomPost.getContent();
        this.address = roomPost.getAddress();
        this.roomStatus = roomPost.getRoomStatus();
        this.images = roomPost.getImages();
        this.member = roomPost.getMember();
    }
}
