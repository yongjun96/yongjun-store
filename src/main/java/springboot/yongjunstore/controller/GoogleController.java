package springboot.yongjunstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoogleController {

    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        // 인증 성공 후 호출되는 메서드
        // 사용자 정보 출력
        System.out.println("User name: " + oauth2User.getName());
        System.out.println("User authorities: " + oauth2User.getAuthorities());
        // 원하는 작업 수행
        return "redirect:/"; // 로그인 후 리다이렉트할 URL
    }

}
