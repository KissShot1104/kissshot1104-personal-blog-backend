package kissshot1104.personal.blog.post.dto.request;

import lombok.Builder;

@Builder
public record CreatePostRequest(Long categoryId,
                                String title,
                                String content,
                                String postPassword,
                                String postSecurity) {
}
