package springboot.yongjunstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.domain.ChatRoom;
import springboot.yongjunstore.repository.ChatRoomRepository;
import springboot.yongjunstore.request.ChatRoomCreateRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public void createChatRoom(ChatRoomCreateRequest chatRoomCreateRequest) {

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomName(chatRoomCreateRequest.getChatRoomName())
                .build();

        chatRoomRepository.save(chatRoom);
    }
}
