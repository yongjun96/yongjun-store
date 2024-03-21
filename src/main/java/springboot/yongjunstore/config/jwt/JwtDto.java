package springboot.yongjunstore.config.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtDto {

    private String grantType; // Bearer 사용
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
}
