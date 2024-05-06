package kissshot1104.personal.blog.post.entity;

import java.util.Arrays;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum PostSecurity {
    CLOSE, PROTECTED, OPEN;

    public static PostSecurity checkPostSecurity(final String securityType) {
        return Arrays.stream(PostSecurity.values())
                .filter(type -> type.name().equals(securityType))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }
}
