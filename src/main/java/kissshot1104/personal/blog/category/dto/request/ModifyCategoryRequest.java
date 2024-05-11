package kissshot1104.personal.blog.category.dto.request;

import lombok.Builder;

@Builder
public record ModifyCategoryRequest(Long categoryId,
                                    String categoryName,
                                    Long parentCategoryId,
                                    Long subCategoryId,
                                    Long subParentCategoryId) {
    public Boolean isNewCategory() {
        if (categoryId == null) {
            return true;
        }
        return false;
    }
}
