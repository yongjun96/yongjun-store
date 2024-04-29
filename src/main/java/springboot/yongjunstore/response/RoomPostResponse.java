package springboot.yongjunstore.response;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "방 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "제목입니다.")
    private String title; // 제목

    @Schema(description = "월세", example = "10")
    private String monthlyPrice; // 방 월세

    @Schema(description = "보증금 및 전세", example = "전세")
    private Deposit deposit; // 보증금 및 전세

    @Schema(description = "보증금 및 전세 가격", example = "10000")
    private String depositPrice; // 보증금 및 전세 가격

    @Schema(description = "주인", example = "홍길동")
    private String roomOwner; // 방 주인

    @Schema(description = "평수(면적)", example = "5")
    private String squareFootage; // 방 평수(면적)

    @Schema(description = "내용", example = "내용입니다.")
    private String content; // 글 내용

    @Schema(description = "주소", example = "서울시 강남구")
    private String address; // 방 주소

    @Schema(description = "상세 주소", example = "강남 아파트 101동 101호")
    private String detailAddress; // 방 상세 주소

    @Schema(description = "상태", example = "매매")
    private RoomStatus roomStatus; // 방 상태

    @Schema(description = "방주인 정보", example = "member")
    private MemberResponse member; // 방주인 정보

    @Schema(description = "이미지 리스트", example = "이미지 리스트")
    private List<ImagesResponse> imagesList = new ArrayList<>(); // 이미지 리스트

    @Builder
    public RoomPostResponse(RoomPost roomPost, List<ImagesResponse> imagesResponseList) {
        this.id = roomPost.getId();
        this.title = roomPost.getTitle();
        this.monthlyPrice = roomPost.getMonthlyPrice();
        this.deposit = roomPost.getDeposit();
        this.depositPrice = roomPost.getDepositPrice();
        this.roomOwner = roomPost.getRoomOwner();
        this.squareFootage = roomPost.getSquareFootage();
        this.content = roomPost.getContent();
        this.address = roomPost.getAddress();
        this.detailAddress = roomPost.getDetailAddress();
        this.roomStatus = roomPost.getRoomStatus();
        this.member = new MemberResponse(roomPost.getMember());
        this.imagesList = imagesResponseList;
    }


}
