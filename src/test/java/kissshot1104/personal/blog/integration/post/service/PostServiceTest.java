package kissshot1104.personal.blog.integration.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import jakarta.persistence.EntityManager;
import java.util.List;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.category.service.CategoryService;
import kissshot1104.personal.blog.global.exception.AuthException;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.repository.MemberRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class PostServiceTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private EntityManager em;

    private Member member;

    private Category category1;
    private Category category2;
    private Category category3;
    private Post publicPost;
    private Post protectedPost;
    private Post privatePost;

    @BeforeEach
    void setUp() {

        memberRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();

        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1;").executeUpdate();
        em.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1;").executeUpdate();
        em.createNativeQuery("ALTER TABLE post AUTO_INCREMENT = 1;").executeUpdate();

        member = Member.builder()
                .nickName("nickName1")
                .username("username")
                .password("password")
                .roles("ROLE_ADMIN")
                .build();
        memberRepository.save(member);

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
        categoryRepository.saveAll(List.of(category1, category2, category3));

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
        postRepository.saveAll(List.of(publicPost, protectedPost, privatePost));
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
    @DisplayName("잘못된 카테고리를 입력시 예외가 발생한다.")
    public void createPostInvalidCategoryName() {
        final CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .postSecurity("PUBLIC")
                .categoryId(9999L)
                .build();

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

        assertThat(postService.createPost(createPostRequest, member))
                .isEqualTo(4L);
        final Post post = postRepository.findById(4L).get();
        assertThat(post)
                .extracting("id", "category", "member", "title", "content", "postPassword", "postSecurity")
                .contains(4L, category1, member, "title1", "content1", "password1", PostSecurity.PUBLIC);
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

        assertThatThrownBy(() -> postService.findPost(2L, authenticationDataRequest, member2))
                .isInstanceOf(AuthException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("protected 게시글을 조회 시 작성자는 비밀번호를 입력하지 않아도 된다.")
    public void findPostIfAuthorOrValidPassword() {
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

        final FindPostResponse findPostResponse =
                postService.findPost(2L, authenticationDataRequest, member2);

        assertThat(findPostResponse)
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(2L, "category2", "nickName1", "title2", "content2", "PROTECTED");
    }

    @Test
    @DisplayName("private 게시글을 조회한다.")
    public void findPrivatePostTest() {
        final FindPostResponse response = postService.findPost(3L, null, member);
        assertThat(response)
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(3L, "category3", "nickName1", "title3", "content3", "PRIVATE");
    }
}
