package kissshot1104.personal.blog.category.dto.request;

import lombok.Builder;

@Builder
public record CreateCategoryRequest(Long parentCategoryId,
                                    String categoryName) {
}
