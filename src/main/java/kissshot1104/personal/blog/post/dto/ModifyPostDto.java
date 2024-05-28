package kissshot1104.personal.blog.post.dto;

import kissshot1104.personal.blog.post.dto.request.PostModifyRequest;
import lombok.Builder;

@Builder
public record ModifyPostDto(String title,
                            String content,
                            String postPassword,
                            String postSecurity) {

    public PostModifyRequest toPostModifyRequest() {
        final PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title(title)
                .content(content)
                .postPassword(postPassword)
                .postSecurity(postSecurity)
                .build();
        return postModifyRequest;
    }
}
