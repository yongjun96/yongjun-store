package springboot.yongjunstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.yongjunstore.domain.base.BaseTimeEntity;
import springboot.yongjunstore.domain.room.RoomPost;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Size(min = 3, max = 50)
    @Email
    private String email;

    private String password;

    @Size(min = 1, max = 30)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    // googleLogin
    private String provider;

    // googleLogin
    private String providerId;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<RoomPost> roomPosts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @Builder
    public Member(String email, String password, String name, Role role, String provider, String providerId,
                  List<RefreshToken> refreshTokens, List<RoomPost> roomPosts) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.roomPosts = roomPosts;
        this.refreshTokens = refreshTokens;
    }

}
