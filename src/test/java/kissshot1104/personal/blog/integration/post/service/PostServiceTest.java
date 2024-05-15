package kissshot1104.personal.blog.integration.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.util.List;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.category.service.CategoryService;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.repository.MemberRepository;
import kissshot1104.personal.blog.post.dto.request.CreatePostRequest;
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
    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    void setUp() {

        memberRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();

        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1;").executeUpdate();
        em.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1;").executeUpdate();
        em.createNativeQuery("ALTER TABLE post AUTO_INCREMENT = 1;").executeUpdate();

        member = Member.builder()
                .id(1L)
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
        postRepository.saveAll(List.of(post1, post2, post3));
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
}
