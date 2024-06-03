package kissshot1104.personal.blog.post.dto;

import java.util.List;
import kissshot1104.personal.blog.post.dto.response.FindPostResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record FindPostDto(Long postId,
                          String category,
                          String nickName,
                          String title,
                          String content,
                          String postSecurity) {

    public static FindPostDto of(final FindPostResponse response) {
        final FindPostDto findPostDto = FindPostDto.builder()
                .postId(response.getPostId())
                .category(response.getCategory())
                .nickName(response.getNickName())
                .title(response.getTitle())
                .content(response.getContent())
                .postSecurity(response.getPostSecurity())
                .build();
        return findPostDto;
    }

    public static Page<FindPostDto> listOf(final Page<FindPostResponse> responses) {
        final Page<FindPostDto> findPostDtos = responses
                .map(FindPostDto::of);
        return findPostDtos;
    }
}
