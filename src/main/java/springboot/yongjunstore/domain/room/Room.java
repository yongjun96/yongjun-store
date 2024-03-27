package springboot.yongjunstore.domain.room;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.yongjunstore.domain.Post;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    private String name; // 방 이름

    private String price; // 방 가격

    private String description; // 방 설명

    private String mainPhoto; // 방 메인 사진

    private String roomOwner; // 방 주인

    private String detail; // 방 세부 사항

    private String squareFootage; // 방 평수(면적)

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Images> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Room(String name, String price, String description,
                String mainPhoto, String roomOwner, String detail,
                String squareFootage, List<Images> images, Post post,
                RoomStatus status) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.mainPhoto = mainPhoto;
        this.roomOwner = roomOwner;
        this.detail = detail;
        this.squareFootage = squareFootage;
        this.images = images;
        this.post = post;
        this.status = status;
    }
}
