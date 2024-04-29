package springboot.yongjunstore.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.common.exceptioncode.ErrorCodeResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    // Controller 에서 발생한 GlobalException 처리
    @ExceptionHandler(value = GlobalException.class)
    public ResponseEntity<ErrorCodeResponse> ExceptionHandler(GlobalException e){

        ErrorCodeResponse ecr = new ErrorCodeResponse(e.getErrorCode());

        return ResponseEntity.status(ecr.getStatus()).body(ecr);
    }


    //valid 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e){

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }


    // 제약 조건을 위반한 경우
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(ConstraintViolationException e){

        Map<String, String> errors = new HashMap<>();

        e.getConstraintViolations().forEach(
                c -> errors.put(c.getPropertyPath().toString(), c.getMessage()));

        return ResponseEntity.badRequest().body(errors);
    }


    // 파일 업로드 크기 제한을 초과하는 경우
    @ExceptionHandler({MaxUploadSizeExceededException.class, SizeLimitExceededException.class})
    public ResponseEntity<ErrorCodeResponse> handleMaxSizeException(Exception e) {

        ErrorCode errorCode = null;

        // 파일을 한 번에 올릴 때 허용되는 용량 초과한 경우
        if (e instanceof MaxUploadSizeExceededException) {
            errorCode = ErrorCode.IMAGE_FILE_MAX_UPLOAD_SIZE;
        }

        // 전체 파일 용량 최대치 초과한 경우
        else if (e instanceof SizeLimitExceededException) {
            errorCode = ErrorCode.SERVER_FILE_SIZE_LIMIT;
        }

        // 에러 코드가 설정된 경우 응답을 반환
        if (errorCode != null) {
            ErrorCodeResponse ecr = new ErrorCodeResponse(errorCode);
            return ResponseEntity.status(ecr.getStatusCode()).body(ecr);
        }

        // 예상 못한 서버 에러가 발생한 경우
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorCodeResponse(ErrorCode.SERVER_INTERNAL_SERVER_ERROR));
    }

}
