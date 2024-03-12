package springboot.yongjunstore.common.exceptioncode;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorCodeResponse {

    private final int statusCode;
    private final HttpStatus status;
    private final String code;
    private final String message;

    @Builder
    public ErrorCodeResponse(ErrorCode errorCode) {
        this.statusCode = errorCode.getStatusCode();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
