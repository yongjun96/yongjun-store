package springboot.yongjunstore.response;

import lombok.*;
import springboot.yongjunstore.domain.room.Images;

@Data
@NoArgsConstructor
@ToString
public class ImagesResponse {

    private Long id;

    private String name;

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
