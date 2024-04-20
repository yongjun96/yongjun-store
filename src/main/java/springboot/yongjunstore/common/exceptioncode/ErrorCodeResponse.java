package springboot.yongjunstore.common.exceptioncode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorCodeResponse {

    @Schema(description = "상태코드", example = "400")
    private final int statusCode;

    @Schema(description = "상태", example = "에러의 상태를 나타냅니다.")
    private final HttpStatus status;

    @Schema(description = "에러코드", example = "에러의 정의된 코드를 나타냅니다.")
    private final String code;

    @Schema(description = "에러메세지", example = "에러의 상세 메세지를 나타냅니다.")
    private final String message;

    @Builder
    public ErrorCodeResponse(ErrorCode errorCode) {
        this.statusCode = errorCode.getStatusCode();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
