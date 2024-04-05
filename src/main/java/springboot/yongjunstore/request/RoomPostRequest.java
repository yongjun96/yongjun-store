package springboot.yongjunstore.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import springboot.yongjunstore.domain.room.Deposit;
import springboot.yongjunstore.domain.room.RoomStatus;

@Data
@NoArgsConstructor
@ToString
public class RoomPostRequest {

    private Long id;

    @NotBlank(message = "제목은 필수값입니다.")
    private String title;

    @NotBlank(message = "방 이름은 필수값 입니다.")
    private String roomName; // 방 이름

    @Pattern(regexp = "^[0-9]+$", message = "숫자만 입력가능합니다. 1만원 단위")
    private String monthlyPrice; // 방 윌세

    @NotNull(message = "보증금 및 전세는 필수 값입니다.")
    private Deposit deposit; // 보증금 및 전세

    @Pattern(regexp = "^[0-9]+$", message = "숫자만 입력가능합니다. 1만원 단위")
    private String depositPrice; // 보증금 및 전세 가격

    private String description; // 방 설명

    @NotBlank(message = "이름을 입력해주세요.")
    private String roomOwner; // 방 주인

    private String detail; // 방 세부 사항

    @Pattern(regexp = "^[0-9]+$", message = "숫자만 입력가능합니다. 1평 단위")
    private String squareFootage; // 방 평수(면적)

    @Pattern(regexp = ".{10,}", message = "10자 이상 입력해주세요.")
    private String content; // 글 내용

    @NotBlank(message = "주소는 필수값 입니다.")
    private String address; // 방 주소

    @NotNull(message = "임대, 매매, 종료 상태는 필수값입니다.")
    private RoomStatus roomStatus; // 방 임대, 매매 상태

    @NotNull(message = "/member/find/{email}을 호출하지 못해 id를 받아 오지못했습니다.")
    private Long memberId; // 방 주인 정보

    @Builder
    public RoomPostRequest(Long id, String title, String roomName, String monthlyPrice, Deposit deposit, String depositPrice, String description, String roomOwner, String detail, String squareFootage, String content, String address, RoomStatus roomStatus, Long memberId) {
        this.id = id;
        this.title = title;
        this.roomName = roomName;
        this.monthlyPrice = monthlyPrice;
        this.deposit = deposit;
        this.depositPrice = depositPrice;
        this.description = description;
        this.roomOwner = roomOwner;
        this.detail = detail;
        this.squareFootage = squareFootage;
        this.content = content;
        this.address = address;
        this.roomStatus = roomStatus;
        this.memberId = memberId;
    }
}
