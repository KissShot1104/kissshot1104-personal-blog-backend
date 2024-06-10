package kissshot1104.personal.blog.post.dto.request;

import lombok.Builder;

@Builder
public record AuthenticationData(String postPassword) {
}
