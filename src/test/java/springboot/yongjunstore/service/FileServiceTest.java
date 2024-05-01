package springboot.yongjunstore.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.ImagesRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
// 해당 properties의 세팅 값을 /uploads 지정해서 사용
@TestPropertySource(properties = {"fileUpload.upload.local.path=/uploads"})
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @MockBean
    private ImagesRepository imagesRepository;


    @Test
    @DisplayName("파일 업로드 성공 : 2개 이상")
    void mainPhotoUpload() {
        // given
        RoomPost roomPost = RoomPost.builder()
                .title("제목 입니다.")
                .address("주소입니다.")
                .detailAddress("상세 주소")
                .content("내용입니다. 내용입니다.")
                .roomOwner("방주인")
                .depositPrice("10000")
                .monthlyPrice("10")
                .squareFootage("4")
                .build();

        MockMultipartFile file1 = new MockMultipartFile(
                "file", "filename.jpg", "image/jpeg", "imageData".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "file", "filename2.jpg", "image/jpeg", "imageData".getBytes());

        List<MultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        // when
        fileService.mainPhotoUpload(files, roomPost);

        // then
        // imagesRepository.save(images)가 2번 동작 했는지 확인
        verify(imagesRepository, times(2)).save(any(Images.class));
    }


    @Test
    @DisplayName("파일 업로드 실패 : 이미지 파일이 아닌 경우")
    void mainPhotoUploadFail() {
        // given
        RoomPost roomPost = RoomPost.builder()
                .title("제목 입니다.")
                .address("주소입니다.")
                .detailAddress("상세 주소")
                .content("내용입니다. 내용입니다.")
                .roomOwner("방주인")
                .depositPrice("10000")
                .monthlyPrice("10")
                .squareFootage("4")
                .build();

        // 가짜 파일 생성
        MockMultipartFile file1 = new MockMultipartFile(
                "file", "filename.jpg", "text/pdf", "testData".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "file", "filename2.jpg", "text/pdf", "testData".getBytes());

        List<MultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        // when
        // 테스트 대상 메서드 호출
        assertThatThrownBy(() -> fileService.mainPhotoUpload(files, roomPost))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.IMAGE_FILE_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("파일 업로드 실패 : 파일의 확장자가 없는 경우")
    void mainPhotoUploadExtensionFail() {
        // given
        RoomPost roomPost = RoomPost.builder()
                .title("제목 입니다.")
                .address("주소입니다.")
                .detailAddress("상세 주소")
                .content("내용입니다. 내용입니다.")
                .roomOwner("방주인")
                .depositPrice("10000")
                .monthlyPrice("10")
                .squareFootage("4")
                .build();

        // 가짜 파일 생성
        MockMultipartFile file1 = new MockMultipartFile(
                "file", "filename", "image/jpeg", "testData".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "file", "filename2", "image/jpeg", "testData".getBytes());

        List<MultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        // when
        // 테스트 대상 메서드 호출
        assertThatThrownBy(() -> fileService.mainPhotoUpload(files, roomPost))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.IMAGE_FILE_EXTENSION_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("파일 업로드 실패 : 경로에 업로드가 실패한 경우")
    void mainPhotoUploadPathFail() throws IOException {
        // given
        RoomPost roomPost = RoomPost.builder()
                .title("제목 입니다.")
                .address("주소입니다.")
                .detailAddress("상세 주소")
                .content("내용입니다. 내용입니다.")
                .roomOwner("방주인")
                .depositPrice("10000")
                .monthlyPrice("10")
                .squareFootage("4")
                .build();

        // 가짜 파일 생성
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);


        when(file1.getOriginalFilename()).thenReturn("test.jpg");
        when(file1.getContentType()).thenReturn("image/jpeg");
        when(file1.getName()).thenReturn("file");

        List<MultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);


        doThrow(new IOException()).when(file1).transferTo(any(Path.class));

        // when, then
        assertThatThrownBy(() -> fileService.mainPhotoUpload(files, roomPost))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.IMAGE_FILE_NOT_UPLOAD.getMessage());
    }
}