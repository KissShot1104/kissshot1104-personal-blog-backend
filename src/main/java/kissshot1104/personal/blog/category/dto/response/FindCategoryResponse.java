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


//public class CategoryDTO {
//    private Long id;
//    private String categoryName;
//    private List<CategoryDTO> children;
//
//    // Constructors, Getters and Setters
//    public CategoryDTO(Long id, String categoryName) {
//        this.id = id;
//        this.categoryName = categoryName;
//        this.children = new ArrayList<>();
//    }
//
//    public void addChild(CategoryDTO child) {
//        this.children.add(child);
//    }
//
//    // standard getters and setters
//}