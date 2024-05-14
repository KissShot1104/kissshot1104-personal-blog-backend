package kissshot1104.personal.blog.unit.post;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import kissshot1104.personal.blog.category.dto.request.CreateCategoryRequest;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.category.service.CategoryService;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.request.CreatePostRequest;
import kissshot1104.personal.blog.post.entity.Post;
import kissshot1104.personal.blog.post.entity.PostSecurity;
import kissshot1104.personal.blog.post.repository.PostRepository;
import kissshot1104.personal.blog.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private PostService postService;

    private Member member;

    private Post post1;
    private Post post2;
    private Post post3;
    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {

        member = Member.builder()
                .id(1L)
                .username("username")
                .password("password")
                .roles("ROLE_ADMIN")
                .build();
        category1 = Category.builder()
                .id(1L)
                .category(null)
                .categoryName("category1")
                .categoryDepth(0L)
                .build();
        category2 = Category.builder()
                .id(2L)
                .category(category1)
                .categoryName("category2")
                .categoryDepth(1L)
                .build();
        category3 = Category.builder()
                .id(3L)
                .category(null)
                .categoryName("category3")
                .categoryDepth(0L)
                .build();

        post1 = Post.builder()
                .id(1L)
                .category(category1)
                .member(member)
                .title("title1")
                .content("content1")
                .postPassword("password1")
                .postSecurity(PostSecurity.PUBLIC)
                .build();
        post2 = Post.builder()
                .id(2L)
                .category(category2)
                .member(member)
                .title("title2")
                .content("content2")
                .postPassword("password2")
                .postSecurity(PostSecurity.PROTECTED)
                .build();
        post3 = Post.builder()
                .id(3L)
                .category(category3)
                .member(member)
                .title("title3")
                .content("content3")
                .postPassword("password3")
                .postSecurity(PostSecurity.PRIVATE)
                .build();
    }

    @Test
    @DisplayName("잘못된 접근 제어를 입력시 예외가 발생한다.")
    public void createPostInvalidAccessType() {
        final CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .postSecurity("invalid Security")
                .build();

        assertThatThrownBy(() -> postService.createPost(createPostRequest, member))
                .isInstanceOf(BusinessException.class)
                .hasMessage("존재하지 않는 값을 요청했습니다.");
    }

    @Test
    @DisplayName("잘못된 접근 제어를 입력시 예외가 발생한다.")
    public void createPostInvalidCategoryName() {
        final CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .postSecurity("PUBLIC")
                .categoryId(9999L)
                .build();

        given(categoryService.findByCategoryId(any()))
                .willThrow(new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        assertThatThrownBy(() -> postService.createPost(createPostRequest, member))
                .isInstanceOf(BusinessException.class)
                .hasMessage("지정한 Entity를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("게시글을 추가한다.")
    public void createPost() {
        final CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .categoryId(1L)
                .title("title1")
                .content("content1")
                .postPassword("password1")
                .postSecurity("PUBLIC")
                .build();

        given(categoryService.findByCategoryId(any()))
                .willReturn(category1);

        given(postRepository.save(any()))
                .willReturn(post1);

        assertThat(postService.createPost(createPostRequest, member))
                .isEqualTo(1L);
    }
}
