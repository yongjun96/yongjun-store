package springboot.yongjunstore.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;
    private String chatRoomName;

    @Builder
    public ChatRoom(Long id, String chatRoomName) {
        this.id = id;
        this.chatRoomName = chatRoomName;
    }
}
