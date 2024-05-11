package kissshot1104.personal.blog.category.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
//todo 리팩토링 해야함 메모
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public List<FindCategoryResponse> findAllCategory() {
        final List<Category> categoryList = categoryRepository.findAll();
        final Map<Long, FindCategoryResponse> findCategoryResponseMap = new HashMap<>();

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
    public void saveCategoryChanges(final List<ModifyCategoryRequest> requests,
                                    final Member member) {
        final List<Category> modifiedCategories = new ArrayList<>();
        //todo createOrderMapCategories에 ModifyCategoryRequest가 아닌
        // createCategoryRequest로 변환 후 매개변수로 넘겨줘야할지
        final Map<Long, Category> categoryMap = createOrMapCategories(requests, modifiedCategories);

        requests.forEach(request -> {

            final Category category = updateCategory(request, categoryMap);
            modifiedCategories.add(category);

        });
        deleteUnreferencedCategories(modifiedCategories);
        updateCategoryDepth(modifiedCategories);
    }

    private Map<Long, Category> createOrMapCategories(final List<ModifyCategoryRequest> requests,
                                                      final List<Category> modifiedCategories) {
        final Map<Long, Category> categoryMap = new HashMap<>();
        requests.stream().filter(ModifyCategoryRequest::isNewCategory)
                .forEach(request -> {
                    final Category category = addCategory(request);
                    modifiedCategories.add(category);
                    categoryMap.put(request.subCategoryId(), category);
                });
        return categoryMap;
    }

    public Category updateCategory(final ModifyCategoryRequest request, final Map<Long, Category> categoryMap) {
        final Category category = findCategory(request, categoryMap);
        final Category newParent = determineNewParent(request, categoryMap);
        category.modifyCategory(newParent, request.categoryName());
        return category;
    }

    private Category findCategory(final ModifyCategoryRequest request, final Map<Long, Category> categoryMap) {
        final Category category = Optional.ofNullable(request.categoryId())
                .map(categoryRepository::findById)
                .orElseGet(() -> Optional.ofNullable(categoryMap.get(request.subCategoryId())))
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return category;
    }

    private Category determineNewParent(final ModifyCategoryRequest request, final Map<Long, Category> categoryMap) {
        final Category category = Optional.ofNullable(request.parentCategoryId())
                .map(this::findByCategoryId)
                .orElseGet(() -> categoryMap.get(request.subParentCategoryId()));
        return category;
    }

    public Category addCategory(final ModifyCategoryRequest request) {
        final Category category = Category.builder()
                .categoryDepth(0L)
                .categoryName(request.categoryName())
                .build();
        final Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    public void deleteUnreferencedCategories(final List<Category> referencedCategories) {
        final List<Long> referencedIds = referencedCategories.stream()
                .map(Category::getId)
                .toList();
        final List<Category> toDelete = categoryRepository.findAllByIdNotIn(referencedIds);
        toDelete.forEach(this::deleteCategoryAndChildren);
    }

    private void deleteCategoryAndChildren(final Category category) {
        final List<Category> children = categoryRepository.findAllByCategory(category);
        children.forEach(this::deleteCategoryAndChildren);
        categoryRepository.delete(category);
    }

    private void updateCategoryDepth(final List<Category> categories) {
        for (final Category category : categories) {
            final Long categoryDepth = getCategoryDepth(category);
            category.modifyCategoryDepth(categoryDepth);
        }
    }

    private Long getCategoryDepth(Category category) {
        Long depth = 0L;
        while (category != null) {
            category = category.getCategory();
            depth++;
        }
        return depth - 1;
    }

    public Category findByCategoryId(Long categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return category;
    }
}
