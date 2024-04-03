package springboot.yongjunstore.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springboot.yongjunstore.domain.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByName(String name);

    Optional<Member> findByEmailAndPassword(String email, String password);

    Optional<Member> findById(Long id);

    @Modifying
    @Query("UPDATE Member m SET m.password = :password WHERE m.email = :email")
    int updateMemberPassword(@Param("email") String email, @Param("password") String password);

}
