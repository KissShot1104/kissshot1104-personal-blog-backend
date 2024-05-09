package kissshot1104.personal.blog.integration.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import jakarta.persistence.EntityManager;
import java.util.List;
import kissshot1104.personal.blog.category.dto.request.ModifyCategoryRequest;
import kissshot1104.personal.blog.category.dto.response.FindCategoryResponse;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.category.service.CategoryService;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EntityManager em;

    private Member member;

    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    public void setUp() {
        categoryRepository.deleteAllInBatch();
        em.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1;").executeUpdate();

        category1 = Category.builder()
                .category(null)
                .categoryDepth(0L)
                .categoryName("Test Parent Category Name")
                .build();

        category2 = Category.builder()
                .category(category1)
                .categoryDepth(1L)
                .categoryName("Test Child Category Name")
                .build();

        category3 = Category.builder()
                .category(null)
                .categoryDepth(0L)
                .categoryName("Test Category Name3")
                .build();
        categoryRepository.saveAll(List.of(category1, category2, category3));
    }


    @Test
    @DisplayName("부모 카테고리가 존재하지 않으면 예외가 발생한다.")
    void createCategoryParentCategoryNotFoundException() {

        final ModifyCategoryRequest modifyCategoryRequest = ModifyCategoryRequest.builder()
                .parentCategoryId(999L)
                .categoryName(null)
                .build();

        assertThatThrownBy(() -> categoryService.saveCategoryChanges(List.of(modifyCategoryRequest), member))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("root 카테고리를 만든다.")
    void createRootCategory() {
        final ModifyCategoryRequest modifyCategoryRequest = ModifyCategoryRequest.builder()
                .parentCategoryId(null)
                .categoryName("Test Parent Category Name")
                .build();
        categoryService.saveCategoryChanges(List.of(modifyCategoryRequest), member);
        final Category result = categoryService.findByCategoryId(4L);

        assertThat(result)
                .extracting("id",
                        "category",
                        "categoryName",
                        "categoryDepth")
                .contains(4L,
                        null,
                        "Test Parent Category Name",
                        0L
                );
    }

    @Test
    @DisplayName("자식 카테고리를 만든다.")
    void createChildCategory() {
        //given
        final ModifyCategoryRequest modifyCategoryRequest1 = ModifyCategoryRequest.builder()
                .parentCategoryId(1L)
                .categoryName("Test Child Category Name")
                .build();

        final ModifyCategoryRequest modifyCategoryRequest2 = ModifyCategoryRequest.builder()
                .categoryId(1L)
                .categoryName("Test Modify Category Name1")
                .parentCategoryId(null)
                .build();

        final ModifyCategoryRequest modifyCategoryRequest3 = ModifyCategoryRequest.builder()
                .categoryId(2L)
                .categoryName("Test Modify Category Name2")
                .parentCategoryId(3L)
                .build();

        final ModifyCategoryRequest modifyCategoryRequest4 = ModifyCategoryRequest.builder()
                .categoryId(3L)
                .categoryName("Test Modify Category Name3")
                .parentCategoryId(1L)
                .build();

        final List<ModifyCategoryRequest> modifyCategoryRequests =
                List.of(modifyCategoryRequest1, modifyCategoryRequest2, modifyCategoryRequest3, modifyCategoryRequest4);

        categoryService.saveCategoryChanges(modifyCategoryRequests, new Member());
        final Category result = categoryService.findByCategoryId(4L);
        assertThat(result)
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(4L, category1, "Test Child Category Name", 1L);
    }

    @Test
    @DisplayName("모든 카테고리를 조회한다.")
    public void findAllCategoryTest() {
        List<FindCategoryResponse> FindCategoryResponseList = categoryService.findAllCategory();

        assertThat(FindCategoryResponseList.size()).isEqualTo(2);
        assertThat(FindCategoryResponseList.get(0).childCategoryList()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 Category를 조회하면 예외가 발생한다.")
    public void modifyCategoryEntityNotFoundException() {

        //given
        final ModifyCategoryRequest modifyCategoryRequest1 = ModifyCategoryRequest.builder()
                .categoryId(9999L)
                .categoryName("Test Modify Category Name1")
                .parentCategoryId(null)
                .build();

        final List<ModifyCategoryRequest> modifyCategoryRequestList =
                List.of(modifyCategoryRequest1);

        //then
        assertThatThrownBy(() -> categoryService.saveCategoryChanges(modifyCategoryRequestList, member))
                .isInstanceOf(BusinessException.class)
                .hasMessage("지정한 Entity를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("이름이 변한 모든 카테고리의 이름을 수정한다.")
    public void modifyCategoryName() {

        //given
        final ModifyCategoryRequest modifyCategoryRequest1 = ModifyCategoryRequest.builder()
                .categoryId(1L)
                .categoryName("Test Modify Category Name1")
                .parentCategoryId(null)
                .build();

        final ModifyCategoryRequest modifyCategoryRequest2 = ModifyCategoryRequest.builder()
                .categoryId(2L)
                .categoryName("Test Modify Category Name2")
                .parentCategoryId(1L)
                .build();

        final ModifyCategoryRequest modifyCategoryRequest3 = ModifyCategoryRequest.builder()
                .categoryId(3L)
                .categoryName("Test Modify Category Name3")
                .parentCategoryId(null)
                .build();

        final List<ModifyCategoryRequest> modifyCategoryRequestList =
                List.of(modifyCategoryRequest1, modifyCategoryRequest2, modifyCategoryRequest3);

        //when
        categoryService.saveCategoryChanges(modifyCategoryRequestList, member);

        //then
        assertThat(List.of(category1,category2, category3))
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(
                        tuple(1L, null, "Test Modify Category Name1", 0L),
                        tuple(2L, category1, "Test Modify Category Name2", 1L),
                        tuple(3L, null, "Test Modify Category Name3", 0L)
                );
    }

    @Test
    @DisplayName("이름이 변한 모든 카테고리의 부모카테고리와 깊이를 수정한다.")
    public void modifyCategoryParentCategory() {

        //given
        final ModifyCategoryRequest modifyCategoryRequest1 = ModifyCategoryRequest.builder()
                .categoryId(1L)
                .categoryName("Test Modify Category Name1")
                .parentCategoryId(null)
                .build();

        final ModifyCategoryRequest modifyCategoryRequest2 = ModifyCategoryRequest.builder()
                .categoryId(2L)
                .categoryName("Test Modify Category Name2")
                .parentCategoryId(3L)
                .build();

        final ModifyCategoryRequest modifyCategoryRequest3 = ModifyCategoryRequest.builder()
                .categoryId(3L)
                .categoryName("Test Modify Category Name3")
                .parentCategoryId(1L)
                .build();

        final List<ModifyCategoryRequest> modifyCategoryRequestList =
                List.of(modifyCategoryRequest1, modifyCategoryRequest2, modifyCategoryRequest3);

        //when
        categoryService.saveCategoryChanges(modifyCategoryRequestList, member);

        //then
        assertThat(List.of(category1,category2, category3))
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(
                        tuple(1L, null, "Test Modify Category Name1", 0L),
                        tuple(2L, category3, "Test Modify Category Name2", 2L),
                        tuple(3L, category1, "Test Modify Category Name3", 1L)
                );
    }

    @Test
    @DisplayName("카테고리들을 삭제한다.")
    public void deleteCategory() {

        //given
        final ModifyCategoryRequest modifyCategoryRequest1 = ModifyCategoryRequest.builder()
                .categoryId(1L)
                .categoryName("Test Modify Category Name1")
                .parentCategoryId(null)
                .build();

        final ModifyCategoryRequest modifyCategoryRequest3 = ModifyCategoryRequest.builder()
                .categoryId(3L)
                .categoryName("Test Modify Category Name3")
                .parentCategoryId(1L)
                .build();

        final List<ModifyCategoryRequest> modifyCategoryRequestList =
                List.of(modifyCategoryRequest1, modifyCategoryRequest3);

        //when
        categoryService.saveCategoryChanges(modifyCategoryRequestList, member);

        //then
        assertThat(categoryRepository.findAll())
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(
                        tuple(1L, null, "Test Modify Category Name1", 0L),
                        tuple(3L, category1, "Test Modify Category Name3", 1L)
                );

    }
}
