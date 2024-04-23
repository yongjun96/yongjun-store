package springboot.yongjunstore.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import springboot.yongjunstore.domain.room.Images;

@Data
@NoArgsConstructor
@ToString
public class ImagesResponse {

    @Schema(description = "이미지 ID", example = "1")
    private Long id;

    @Schema(description = "이미지 이름", example = "이미지 이름")
    private String name;

    @Schema(description = "이미지 경로", example = "이미지 경로")
    private String path;

    @Builder
    public ImagesResponse(Long id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public ImagesResponse (Images images){
        this.id = images.getId();
        this.name = images.getName();
        this.path = images.getPath();
    }

}
