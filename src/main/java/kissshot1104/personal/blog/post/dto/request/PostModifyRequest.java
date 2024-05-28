package kissshot1104.personal.blog.post.dto.request;

import lombok.Builder;

@Builder
public record PostModifyRequest(String title,
                                String content,
                                String postSecurity,
                                String postPassword) {
}
