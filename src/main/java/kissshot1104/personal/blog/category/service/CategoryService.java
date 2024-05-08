package kissshot1104.personal.blog.category.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kissshot1104.personal.blog.category.dto.request.CreateCategoryRequest;
import kissshot1104.personal.blog.category.dto.response.FindCategoryResponse;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(final CreateCategoryRequest request, final Member member) {
        final Category parentCategory = fetchParentCategory(request.parentCategoryId());
        return createCategoryInternal(request, parentCategory);
    }

    private Category fetchParentCategory(final Long parentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }

        final Category parentCategory = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));

        return parentCategory;
    }

    private Category createCategoryInternal(final CreateCategoryRequest request, final Category parent) {
        //최상위 카테고리는 0 그 밑으로 내려갈수록 1씩 증가함
        final long depth = (parent == null) ? 0 : parent.getCategoryDepth() + 1;
        final Category category = Category.builder()
                .category(parent)
                .categoryName(request.categoryName())
                .categoryDepth(depth)
                .build();

        final Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    //todo 리팩토링 해야함 메모
    public List<FindCategoryResponse> findAllCategory() {
        final List<Category> categoryList = categoryRepository.findAll();
        Map<Long, FindCategoryResponse> findCategoryResponseMap = new HashMap<>();

        categoryList.forEach(category ->
                findCategoryResponseMap.put(category.getId(),
                        FindCategoryResponse.of(category.getId(), category.getCategoryDepth(), category.getCategoryName()))
        );

        categoryList.forEach(category -> {
            if (category.getCategory() != null) {
                findCategoryResponseMap.get(category.getCategory().getId()).addChild(findCategoryResponseMap.get(category.getId()));
            }
        });

        final List<FindCategoryResponse> findCategoryResponseList =
                findCategoryResponseMap.values().stream()
                        .filter(FindCategoryResponse -> FindCategoryResponse.categoryDepth() == 0)
                        .toList();
        return findCategoryResponseList;
    }

    public Category findByCategoryId(final Long categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return category;
    }

}
