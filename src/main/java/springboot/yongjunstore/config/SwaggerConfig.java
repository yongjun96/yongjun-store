package springboot.yongjunstore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import springboot.yongjunstore.common.annotation.ErrorCodeHolder;
import springboot.yongjunstore.common.annotation.SwaggerErrorCode;
import springboot.yongjunstore.common.annotation.SwaggerErrorCodes;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.common.exceptioncode.ErrorCodeResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    @Value("${custom.url.backend-url}")
    private String backEndUrl;

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );
        Server server = new Server()
                .url(backEndUrl)
                .description("Production Server");

        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addServersItem(server)
                .components(components);
    }
    private Info apiInfo() {
        return new Info()
                .title("방장 API") // API의 제목
                .description("version - 0.0.1") // API에 대한 설명
                .version("0.0.1"); // API의 버전
    }



    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            SwaggerErrorCodes swaggerErrorCodes = handlerMethod.getMethodAnnotation(SwaggerErrorCodes.class);

            // @SwaggerErrorCodes 어노테이션이 붙어있다면
            if (swaggerErrorCodes != null) {
                generateErrorCodeResponseExample(operation, swaggerErrorCodes.value());
            } else {
                SwaggerErrorCode swaggerErrorCode = handlerMethod.getMethodAnnotation(SwaggerErrorCode.class);

                // @SwaggerErrorCodes 어노테이션이 붙어있지 않고
                // @SwaggerErrorCode 어노테이션이 붙어있다면
                if (swaggerErrorCode != null) {
                    generateErrorCodeResponseExample(operation, swaggerErrorCode.value());
                }
            }

            return operation;
        };
    }


    // 여러 개의 에러 응답값 추가
    private void generateErrorCodeResponseExample(Operation operation, ErrorCode[] errorCodes) {
        ApiResponses responses = operation.getResponses();

        // ErrorCodeHolder(에러 응답값) 객체를 만들고 에러 코드 별로 그룹화
        Map<Integer, List<ErrorCodeHolder>> statusWithErrorCodeHolders = Arrays.stream(errorCodes)
                .map(errorCode -> ErrorCodeHolder.builder()
                                .holder(getSwaggerExample(errorCode))
                                .code(errorCode.getStatus().value())
                                .name(errorCode.name())
                                .build()
                )
                .collect(Collectors.groupingBy(ErrorCodeHolder::getCode));

        // ApiResponses 에 추가
        addExamplesToResponses(responses, statusWithErrorCodeHolders);
    }


    // 단일 에러 응답값 예시 추가
    private void generateErrorCodeResponseExample(Operation operation, ErrorCode errorCode) {
        ApiResponses responses = operation.getResponses();

        // ErrorCodeHolder 객체 생성 및 ApiResponses 에 추가
        ErrorCodeHolder errorCodeHolder = ErrorCodeHolder.builder()
                .holder(getSwaggerExample(errorCode))
                .name(errorCode.name())
                .code(errorCode.getStatus().value())
                .build();
        addExamplesToResponses(responses, errorCodeHolder);
    }


    // ErrorCodeResponse 형태의 예시 객체 생성
    private Example getSwaggerExample(ErrorCode errorCode) {
        ErrorCodeResponse errorResponse = ErrorCodeResponse.builder()
                .errorCode(errorCode)
                .build();
        Example example = new Example();
        example.setValue(errorResponse);

        return example;
    }


    // ErrorCodeHolder 를 ApiResponses 에 추가
    private void addExamplesToResponses(ApiResponses responses, Map<Integer, List<ErrorCodeHolder>> statusWithErrorCodeHolders) {
        statusWithErrorCodeHolders.forEach(
                (status, v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();

                    v.forEach(errorCodeHolder ->
                            mediaType.addExamples(
                                    errorCodeHolder.getName(),
                                    errorCodeHolder.getHolder()
                            )
                    );
                    content.addMediaType("application/json", mediaType);
                    apiResponse.setContent(content);
                    responses.addApiResponse(String.valueOf(status), apiResponse);
                }
        );
    }


    private void addExamplesToResponses(ApiResponses responses, ErrorCodeHolder errorCodeHolder) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        ApiResponse apiResponse = new ApiResponse();

        mediaType.addExamples(errorCodeHolder.getName(), errorCodeHolder.getHolder());
        content.addMediaType("application/json", mediaType);
        apiResponse.content(content);
        responses.addApiResponse(String.valueOf(errorCodeHolder.getCode()), apiResponse);
    }

}
