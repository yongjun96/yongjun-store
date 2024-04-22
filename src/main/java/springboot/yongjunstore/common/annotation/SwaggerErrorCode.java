package springboot.yongjunstore.common.annotation;

import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메소드에 사용
@Retention(RetentionPolicy.RUNTIME) // 컴파일 이후에도 JVM에 참조됨
public @interface SwaggerErrorCode {

    ErrorCode value();

}
