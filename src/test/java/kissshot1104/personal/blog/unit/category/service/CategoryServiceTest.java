package kissshot1104.personal.blog.unit.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
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

//    @Test
//    @DisplayName("부모 카테고리가 존재하지 않으면 예외가 발생한다.")
//    void createCategoryParentCategoryNotFoundException() {
//
//        final ModifyCategoryRequest createCategoryRequest =
//                new ModifyCategoryRequest(null, "Test Parent Category Name", 9999L);
//
//        given(categoryRepository.findById(any()))
//                .willThrow(new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
//
//        assertThatThrownBy(() -> categoryService.addCategory(createCategoryRequest))
//                .isInstanceOf(BusinessException.class)
//                .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage());
//    }
//
//    //
////    //todo 어떤 예외가 발생하는지 자세하게 알아보고 변경해야함
////    @Test
////    @DisplayName("카테고리 이름이 없다면 예외가 발생한다.")
////    void createCategoryEmptyCategoryNameException() {
////
////        final CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
////                .parentCategoryId(null)
////                .categoryName(null)
////                .build();
////
////        assertThatThrownBy(() -> categoryService.createCategory(createCategoryRequest, member))
////                .isInstanceOf(ConstraintViolationException.class);
////    }
////
//    @Test
//    @DisplayName("root 카테고리를 만든다.")
//    void createRootCategory() {
//        final ModifyCategoryRequest createCategoryRequest =
//                new ModifyCategoryRequest(null, "Test Parent Category Name", null);
//
//        final Category parentCategory = Category.builder()
//                .id(1L)
//                .category(null)
//                .categoryName("Test Parent Category Name")
//                .categoryDepth(0L)
//                .build();
//
//        lenient().when(categoryRepository.findById(any())).thenReturn(Optional.of(parentCategory));
//        given(categoryRepository.save(any())).willReturn(parentCategory);
//
//        assertThat(categoryService.addCategory(createCategoryRequest))
//                .extracting("id",
//                        "category",
//                        "categoryName",
//                        "categoryDepth")
//                .contains(1L,
//                        null,
//                        "Test Parent Category Name",
//                        0L
//                );
//    }
//
//    @Test
//    @DisplayName("자식 카테고리를 만든다.")
//    void createChildCategory() {
//
//        final Category parentCategory = Category.builder()
//                .id(1L)
//                .category(null)
//                .categoryName("Test Parent Category Name")
//                .categoryDepth(0L)
//                .build();
//
//        lenient().when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
//
//        final Category childCategory = Category.builder()
//                .id(2L)
//                .category(parentCategory)
//                .categoryName("Test Child Category Name")
//                .categoryDepth(1L)
//                .build();
//        given(categoryRepository.save(any())).willReturn(childCategory);
//
//        Category result = categoryService.addCategory(new ModifyCategoryRequest(null, "Test Child Category Name", 1L));
//        assertThat(result)
//                .extracting("id", "category", "categoryName", "categoryDepth")
//                .contains(2L, parentCategory, "Test Child Category Name", 1L);
//    }

    @Test
    @DisplayName("모든 카테고리를 조회한다.")
    public void findAllCategory() {

        given(categoryRepository.findAll())
                .willReturn(List.of(category1, category2, category3));

        List<FindCategoryResponse> FindCategoryResponseList = categoryService.findAllCategory();

        assertThat(FindCategoryResponseList.size()).isEqualTo(2);
        assertThat(FindCategoryResponseList.get(0)).isNotNull();
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

        given(categoryRepository.findById(9999L))
                .willThrow(new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
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

        given(categoryRepository.findById(1L))
                .willReturn(Optional.of(category1));
        given(categoryRepository.findById(2L))
                .willReturn(Optional.of(category2));
        given(categoryRepository.findById(3L))
                .willReturn(Optional.of(category3));
        //when
        categoryService.saveCategoryChanges(modifyCategoryRequestList, member);

        //then
        assertThat(List.of(category1, category2, category3))
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

        given(categoryRepository.findById(1L))
                .willReturn(Optional.of(category1));
        given(categoryRepository.findById(2L))
                .willReturn(Optional.of(category2));
        given(categoryRepository.findById(3L))
                .willReturn(Optional.of(category3));
        //when
        categoryService.saveCategoryChanges(modifyCategoryRequestList, member);

        //then
        assertThat(List.of(category1, category2, category3))
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

        given(categoryRepository.findById(1L))
                .willReturn(Optional.of(category1));
        given(categoryRepository.findById(3L))
                .willReturn(Optional.of(category3));
        given(categoryRepository.findAllByIdNotIn(any()))
                .willReturn(List.of(category2));
        //when
        categoryService.saveCategoryChanges(modifyCategoryRequestList, member);

        verify(categoryRepository, times(1)).findAllByIdNotIn(any());
    }

}
