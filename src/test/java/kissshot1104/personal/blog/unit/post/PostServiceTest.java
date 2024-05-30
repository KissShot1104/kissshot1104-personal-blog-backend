package kissshot1104.personal.blog.unit.post;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.category.service.CategoryService;
import kissshot1104.personal.blog.global.exception.AuthException;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.request.AuthenticationDataRequest;
import kissshot1104.personal.blog.post.dto.request.CreatePostRequest;
import kissshot1104.personal.blog.post.dto.response.FindPostResponse;
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

    private Post publicPost;
    private Post protectedPost;
    private Post privatePost;
    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {

        member = Member.builder()
                .nickName("nickName1")
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

        publicPost = Post.builder()
                .id(1L)
                .category(category1)
                .member(member)
                .title("title1")
                .content("content1")
                .postPassword("password1")
                .postSecurity(PostSecurity.PUBLIC)
                .build();
        protectedPost = Post.builder()
                .id(2L)
                .category(category2)
                .member(member)
                .title("title2")
                .content("content2")
                .postPassword("password2")
                .postSecurity(PostSecurity.PROTECTED)
                .build();
        privatePost = Post.builder()
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
                .willReturn(publicPost);

        assertThat(postService.createPost(createPostRequest, member))
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("protected게시글을 조회 시 비밀번호가 틀리면 예외가 발생한다.")
    public void authExceptionWhenInvalidPassword() {
        final Member member2 = Member.builder()
                .username("username2")
                .password("password2")
                .roles("ROLE_USER")
                .build();

        final AuthenticationDataRequest authenticationDataRequest = AuthenticationDataRequest.builder()
                .postPassword("password1")
                .build();

        given(postRepository.findById(any()))
                .willReturn(Optional.of(protectedPost));

        assertThatThrownBy(() -> postService.findPost(2L, authenticationDataRequest, member2))
                .isInstanceOf(AuthException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("protected 게시글을 조회 시 작성자는 비밀번호를 입력하지 않아도 된다.")
    public void findPostIfAuthorOrValidPassword() {
        given(postRepository.findById(any()))
                .willReturn(Optional.of(protectedPost));

        final FindPostResponse findPostResponse =
                postService.findPost(2L, null, member);

        assertThat(findPostResponse)
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(2L, "category2", "nickName1", "title2", "content2", "PROTECTED");
    }

    @Test
    @DisplayName("private 게시글은 작성자가 아니면 조회할 수 없다.")
    public void canNotViewPrivatePostUnlessAuthor() {
        final Member member2 = Member.builder()
                .username("username2")
                .password("password2")
                .roles("ROLE_USER")
                .build();

        given(postRepository.findById(any()))
                .willReturn(Optional.of(privatePost));

        assertThatThrownBy(() -> postService.findPost(3L, null, member2))
                .isInstanceOf(AuthException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("public 게시글을 조회한다.")
    public void findPublicPostTest() {
        final Member member2 = Member.builder()
                .username("username2")
                .password("password2")
                .roles("ROLE_USER")
                .build();

        given(postRepository.findById(any()))
                .willReturn(Optional.of(publicPost));

        final FindPostResponse findPostResponse =
                postService.findPost(1L, null, member2);

        assertThat(findPostResponse)
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(1L, "category1", "nickName1", "title1", "content1", "PUBLIC");
    }

    @Test
    @DisplayName("protected 게시글을 조회한다.")
    public void findProtectedPostTest() {
        final Member member2 = Member.builder()
                .username("username2")
                .password("password2")
                .roles("ROLE_USER")
                .build();

        final AuthenticationDataRequest authenticationDataRequest = AuthenticationDataRequest.builder()
                .postPassword("password2")
                .build();

        given(postRepository.findById(any()))
                .willReturn(Optional.of(protectedPost));

        final FindPostResponse findPostResponse =
                postService.findPost(2L, authenticationDataRequest, member2);

        assertThat(findPostResponse)
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(2L, "category2", "nickName1", "title2", "content2", "PROTECTED");
    }

    @Test
    @DisplayName("private 게시글을 조회한다.")
    public void findPrivatePostTest() {
        given(postRepository.findById(any()))
                .willReturn(Optional.of(privatePost));

        final FindPostResponse response = postService.findPost(3L, null, member);
        assertThat(response)
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(3L, "category3", "nickName1", "title3", "content3", "PRIVATE");
    }
}
