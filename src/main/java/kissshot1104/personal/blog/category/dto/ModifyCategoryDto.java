package kissshot1104.personal.blog.category.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import kissshot1104.personal.blog.category.dto.request.ModifyCategoryRequest;
import lombok.Builder;

@Builder
public record ModifyCategoryDto(Long categoryId,
                                @NotBlank(message = "카테고리 이름은 필수입니다.") String categoryName,
                                Long parentCategoryId,
                                Long subCategoryId,
                                Long subParentCategoryId) {
    public ModifyCategoryRequest toCategoryRequest() {
        final ModifyCategoryRequest modifyCategoryRequest = ModifyCategoryRequest.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .parentCategoryId(parentCategoryId)
                .subCategoryId(subCategoryId)
                .subParentCategoryId(subParentCategoryId)
                .build();
        return modifyCategoryRequest;
    }

    public static List<ModifyCategoryRequest> toModifyCategoryRequests(final List<ModifyCategoryDto> modifyCategoryDtos) {
        final List<ModifyCategoryRequest> modifyCategoryRequests = modifyCategoryDtos.stream()
                .map(ModifyCategoryDto::toCategoryRequest)
                .toList();
        return modifyCategoryRequests;
    }
}
