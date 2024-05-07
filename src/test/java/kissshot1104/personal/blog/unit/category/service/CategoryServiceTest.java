package kissshot1104.personal.blog.unit.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Member member;

    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {
        category1 = Category.builder()
                .id(1L)
                .category(null)
                .categoryDepth(0L)
                .categoryName("Test Category Name1")
                .build();

        category2 = Category.builder()
                .id(2L)
                .category(category1)
                .categoryDepth(1L)
                .categoryName("Test Category Name2")
                .build();

        category3 = Category.builder()
                .id(3L)
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

        given(categoryRepository.findById(any()))
                .willThrow(new BusinessException(ErrorCode.INVALID_INPUT_VALUE));

        assertThatThrownBy(() -> categoryService.createCategory(createCategoryRequest, member))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage());
    }

    //todo 어떤 예외가 발생하는지 자세하게 알아보고 변경해야함
//    @Test
//    @DisplayName("카테고리 이름이 없다면 예외가 발생한다.")
//    void createCategoryEmptyCategoryNameException() {
//
//        final CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
//                .parentCategoryId(null)
//                .categoryName(null)
//                .build();
//
//        assertThatThrownBy(() -> categoryService.createCategory(createCategoryRequest, member))
//                .isInstanceOf(ConstraintViolationException.class);
//    }

    @Test
    @DisplayName("root 카테고리를 만든다.")
    void createRootCategory() {
        final CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
                .parentCategoryId(null)
                .categoryName("Test Category Name")
                .build();

        final Category parentCategory = Category.builder()
                .id(1L)
                .category(null)
                .categoryName("Test Parent Category Name")
                .categoryDepth(0L)
                .build();

        lenient().when(categoryRepository.findById(any())).thenReturn(Optional.of(parentCategory));
        given(categoryRepository.save(any())).willReturn(parentCategory);

        assertThat(categoryService.createCategory(createCategoryRequest, member))
                .extracting("id",
                        "category",
                        "categoryName",
                        "categoryDepth")
                .contains(1L,
                        null,
                        "Test Parent Category Name",
                        0L
                );
    }

    @Test
    @DisplayName("자식 카테고리를 만든다.")
    void createChildCategory() {

        final Category parentCategory = Category.builder()
                .id(1L)
                .category(null)
                .categoryName("Test Parent Category Name")
                .categoryDepth(0L)
                .build();

        given(categoryRepository.findById(1L)).willReturn(Optional.of(parentCategory));

        final Category childCategory = Category.builder()
                .id(2L)
                .category(parentCategory)
                .categoryName("Test Child Category Name")
                .categoryDepth(1L)
                .build();
        given(categoryRepository.save(any())).willReturn(childCategory);

        Category result = categoryService.createCategory(new CreateCategoryRequest(1L, "Test Child Category Name"), new Member());
        assertThat(result)
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(2L, parentCategory, "Test Child Category Name", 1L);
    }

    @Test
    @DisplayName("모든 카테고리를 조회한다.")
    public void findAllCategory() {

        given(categoryRepository.findAll())
                .willReturn(List.of(category1, category2, category3));

        List<FindCategoryResponse> FindCategoryResponseList = categoryService.findAllCategory();

        assertThat(FindCategoryResponseList.size()).isEqualTo(2);
        assertThat(FindCategoryResponseList.get(0)).isNotNull();
    }
}
