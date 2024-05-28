package kissshot1104.personal.blog.integration.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.util.List;
import kissshot1104.personal.blog.global.exception.AuthException;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.repository.MemberRepository;
import kissshot1104.personal.blog.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    private Member member1;
    private Member member2;
    @BeforeEach
    public void setUp() {
        memberRepository.deleteAllInBatch();
        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1;").executeUpdate();

        member1 = Member.builder()
                .username("member1")
                .password("password1")
                .roles("USER")
                .build();

        member2 = Member.builder()
                .username("member2")
                .password("password2")
                .roles("USER")
                .build();

        memberRepository.saveAll(List.of(member1, member2));
    }

    @Test
    @DisplayName("동일한 사용자인지 검사한다.")
    public void checkAuthorizedMemberTest() {
        assertThatThrownBy(() -> memberService.checkAuthorizedMember(member1, member2))
                .isInstanceOf(AuthException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("작성자나 요청자에 null을 넣으면 예외가 발생한다.")
    public void shouldThrowExceptionWhenAuthorIsNull() {
        assertThatThrownBy(() -> memberService.checkAuthorizedMember(null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("적절하지 않은 요청 값입니다.");
    }
}
