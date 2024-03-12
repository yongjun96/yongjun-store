package springboot.yongjunstore.common.exception;

import lombok.Getter;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
