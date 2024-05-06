package kissshot1104.personal.blog.member.repository;


import java.util.Optional;
import kissshot1104.personal.blog.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(final String username);
}
