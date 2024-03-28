package springboot.yongjunstore.domain.room;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "images_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_post_id")
    private RoomPost roomPost;

    @Builder
    public Images(String name, RoomPost roomPost) {
        this.name = name;
        this.roomPost = roomPost;
    }
}
