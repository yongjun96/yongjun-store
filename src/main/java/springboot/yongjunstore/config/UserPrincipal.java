package springboot.yongjunstore.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import springboot.yongjunstore.domain.Member;

import java.util.List;

public class UserPrincipal extends User {

    // role : 역할 -> 관리자, 사용자, 매니저
    // authority : 권한 -> 글쓰기, 읽기, 사용자 정지 시키기

    public UserPrincipal(Member member){
        super(member.getEmail(), member.getPassword(), List.of(
                new SimpleGrantedAuthority("ROLE_"+member.getRole())
                //, new SimpleGrantedAuthority("WRITE") // DB에 값 넣는 걸로 변경 필요
        ));
    }
}
