package springboot.yongjunstore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import springboot.yongjunstore.response.ErrorResponse;

public class ExceptionController {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){

        ErrorResponse response = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST))
                .message("잘못된 요청입니다.")
                .build();

        for(FieldError fieldError : e.getFieldErrors()){
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return response;
    }
}
