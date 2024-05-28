package kissshot1104.personal.blog.member.service;

import kissshot1104.personal.blog.global.exception.AuthException;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.member.entity.Member;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    public void checkAuthorizedMember(final Member author, final Member requester) {
        if (author == null || requester == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (author != requester) {
            throw new AuthException(ErrorCode.UNAUTHORIZED_USER);
        }
    }
}
