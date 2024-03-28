package springboot.yongjunstore.domain.room;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.yongjunstore.domain.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_post_id")
    private Long id;

    private String name; // 방 이름

    private String monthlyPrice; // 방 월세

    @Enumerated(EnumType.STRING)
    private Deposit deposit; // 보증금 및 전세

    private String depositPrice; // 보증금 및 전세 가격

    private String description; // 방 설명

    private String mainPhoto; // 방 메인 사진

    private String roomOwner; // 방 주인

    private String detail; // 방 세부 사항

    private String squareFootage; // 방 평수(면적)

    private String content; // 글 내용

    private String address; // 방 주소

    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus; // 방 상태

    @OneToMany(mappedBy = "roomPost", fetch = FetchType.LAZY)
    private List<Images> images = new ArrayList<>(); // 글 사진들

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 방주인 정보

    @Builder
    public RoomPost(String name, String monthlyPrice, Deposit deposit,
                    String depositPrice, String description, String mainPhoto,
                    String roomOwner, String detail, String squareFootage,
                    String content, String address, List<Images> images,
                    RoomStatus roomStatus, Member member) {
        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.deposit = deposit;
        this.description = description;
        this.depositPrice = depositPrice;
        this.mainPhoto = mainPhoto;
        this.roomOwner = roomOwner;
        this.detail = detail;
        this.squareFootage = squareFootage;
        this.content = content;
        this.address = address;
        this.images = images;
        this.roomStatus = roomStatus;
        this.member = member;
    }
}
