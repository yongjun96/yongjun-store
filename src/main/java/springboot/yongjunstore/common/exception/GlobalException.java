package springboot.yongjunstore.common.exception;

import lombok.Builder;
import lombok.Getter;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;

import java.util.Map;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode(){
        return errorCode;
    }
}
