package kissshot1104.personal.blog.post.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import kissshot1104.personal.blog.post.entity.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPostResponse {
    private Long postId;
    private String category;
    private String nickName;
    private String title;
    private String content;
    private String postSecurity;

    @QueryProjection
    public FindPostResponse(final Long postId,
                            final String category,
                            final String nickName,
                            final String title,
                            final String content,
                            final String postSecurity) {
        this.postId = postId;
        this.category = category;
        this.nickName = nickName;
        this.title = title;
        this.content = content;
        this.postSecurity = postSecurity;
    }

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

    public static List<FindPostResponse> listOf(final List<Post> posts) {
        final List<FindPostResponse> findPostResponses = posts.stream()
                .map(FindPostResponse::of)
                .toList();
        return findPostResponses;
    }
}
