package kissshot1104.personal.blog.post.dto.response;

import kissshot1104.personal.blog.post.entity.Post;
import lombok.Builder;

@Builder
public record FindPostResponse(Long postId,
                               String category,
                               String nickName,
                               String title,
                               String content,
                               String postSecurity) {
    public static FindPostResponse of(final Post post) {
        final FindPostResponse findPostResponse = FindPostResponse.builder()
                .postId(post.getId())
                .category(post.getCategory().getCategoryName())
                .nickName(post.getMember().getNickName())
                .title(post.getTitle())
                .content(post.getContent())
                .postSecurity(post.getPostSecurity().name())
                .build();
        return findPostResponse;
    }
}
