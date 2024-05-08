package kissshot1104.personal.blog.integration.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import kissshot1104.personal.blog.category.dto.request.CreateCategoryRequest;
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
        em.createNativeQuery("ALTER TABLE your_table_name AUTO_INCREMENT = 1");

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

        final CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
                .parentCategoryId(999L)
                .categoryName(null)
                .build();

        assertThatThrownBy(() -> categoryService.createCategory(createCategoryRequest, member))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage());
    }

    @Test
    @DisplayName("root 카테고리를 만든다.")
    void createRootCategory() {
        final CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
                .parentCategoryId(null)
                .categoryName("Test Parent Category Name")
                .build();

        assertThat(categoryService.createCategory(createCategoryRequest, member))
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

        final CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
                .parentCategoryId(1L)
                .categoryName("Test Child Category Name")
                .build();

        Category result = categoryService.createCategory(createCategoryRequest, new Member());
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
}
