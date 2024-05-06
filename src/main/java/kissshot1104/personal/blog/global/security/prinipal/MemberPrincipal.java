package kissshot1104.personal.blog.global.security.prinipal;

import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberPrincipal implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByUsername(username).orElse(null);
        if (member == null) {
            throw new UsernameNotFoundException("user not found");
        }

        return new MemberAdapter(member);
    }
}
