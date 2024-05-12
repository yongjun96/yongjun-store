package springboot.yongjunstore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import springboot.yongjunstore.repository.ChatRoomRepository;
import springboot.yongjunstore.request.ChatRoomCreateRequest;

@ActiveProfiles("test")
@SpringBootTest
class ChatRoomServiceTest {

    @Autowired private ChatRoomService chatRoomService;
    @Autowired private ChatRoomRepository chatRoomRepository;

    @BeforeEach
    void setUp(){
        chatRoomRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅방 신규 생성 성공")
    void createChatRoom(){

        ChatRoomCreateRequest chatRoomCreateRequest = new ChatRoomCreateRequest();

        chatRoomCreateRequest.setChatRoomName("신규 채팅방");

        chatRoomService.createChatRoom(chatRoomCreateRequest);

        Assertions.assertThat(chatRoomRepository.count()).isEqualTo(1);
        Assertions.assertThat(chatRoomRepository.findByChatRoomName(chatRoomCreateRequest.getChatRoomName())).isEqualTo(chatRoomCreateRequest.getChatRoomName());

    }



}