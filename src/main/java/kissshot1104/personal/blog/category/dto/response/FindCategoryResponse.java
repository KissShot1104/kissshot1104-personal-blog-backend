package kissshot1104.personal.blog.category.dto.response;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record FindCategoryResponse(Long categoryId,
                                   Long categoryDepth,
                                   String categoryName,
                                   List<FindCategoryResponse> childCategoryList) {
//todo 리팩토링 필요
    public static FindCategoryResponse of(final Long categoryId, final Long categoryDepth, final String categoryName) {
        final FindCategoryResponse findCategoryResponse = FindCategoryResponse.builder()
                .categoryId(categoryId)
                .categoryDepth(categoryDepth)
                .categoryName(categoryName)
                .childCategoryList(new ArrayList<>())
                .build();

        return findCategoryResponse;
    }

    public void addChild(final FindCategoryResponse childCategory) {
        childCategoryList.add(childCategory);
    }

}
