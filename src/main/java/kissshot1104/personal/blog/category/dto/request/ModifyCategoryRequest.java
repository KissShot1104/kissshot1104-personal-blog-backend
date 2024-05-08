package kissshot1104.personal.blog.category.dto.request;

import lombok.Builder;

@Builder
public record ModifyCategoryRequest(Long categoryId,
                                    String categoryName,
                                    Long parentCategoryId) {
}
