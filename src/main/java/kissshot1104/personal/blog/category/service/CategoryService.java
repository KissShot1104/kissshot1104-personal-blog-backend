package kissshot1104.personal.blog.category.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kissshot1104.personal.blog.category.dto.request.ModifyCategoryRequest;
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

    //todo 리팩토링 해야함 메모
    public List<FindCategoryResponse> findAllCategory() {
        final List<Category> categoryList = categoryRepository.findAll();
        Map<Long, FindCategoryResponse> findCategoryResponseMap = new HashMap<>();

        categoryList.forEach(category ->
                findCategoryResponseMap.put(category.getId(),
                        FindCategoryResponse.of(category.getId(), category.getCategoryDepth(),
                                category.getCategoryName()))
        );

        categoryList.forEach(category -> {
            if (category.getCategory() != null) {
                findCategoryResponseMap.get(category.getCategory().getId())
                        .addChild(findCategoryResponseMap.get(category.getId()));
            }
        });

        final List<FindCategoryResponse> findCategoryResponseList =
                findCategoryResponseMap.values().stream()
                        .filter(FindCategoryResponse -> FindCategoryResponse.categoryDepth() == 0)
                        .toList();
        return findCategoryResponseList;
    }

    @Transactional
    public void saveCategoryChanges(List<ModifyCategoryRequest> modifyCategoryRequests, Member member) {
        List<Category> modifyedCategories = new ArrayList<>();
        for (final ModifyCategoryRequest modifyCategoryRequest : modifyCategoryRequests) {
            Category category = null;
            if (modifyCategoryRequest.categoryId() == null) {
                category = addCategory(modifyCategoryRequest);
            } else if (modifyCategoryRequest.categoryId() != null) {
                category = modifyCatgory(modifyCategoryRequest);
            }
            modifyedCategories.add(category);
        }
        deleteCategory(modifyedCategories);
        //깊이 다시 계산
        for (final Category category : modifyedCategories) {
            category.modifyCategoryDepth(calcCategoryDepth(category));
        }
    }

    public Category modifyCatgory(final ModifyCategoryRequest modifyCategoryRequest) {
        Category category = findByCategoryId(modifyCategoryRequest.categoryId());

        Category newParent = null;
        if (modifyCategoryRequest.parentCategoryId() != null) {
            newParent = categoryRepository.findById(modifyCategoryRequest.parentCategoryId())
                    .orElse(null);
        }
        changeParent(category, newParent, modifyCategoryRequest.categoryName());
        category.modifyCategory(newParent, modifyCategoryRequest.categoryName());

        return category;
    }

    public Category addCategory(final ModifyCategoryRequest categoryRequest) {
        Category parentCategory = null;
        if (categoryRequest.parentCategoryId() != null) {
            parentCategory = findByCategoryId(categoryRequest.parentCategoryId());
        }
        final Category category = Category.builder()
                .category(parentCategory)
                .categoryDepth(0L)
                .categoryName(categoryRequest.categoryName())
                .build();
        final Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    public void deleteCategory(final List<Category> categories) {
        final List<Long> categoryIds = categories.stream()
                .map(Category::getId)
                .toList();
        final List<Category> categoriesToDelete = new ArrayList<>(categoryRepository.findAllByIdNotIn(categoryIds));

        for (final Category category : categoriesToDelete) {
            if (category != null) {
                deleteChildren(category);
                categoryRepository.delete(category);
            }
        }
    }

    private void deleteChildren(Category parent) {
        List<Category> categories = categoryRepository.findAllByCategory(parent);
        for (Category child : categories) {
            deleteChildren(child);
            categoryRepository.delete(child);
        }
    }

    public Category findByCategoryId(final Long categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return category;
    }

    private Long calcCategoryDepth(Category category) {
        Long depth = 0L;
        while (category != null) {
            category = category.getCategory();
            depth++;
        }
        return depth - 1;
    }

    public void changeParent(Category category, Category newParent, String newName) {
        if (category != null && newParent != category.getCategory()) {
            if (category.getCategory() != null) {
                category.getCategory().removeChildInternal(category);
            }
            category.modifyCategory(newParent, newName);
            if (newParent != null) {
                newParent.addChildInternal(category);
            }
        }
    }


}
