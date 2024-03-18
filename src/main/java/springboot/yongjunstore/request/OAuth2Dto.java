package springboot.yongjunstore.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@Getter
@Builder
public class OAuth2Dto {

    private Map<String, Object> attributes; // 사용자 정보
    private String key; // 사용자 키
    private String email; // 이메일
    private String name; // 이름
    private String picture; // 프로필 사진
    private String provider; // 제공자

    // 서비스에 따라 OAuth2Attribute 객체를 생성하는 메서드
     public static OAuth2Dto of(String provider, String attributeKey, Map<String, Object> attributes) {
         return ofGoogle(provider, attributeKey, attributes);
    }

    /*
     *   Google 로그인일 경우 사용하는 메서드, 사용자 정보가 따로 Wrapping 되지 않고 제공되어,
     *   바로 get() 메서드로 접근이 가능하다.
     * */
    public static OAuth2Dto ofGoogle(String provider, String attributeKey, Map<String, Object> attributes) {
        return OAuth2Dto.builder()
                .attributes(attributes)
                .key(attributeKey)
                .email((String) attributes.get("email"))
                .provider(provider)
                .build();
    }

    // OAuth2User 객체에 넣어주기 위해서 Map으로 값들을 반환해준다.
    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", key);
        map.put("key", key);
        map.put("email", email);
        map.put("provider", provider);

        return map;
    }

}
