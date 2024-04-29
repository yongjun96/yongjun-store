package springboot.yongjunstore.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.ImagesRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final ImagesRepository imagesRepository;
    private final AmazonS3 amazonS3;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;


    @Transactional
    public List<String> uploadS3Images(List<MultipartFile> uploadImages, RoomPost roomPost) {

        List<String> imageUrls = new ArrayList<>();


            for (MultipartFile uploadFile : uploadImages) {

                // 이미지 파일만 업로드
                if (!Objects.requireNonNull(uploadFile.getContentType()).startsWith("image")) {
                    log.warn("이미지 파일이 아닙니다.");
                    throw new GlobalException(ErrorCode.IMAGE_FILE_NOT_FOUND);
                }

                // 파일 크기가 최대 허용 크기를 초과
                if (uploadFile.getSize() > convertMaxFileSize()) {
                    log.warn("파일 크기 제한 초과: " + uploadFile.getSize() + " bytes");
                    throw new MaxUploadSizeExceededException(uploadFile.getSize());
                }

                String orginalName = StringUtils.cleanPath(uploadFile.getOriginalFilename());
                String fileName = orginalName.substring(orginalName.lastIndexOf("/") + 1); // 수정

                log.info("orginalName: " + orginalName);
                log.info("fileName: " + fileName);

                //확장자
                String extension = "";

                int lastIndex = fileName.lastIndexOf(".");
                if (lastIndex != -1) {
                    extension = fileName.substring(lastIndex); // 수정
                    System.out.println("확장자: " + extension);
                } else {
                    throw new GlobalException(ErrorCode.IMAGE_FILE_EXTENSION_NOT_FOUND);
                }

                // UUID
                String uuid = UUID.randomUUID().toString();

                //파일 name 빼고 uuid만 사용해서 만들기
                String saveName = orginalName + uuid + extension;


                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(uploadFile.getSize());
                metadata.setContentType(uploadFile.getContentType());

                try {

                    // Amazon S3에 이미지 업로드
                    amazonS3.putObject(bucket, saveName, uploadFile.getInputStream(), metadata);

                    // 업로드된 이미지의 URL을 리스트에 추가
                    String imageUrl = amazonS3.getUrl(bucket, saveName).toString();
                    imageUrls.add(imageUrl);

                    // 경로에 이미지 저장 완료
                    Images images = Images.builder()
                            .path(bucket)
                            .name(imageUrl)
                            .roomPost(roomPost)
                            .build();

                    // DB에 이미지 저장 완료
                    imagesRepository.save(images);

                } catch (IOException e) {
                    e.printStackTrace();
                    throw new GlobalException(ErrorCode.IMAGE_FILE_NOT_UPLOAD);
                }
            }

        // 모든 이미지의 URL을 반환
        return imageUrls;
    }


    @Transactional
    public void mainPhotoUpload(List<MultipartFile> uploadImages, RoomPost roomPost){

        for (MultipartFile uploadFile: uploadImages) {

            // 이미지 파일만 업로드
            if (!Objects.requireNonNull(uploadFile.getContentType()).startsWith("image")) {
                log.warn("이미지 파일이 아닙니다.");
                throw new GlobalException(ErrorCode.IMAGE_FILE_NOT_FOUND);
            }

            // 실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로 => 바뀐 듯 ..
            // cleanPath()를 통해서 ../ 내부 점들에 대해서 사용을 억제
            String orginalName = StringUtils.cleanPath(uploadFile.getOriginalFilename());
            String fileName = orginalName.substring(orginalName.lastIndexOf("\\") + 1);

            log.info("orginalName: " + orginalName);
            log.info("fileName: " + fileName);

            //확장자
            String extension = "";

            int lastIndex = fileName.lastIndexOf(".");
            if (lastIndex != -1) {
                extension = "."+fileName.substring(lastIndex + 1);
                System.out.println("확장자: " + extension);
            } else {
               throw new GlobalException(ErrorCode.IMAGE_FILE_EXTENSION_NOT_FOUND);
            }

            // 날짜 폴더 생성
            String folderPath = makeFolder();

            // UUID
            String uuid = UUID.randomUUID().toString();

            //파일 name 빼고 uuid만 사용해서 만들기
            String pathSaveName = uuid + extension;

            String saveFileName = pathSaveName.replaceAll("\\s+", "");


            //저장할 때 필요한 전체 경로
            String saveName = uploadPath + File.separator + folderPath + File.separator + saveFileName;

            Path savePath = Paths.get(saveName);


            try {
                // 경로에 이미지 저장 완료
                uploadFile.transferTo(savePath);

                 Images images = Images.builder()
                        .name(saveFileName)
                        .path(folderPath)
                        .roomPost(roomPost)
                        .build();

                // DB에 이미지 저장 완료
                 imagesRepository.save(images);

            } catch (IOException e) {
                e.printStackTrace();
                throw new GlobalException(ErrorCode.IMAGE_FILE_NOT_UPLOAD);
            }
        }
    }

    private Long convertMaxFileSize(){
        String sizeString = maxFileSize.replaceAll("[^0-9]", "");
        // byte 로 변환해서 리턴
        return (Long.parseLong(sizeString) * 1024 * 1024);
    }

    /*날짜 폴더 생성*/
    private String makeFolder() {

        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

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


