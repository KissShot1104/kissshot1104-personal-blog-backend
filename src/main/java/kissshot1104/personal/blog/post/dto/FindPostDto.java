package kissshot1104.personal.blog.post.dto;

import kissshot1104.personal.blog.post.dto.response.FindPostResponse;
import lombok.Builder;

@Builder
public record FindPostDto(Long postId,
                          String category,
                          String nickName,
                          String title,
                          String content,
                          String postSecurity) {

    public static FindPostDto of(final FindPostResponse response) {
        final FindPostDto findPostDto = FindPostDto.builder()
                .postId(response.postId())
                .category(response.category())
                .nickName(response.nickName())
                .title(response.title())
                .content(response.content())
                .postSecurity(response.postSecurity())
                .build();
        return findPostDto;
    }
}
