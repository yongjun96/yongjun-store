package springboot.yongjunstore.domain.room;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.base.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_post_id")
    private Long id;

    @NotNull
    private String title; // 글 제목

    private String monthlyPrice; // 방 월세

    @Enumerated(EnumType.STRING)
    private Deposit deposit; // 보증금 및 전세

    private String depositPrice; // 보증금 및 전세 가격

    @NotNull
    private String roomOwner; // 방 주인

    private String squareFootage; // 방 평수(면적)

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content; // 글 내용

    @Lob
    @Column(columnDefinition = "TEXT")
    private String address; // 방 주소

    private String detailAddress; // 방 상세 주소

    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus; // 방 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 방주인 정보

    @OneToMany(mappedBy = "roomPost", cascade = CascadeType.ALL)
    private List<Images> imagesList =new ArrayList<>();

    @Builder
    public RoomPost(String title, String monthlyPrice, Deposit deposit,
                    String depositPrice, String roomOwner, String squareFootage,
                    String content, String address, String detailAddress,
                    RoomStatus roomStatus, Member member, List<Images> imagesList) {
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
        this.member = member;
        this.imagesList = imagesList;
    }

    public void addMember(Member member){
        this.member = member;
    }

    public void addImagesList(List<Images> imagesList){
        this.imagesList = imagesList;
    }
}
