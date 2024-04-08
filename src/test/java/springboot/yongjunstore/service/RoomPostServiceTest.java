package springboot.yongjunstore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.domain.room.Deposit;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.domain.room.RoomStatus;
import springboot.yongjunstore.repository.ImagesRepository;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.repository.RoomPostRepository;
import springboot.yongjunstore.request.RoomPostRequest;
import springboot.yongjunstore.response.RoomPostResponse;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {"fileUpload.upload.local.path=/uploads"})
class RoomPostServiceTest {

    @Autowired private RoomPostRepository roomPostRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private RoomPostService roomPostService;
    @Autowired private ImagesRepository imagesRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        memberRepository.deleteAll();
        imagesRepository.deleteAll();
        roomPostRepository.deleteAll();
    }

    @Test
    @DisplayName("글생성 성공")
    void createRoom(){

        // given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        Member saveMember = memberRepository.save(member);

        MockMultipartFile file1 = new MockMultipartFile(
                "file", "filename.jpg", "image/jpeg", "imageData".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "file", "filename2.jpg", "image/jpeg", "imageData".getBytes());

        List<MultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        RoomPostRequest roomPostRequest = RoomPostRequest.builder()
                .title("제목")
                .roomOwner("방주인")
                .roomName("방이름")
                .detail("상세설명")
                .depositPrice("전세금")
                .description("부설명")
                .roomStatus(RoomStatus.임대)
                .deposit(Deposit.전세)
                .content("내용입니다. 10글자 이상입니다...")
                .memberId(saveMember.getId())
                .monthlyPrice("보증금")
                .squareFootage("4")
                .address("주소")
                .build();

        // when
        roomPostService.createRoom(roomPostRequest, files);

        //then
        Assertions.assertThat(roomPostRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("글생성 실패 : 회원을 찾을 수 없습니다.")
    void createRoomMemberNotFound(){

        // given
        MockMultipartFile file1 = new MockMultipartFile(
                "file", "filename.jpg", "image/jpeg", "imageData".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "file", "filename2.jpg", "image/jpeg", "imageData".getBytes());

        List<MultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        RoomPostRequest roomPostRequest = RoomPostRequest.builder()
                .title("제목")
                .roomOwner("방주인")
                .roomName("방이름")
                .detail("상세설명")
                .depositPrice("전세금")
                .description("부설명")
                .roomStatus(RoomStatus.임대)
                .deposit(Deposit.전세)
                .content("내용입니다. 10글자 이상입니다...")
                .memberId(1L)
                .monthlyPrice("보증금")
                .squareFootage("4")
                .address("주소")
                .build();


        // when
        Assertions.assertThatThrownBy(() -> roomPostService.createRoom(roomPostRequest, files))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("글조회 성공")
    void getRoomPost(){

        // given
        Member member = Member.builder()
                .email("yongjun@gmail.com")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(Role.ADMIN)
                .name("김용준")
                .build();

        Member saveMember = memberRepository.save(member);

        RoomPost roomPost = RoomPost.builder()
                .title("제목")
                .roomOwner("방주인")
                .roomName("방이름")
                .detail("상세설명")
                .depositPrice("전세금")
                .description("부설명")
                .roomStatus(RoomStatus.임대)
                .deposit(Deposit.전세)
                .content("내용입니다. 10글자 이상입니다...")
                .member(saveMember)
                .monthlyPrice("보증금")
                .squareFootage("4")
                .address("주소")
                .build();

        RoomPost saveRoomPost = roomPostRepository.save(roomPost);

        Images images = Images.builder()
                .roomPost(saveRoomPost)
                .path("테스트 경로")
                .name("테스트 이름")
                .build();

        List<Images> imagesList = new ArrayList<>();
        imagesList.add(images);

        saveRoomPost.addImagesList(imagesList);

        roomPostRepository.save(saveRoomPost);


        // when
        RoomPostResponse roomPostResponse = roomPostService.getRoomPost(roomPost.getId());

        // then
        Assertions.assertThat(saveRoomPost.getId()).isEqualTo(roomPostResponse.getId());

    }


    @Test
    @DisplayName("글리스트 조회 성공")
    void searchRoomPostList(){

        // given
        String searchOption = "title";
        String searchContent = "";
        Pageable pageable = mock(Pageable.class);
        pageable.getPageSize();
        pageable.getOffset();

        when(pageable.getPageSize()).thenReturn(4);
        when(pageable.getOffset()).thenReturn(5L);

        // when
        Page<RoomPostResponse> roomPostResponseList = roomPostService.searchRoomPostList(searchOption, searchContent, pageable);

        // then
        Assertions.assertThat(roomPostResponseList.getSize()).isEqualTo(roomPostRepository.count());
    }




}