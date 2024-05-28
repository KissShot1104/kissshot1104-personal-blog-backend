package kissshot1104.personal.blog.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kissshot1104.personal.blog.post.dto.request.CreatePostRequest;
import lombok.Builder;

@Builder
public record CreatePostDto(@NotNull(message = "카테고리 선택은 필수입니다.") Long categoryId,
                            @NotBlank(message = "게시글 제목은 필수입니다.") String title,
                            @NotBlank(message = "게시글 본문은 필수입니다.") String content,
                            @NotBlank(message = "게시글 접근 제어 레벨은 필수입니다.") String postSecurity,
                            String postPassword) {
    public CreatePostRequest toCreatePostRequest() {
        final CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .categoryId(categoryId)
                .title(title)
                .content(content)
                .postPassword(postPassword)
                .postSecurity(postSecurity)
                .build();
        return createPostRequest;
    }
}
