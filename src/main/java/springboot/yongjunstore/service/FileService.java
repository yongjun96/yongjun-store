package springboot.yongjunstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.ImagesRepository;
import springboot.yongjunstore.repository.RoomPostRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FileService {

    @Value("${fileUpload.upload.local.path}")
    private String uploadPath;

    private final RoomPostRepository roomPostRepository;
    private final ImagesRepository imagesRepository;

    @Transactional
    public void mainPhotoUpload(List<MultipartFile> uploadImages, RoomPost roomPost){

        for (MultipartFile uploadFile: uploadImages) {

            // 이미지 파일만 업로드
            if (!Objects.requireNonNull(uploadFile.getContentType()).startsWith("image")) {
                log.warn("this file is not image type");
                throw new GlobalException(ErrorCode.IMAGE_FILE_NOT_FOUND);
            }

            // 실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로 => 바뀐 듯 ..
            String orginalName = uploadFile.getOriginalFilename();
            String fileName = orginalName.substring(orginalName.lastIndexOf("\\") + 1);

            log.info("orginalName: " + orginalName);
            log.info("fileName: " + fileName);

            // 날짜 폴더 생성
            String folderPath = makeFolder();

            // UUID
            String uuid = UUID.randomUUID().toString();

            // 저장할 파일 이름 중간에 "_"를 이용해서 구현
            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

            Path savePath = Paths.get(saveName);

            try {
                // 경로에 이미지 저장 완료
                uploadFile.transferTo(savePath);

                 Images images = Images.builder()
                        .name(saveName)
                        .path(uploadPath)
                        .roomPost(roomPost)
                        .build();

                // DB에 이미지 저장 완료
                 imagesRepository.save(images);

            } catch (IOException e) {
                throw new GlobalException(ErrorCode.IMAGE_FILE_NOT_UPLOAD);
            }
        }
    }


    /*날짜 폴더 생성*/
    private String makeFolder() {

        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = str.replace("/", File.separator);

        // make folder --------
        File uploadPathFolder = new File(uploadPath, folderPath);

        if (!uploadPathFolder.exists()) {
            boolean mkdirs = uploadPathFolder.mkdirs();
            log.info("-------------------makeFolder------------------");
            log.info("uploadPathFolder.exists(): " + uploadPathFolder.exists());
            log.info("mkdirs: " + mkdirs);
        }

        return folderPath;
    }

}


