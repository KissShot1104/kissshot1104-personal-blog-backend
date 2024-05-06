package kissshot1104.personal.blog.global.security.prinipal;

import java.util.List;
import kissshot1104.personal.blog.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class MemberAdapter extends User {

    private Member member;
    public MemberAdapter(Member member) {
        super(member.getUsername(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.member = member;
    }

}
