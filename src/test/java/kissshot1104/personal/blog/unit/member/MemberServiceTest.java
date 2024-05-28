package kissshot1104.personal.blog.unit.member;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kissshot1104.personal.blog.global.exception.AuthException;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("동일한 사용자인지 검사한다.")
    public void checkAuthorizedMemberTest() {
        final Member member1 = Member.builder().username("member1").build();
        final Member member2 = Member.builder().username("member2").build();

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
