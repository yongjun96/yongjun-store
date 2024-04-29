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
    SERVER_FILE_SIZE_LIMIT(500, HttpStatus.INTERNAL_SERVER_ERROR, "S005", "최대 업로드 크기 제한을 초과하였습니다."),

    // MEMBER
    MEMBER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "M001", "사용자를 찾지 못했습니다."),
    MEMBER_EMAIL_EXISTS(400, HttpStatus.BAD_REQUEST, "M002", "이미 존재하는 이메일입니다."),
    MEMBER_PASSWORD_ERROR(400, HttpStatus.BAD_REQUEST, "M003", "비밀번호가 틀렸습니다."),
    MEMBER_EMAIL_NOT_FOUND(404, HttpStatus.NOT_FOUND, "M004", "존재하지 않는 이메일입니다."),
    MEMBER_PASSWORD_UNCHECKED(400, HttpStatus.BAD_REQUEST, "M005", "비밀번호가 일치하지 않습니다."),
    MEMBER_DELETE_FAIL(400, HttpStatus.BAD_REQUEST, "M006", "회원 탈퇴에 실패하였습니다."),

    // JWT
    JWT_UNSUPPORTED_JWT_EXCEPTION(401, HttpStatus.BAD_REQUEST, "T001", "원하는 토큰과 다른 형식의 토큰입니다."),
    JWT_MALFORMED_JWT_EXCEPTION(402, HttpStatus.BAD_REQUEST, "T002", "잘못된 구조의 지원되지 않는 토큰입니다."),
    JWT_EXPIRED_JWT_EXCEPTION(403, HttpStatus.BAD_REQUEST, "T003", "만료된 토큰입니다."),
    JWT_SIGNATURE_EXCEPTION(404, HttpStatus.NOT_FOUND, "T004", "검증에 실패한 변조된 토큰입니다."),
    JWT_REFRESH_EXPIRED_JWT_EXCEPTION(403, HttpStatus.NOT_FOUND, "T005", "만료된 refreshToken입니다."),

    // OAuth2.0
    OAUTH_EMAIL_EXISTS(400, HttpStatus.BAD_REQUEST, "O001", "이미 일반 회원으로 등록된 이메일입니다."),
    OAUTH_EMAIL_DELETED(400, HttpStatus.BAD_REQUEST, "O002", "탈퇴된 회원입니다. 탈퇴한 시간으로 부터 1분 뒤, 다시 가입해주세요."),

    // fileUpload
    IMAGE_FILE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "F001", "이미지 파일을 찾지 못했습니다."),
    IMAGE_FILE_NOT_UPLOAD(400, HttpStatus.BAD_REQUEST, "F002", "이미지 파일이 업로드되지 못했습니다."),
    IMAGE_FILE_EXTENSION_NOT_FOUND(404, HttpStatus.NOT_FOUND, "F003", "이미지의 확장자를 찾을 수 없습니다."),
    IMAGE_FILE_MAX_UPLOAD_SIZE(413, HttpStatus.PAYLOAD_TOO_LARGE, "F004", "파일 업로드 크기 제한을 초과하였습니다."),

    // RoomPost
    ROOM_POST_NOT_FOUND(404, HttpStatus.NOT_FOUND, "R001", "RoomPost를 찾을 수 없습니다."),
    ROOM_POST_SEARCH_OPTION_NOT_FOUND(404, HttpStatus.NOT_FOUND, "R002", "searchOption(검색 옵션)을 찾을 수 없습니다."),
    ROOM_POST_ALREADY_TERMINATED(400, HttpStatus.BAD_REQUEST, "R003", "이미 개시 중지된 글입니다."),
    ROOM_POST_DELETE_ROLE_EXISTS(400, HttpStatus.BAD_REQUEST, "R004", "삭제 권한이 없는 글입니다."),

    // GoogleEmail
    GOOGLE_EMAIL_MESSAGE_EXCEPTION(400, HttpStatus.BAD_REQUEST, "G001", "메세지 생성에 실패했습니다."),
    GOOGLE_EMAIL_AUTH_NUMBER_ERROR(400, HttpStatus.BAD_REQUEST, "G002", "인증번호가 틀렸습니다."),
    GOOGLE_EMAIL_AUTH_NUMBER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "G003", "인증번호가 만료되었거나, 발급되지 않았습니다. 재발급 받아주세요."),
    GOOGLE_INVALID_AUTH_NUMBER_FORMAT(400, HttpStatus.BAD_REQUEST, "G004", "인증 번호가 올바른 형식이 아닙니다.");


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
