package springboot.yongjunstore.common.annotation;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorCodeHolder {
    private Example holder;
    private String name;
    private int code;
}
