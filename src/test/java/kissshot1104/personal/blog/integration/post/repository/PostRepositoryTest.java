package kissshot1104.personal.blog.integration.post.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import jakarta.persistence.EntityManager;
import java.util.List;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.repository.MemberRepository;
import kissshot1104.personal.blog.post.dto.response.FindPostResponse;
import kissshot1104.personal.blog.post.entity.Post;
import kissshot1104.personal.blog.post.entity.PostSecurity;
import kissshot1104.personal.blog.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    private Member member1;
    private Member member2;

    private Category category1;
    private Category category2;
    private Category category3;
    private Post publicPost1;
    private Post protectedPost1;
    private Post privatePost1;
    private Post publicPost2;
    private Post protectedPost2;
    private Post privatePost2;

    @BeforeEach
    void setUp() {

        memberRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();

        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1;").executeUpdate();
        em.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1;").executeUpdate();
        em.createNativeQuery("ALTER TABLE post AUTO_INCREMENT = 1;").executeUpdate();

        member1 = Member.builder()
                .nickName("nickName1")
                .username("username")
                .password("password")
                .roles("ROLE_USER")
                .build();
        member2 = Member.builder()
                .nickName("nickName2")
                .username("username2")
                .password("password2")
                .roles("ROLE_USER")
                .build();
        memberRepository.saveAll(List.of(member1, member2));

        category1 = Category.builder()
                .category(null)
                .categoryName("category1")
                .categoryDepth(0L)
                .build();
        category2 = Category.builder()
                .category(category1)
                .categoryName("category2")
                .categoryDepth(1L)
                .build();
        category3 = Category.builder()
                .category(null)
                .categoryName("category3")
                .categoryDepth(0L)
                .build();
        categoryRepository.saveAll(List.of(category1, category2, category3));

        publicPost1 = Post.builder()
                .category(category1)
                .member(member1)
                .title("title1")
                .content("content1")
                .postPassword("password1")
                .postSecurity(PostSecurity.PUBLIC)
                .build();
        protectedPost1 = Post.builder()
                .category(category2)
                .member(member1)
                .title("title2")
                .content("content2")
                .postPassword("password2")
                .postSecurity(PostSecurity.PROTECTED)
                .build();
        privatePost1 = Post.builder()
                .category(category3)
                .member(member1)
                .title("title3")
                .content("content3")
                .postPassword("password3")
                .postSecurity(PostSecurity.PRIVATE)
                .build();
        publicPost2 = Post.builder()
                .category(category1)
                .member(member2)
                .title("title4")
                .content("content4")
                .postPassword("password4")
                .postSecurity(PostSecurity.PUBLIC)
                .build();
        protectedPost2 = Post.builder()
                .category(category2)
                .member(member2)
                .title("title5")
                .content("content5")
                .postPassword("password5")
                .postSecurity(PostSecurity.PROTECTED)
                .build();
        privatePost2 = Post.builder()
                .category(category3)
                .member(member2)
                .title("title6")
                .content("content6")
                .postPassword("password6")
                .postSecurity(PostSecurity.PRIVATE)
                .build();
        postRepository.saveAll(List.of(publicPost1, protectedPost1, privatePost1,
                publicPost2, protectedPost2, privatePost2));
    }

    @Test
    @DisplayName("private 게시글은 작성자가 아니면 조회할 수 없다.")
    public void canNotViewPrivatePostUnlessAuthor() {
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<FindPostResponse> responses =
                postRepository.findAllByKeyword(null,
                        null,
                        null,
                        pageable,
                        member1);

        assertThat(responses.getContent())
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(tuple(1L, "category1", "nickName1", "title1", "content1", "PUBLIC"),
                        tuple(2L, "category2", "nickName1", "title2", "content2", "PROTECTED"),
                        tuple(3L, "category3", "nickName1", "title3", "content3", "PRIVATE"),
                        tuple(4L, "category1", "nickName2", "title4", "content4", "PUBLIC"),
                        tuple(5L, "category2", "nickName2", "title5", "content5", "PROTECTED"));
    }

    @Test
    @DisplayName("private 게시글의 작성자는 자신의 private게시글은 조회할 수 있다.")
    public void canViewPrivatePostUnlessAuthor() {
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<FindPostResponse> responses =
                postRepository.findAllByKeyword(null,
                        null,
                        null,
                        pageable,
                        member1);

        assertThat(responses.getContent())
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(tuple(1L, "category1", "nickName1", "title1", "content1", "PUBLIC"),
                        tuple(2L, "category2", "nickName1", "title2", "content2", "PROTECTED"),
                        tuple(3L, "category3", "nickName1", "title3", "content3", "PRIVATE"),
                        tuple(4L, "category1", "nickName2", "title4", "content4", "PUBLIC"),
                        tuple(5L, "category2", "nickName2", "title5", "content5", "PROTECTED"));
    }

    @Test
    @DisplayName("조회 가능한 모든 게시글 중 제목을 or검색한다.")
    public void searchPostsByTitleWithOrCondition() {
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<FindPostResponse> responses =
                postRepository.findAllByKeyword(null,
                        "title",
                        "1",
                        pageable,
                        member1);

        assertThat(responses.getContent())
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(tuple(1L, "category1", "nickName1", "title1", "content1", "PUBLIC"));
    }

    @Test
    @DisplayName("모든 게시글 중 내용(본문)을 or검색한다.")
    public void searchPostsByContentWithOrCondition() {
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<FindPostResponse> responses =
                postRepository.findAllByKeyword(null,
                        "content",
                        "1",
                        pageable,
                        member1);

        assertThat(responses.getContent())
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(tuple(1L, "category1", "nickName1", "title1", "content1", "PUBLIC"));
    }

    @Test
    @DisplayName("조회 가능한 게시글 중 작성자를 or검색한다.")
    public void searchPostsByAuthorWithOrCondition() {
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<FindPostResponse> responses =
                postRepository.findAllByKeyword(null,
                        "author",
                        "1",
                        pageable,
                        member1);

        assertThat(responses.getContent())
                .extracting("postId", "category", "nickName", "title", "content", "postSecurity")
                .contains(tuple(1L, "category1", "nickName1", "title1", "content1", "PUBLIC"),
                        tuple(2L, "category2", "nickName1", "title2", "content2", "PROTECTED"),
                        tuple(3L, "category3", "nickName1", "title3", "content3", "PRIVATE"));
    }

}
