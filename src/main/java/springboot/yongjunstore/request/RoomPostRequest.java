package springboot.yongjunstore.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import springboot.yongjunstore.domain.room.Deposit;
import springboot.yongjunstore.domain.room.RoomStatus;

@Data
@NoArgsConstructor
@ToString
public class RoomPostRequest {

    @Schema(description = "방 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "제목입니다.")
    @NotBlank(message = "제목은 필수값입니다.")
    @Size(min = 1, max = 255)
    private String title;

    @Schema(description = "보증금 및 전세 가격", example = "10000")
    @Pattern(regexp = "^[0-9]{1,13}$", message = "1만원 단위이며 억 단위를 넘을 수 없습니다. [없는 경우 0을 입력해 주세요]")
    private String monthlyPrice; // 방 윌세

    @Schema(description = "보증금 및 전세", example = "전세")
    @NotNull(message = "보증금 및 전세는 필수 값입니다.")
    private Deposit deposit; // 보증금 및 전세

    @Schema(description = "보증금 및 전세 가격", example = "10000")
    @Pattern(regexp = "^[0-9]{1,13}$", message = "1만원 단위이며 억 단위를 넘을 수 없습니다.")
    private String depositPrice; // 보증금 및 전세 가격

    @Schema(description = "주인", example = "홍길동")
    @NotBlank(message = "이름을 입력해주세요.")
    private String roomOwner; // 방 주인

    @Schema(description = "평수(면적)", example = "5")
    @Pattern(regexp = "^[0-9]{1,5}$", message = "1평 단위이며 1만평을 넘을 수 없습니다.")
    private String squareFootage; // 방 평수(면적)

    @Schema(description = "내용", example = "내용입니다.")
    @NotBlank(message = "내용을 입력해 주세요.")
    private String content; // 글 내용

    @Schema(description = "주소", example = "서울시 강남구")
    @NotBlank(message = "주소는 필수값 입니다.")
    private String address; // 방 주소

    @Schema(description = "상세 주소", example = "강남 아파트 101동 101호")
    @NotBlank(message = "상세 주소는 필수값 입니다.")
    private String detailAddress; // 방 상세 주소

    @Schema(description = "상태", example = "매매")
    @NotNull(message = "임대, 매매, 종료 상태는 필수값입니다.")
    private RoomStatus roomStatus; // 방 임대, 매매 상태

    @Schema(description = "방주인 정보", example = "member")
    @NotNull(message = "/member/find/{email}을 호출하지 못해 id를 받아 오지못했습니다.")
    private Long memberId; // 방 주인 정보

    @Builder
    public RoomPostRequest(Long id, String title, String monthlyPrice, Deposit deposit, String depositPrice, String roomOwner, String squareFootage, String content, String address, String detailAddress, RoomStatus roomStatus, Long memberId) {
        this.id = id;
        this.title = title;
        this.monthlyPrice = monthlyPrice;
        this.deposit = deposit;
        this.depositPrice = depositPrice;
        this.roomOwner = roomOwner;
        this.squareFootage = squareFootage;
        this.content = content;
        this.address = address;
        this.detailAddress = detailAddress;
        this.roomStatus = roomStatus;
        this.memberId = memberId;
    }
}
