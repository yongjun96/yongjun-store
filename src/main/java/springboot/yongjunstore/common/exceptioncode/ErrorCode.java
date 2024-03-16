package springboot.yongjunstore.common.exceptioncode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // SERVER
    SERVER_INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 에러"),
    SERVER_FORBIDDEN(403, HttpStatus.FORBIDDEN, "S002", "접근 권한이 없습니다."),
    SERVER_USER_DETAILS_USERNAME_NOT_FOUND(404, HttpStatus.NOT_FOUND, "S003", "해당 계정을 찾을 수 없습니다."),
    SERVER_UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "S004", "로그인이 필요합니다."),

    // MEMBER
    MEMBER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "M001", "사용자를 찾지 못했습니다."),
    MEMBER_EMAIL_EXISTS(400, HttpStatus.BAD_REQUEST, "M002", "이미 존재하는 이메일입니다."),
    MEMBER_PASSWORD_ERROR(404, HttpStatus.BAD_REQUEST, "M003", "비밀번호가 틀렸습니다."),
    MEMBER_EMAIL_NOT_FOUND(404, HttpStatus.BAD_REQUEST, "M004", "존재하지 않는 이메일입니다."),

    // JWT
    JWT_UNSUPPORTED_JWT_EXCEPTION(401, HttpStatus.BAD_REQUEST, "T001", "원하는 토큰과 다른 형식의 토큰입니다."),
    JWT_MALFORMED_JWT_EXCEPTION(402, HttpStatus.BAD_REQUEST, "T002", "잘못된 구조의 지원되지 않는 토큰입니다."),
    JWT_EXPIRED_JWT_EXCEPTION(403, HttpStatus.BAD_REQUEST, "T003", "만료된 토큰입니다."),
    JWT_SIGNATURE_EXCEPTION(404, HttpStatus.NOT_FOUND, "T004", "검증에 실패한 변조된 토큰입니다.");

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
