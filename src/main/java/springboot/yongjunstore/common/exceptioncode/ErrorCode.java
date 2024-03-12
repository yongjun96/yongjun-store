package springboot.yongjunstore.common.exceptioncode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // COMMON
    MEMBER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "M001", "사용자를 찾지 못했습니다."),
    MEMBER_EMAIL_ALREAD_EXISTS(400, HttpStatus.BAD_REQUEST, "M002", "이미 존재하는 이메일입니다."),
    EXPIRED_CODE(400, HttpStatus.BAD_REQUEST, "M003", "Expired Code"),

    // AWS
    AWS_ERROR(400, HttpStatus.BAD_REQUEST, "A001", "aws client error");

    private final int statusCode;
    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(int statusCode, HttpStatus status, String code, String message) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.code = code;
    }

}
