package springboot.yongjunstore.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import springboot.yongjunstore.common.exceptioncode.ErrorCodeResponse;
import springboot.yongjunstore.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = GlobalException.class)
    public ResponseEntity<ErrorCodeResponse> ExceptionHandler(GlobalException e){

        ErrorCodeResponse ecr = new ErrorCodeResponse(e.getErrorCode());

        return ResponseEntity.status(ecr.getStatus()).body(ecr);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e){

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

}
