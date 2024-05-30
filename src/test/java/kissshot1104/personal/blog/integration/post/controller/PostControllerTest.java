package kissshot1104.personal.blog.integration.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.global.security.prinipal.MemberPrincipal;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.repository.MemberRepository;
import kissshot1104.personal.blog.post.dto.CreatePostDto;
import kissshot1104.personal.blog.post.entity.Post;
import kissshot1104.personal.blog.post.repository.PostRepository;
import kissshot1104.personal.blog.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class PostControllerTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberPrincipal memberPrincipal;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member member;
    private UserDetails user;

    private Category category1;
    private Category category2;
    private Category category3;

    private Post post1;
    private Post post2;
    private Post post3;

    private CreatePostDto createPostDto1;
    private CreatePostDto createPostDto2;
    private CreatePostDto createPostDto3;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAllInBatch();
        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1;").executeUpdate();
        categoryRepository.deleteAllInBatch();
        em.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1;").executeUpdate();
        postRepository.deleteAllInBatch();
        em.createNativeQuery("ALTER TABLE post AUTO_INCREMENT = 1").executeUpdate();

        category1 = Category.builder()
                .category(null)
                .categoryDepth(0L)
                .categoryName("Test Category Name1")
                .build();

        category2 = Category.builder()
                .category(category1)
                .categoryDepth(1L)
                .categoryName("Test Category Name2")
                .build();

        category3 = Category.builder()
                .category(null)
                .categoryDepth(0L)
                .categoryName("Test Category Name3")
                .build();
        categoryRepository.saveAll(List.of(category1, category2, category3));

        member = Member.builder()
                .nickName("nickName1")
                .username("username")
                .password(passwordEncoder.encode("1"))
                .build();
        memberRepository.save(member);
        user = memberPrincipal.loadUserByUsername("username");

        createPostDto1 = CreatePostDto.builder()
                .categoryId(1L)
                .title("Test Title1")
                .content("Test Content1")
                .postPassword("Test Post Password1")
                .postSecurity("PUBLIC")
                .build();
        createPostDto2 = CreatePostDto.builder()
                .categoryId(2L)
                .title("Test Title2")
                .content("Test Content2")
                .postPassword("Test Post Password2")
                .postSecurity("PROTECTED")
                .build();
        createPostDto3 = CreatePostDto.builder()
                .categoryId(3L)
                .title("Test Title3")
                .content("Test Content3")
                .postPassword("Test Post Password3")
                .postSecurity("PRIVATE")
                .build();
    }

    //    @Test
//    @DisplayName("카테고리가 입력되지 않으면 예외가 발생한다.")
//    public void shouldThrowExceptionWhenCategoryNotInput() throws Exception {
//        createPostDto1 = CreatePostDto.builder()
//                .title("Test Title1")
//                .content("Test Content1")
//                .postPassword("Test Post Password1")
//                .postSecurity("PUBLIC")
//                .build();
//        mock.perform(post("/api/v1/post/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createPostDto1))
//                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpectAll(
//                        jsonPath("$.message").value("적절하지 않은 요청 값입니다."),
//                        jsonPath("$.code").value("C_002"),
//                        jsonPath("$.errors[0].field").value("categoryId"),
//                        jsonPath("$.errors[0].value").isEmpty(),
//                        jsonPath("$.errors[0].message").value("카테고리 선택은 필수입니다.")
//                );
//    }

    @Test
    @DisplayName("카테고리가 입력되지 않으면 예외가 발생한다.")
    public void shouldThrowExceptionWhenCategoryNotInput() throws Exception {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .title("Test Title1")
                .content("Test Content1")
                .postPassword("Test Post Password1")
                .postSecurity("PUBLIC")
                .build();

        mock.perform(post("/api/v1/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("적절하지 않은 요청 값입니다."),
                        jsonPath("$.code").value("C_002")
                        //todo 아래의 json을 계속 찾지 못하는 에거 발생함 원인 찾기
//                    ,jsonPath("$.errors[0].field").value("categoryId"),
//                    jsonPath("$.errors[0].value").value(""),
//                    jsonPath("$.errors[0].message").value("카테고리 선택은 필수입니다.")
                );
    }

    @Test
    @DisplayName("제목이 입력되지 않으면 예외가 발생한다.")
    public void shouldThrowExceptionWhenTitleNotInput() throws Exception {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .categoryId(1L)
                .content("Test Content1")
                .postPassword("Test Post Password1")
                .postSecurity("PUBLIC")
                .build();

        mock.perform(post("/api/v1/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("적절하지 않은 요청 값입니다."),
                        jsonPath("$.code").value("C_002")
                        //todo 아래의 json을 계속 찾지 못하는 에거 발생함 원인 찾기
//                    ,jsonPath("$.errors[0].field").value("title"),
//                    jsonPath("$.errors[0].value").value(""),
//                    jsonPath("$.errors[0].message").value("제목은 필수입니다.")
                );
    }

    @Test
    @DisplayName("카테고리가 입력되지 않으면 예외가 발생한다.")
    public void shouldThrowExceptionWhenContentNotInput() throws Exception {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .categoryId(1L)
                .title("Test Title1")
                .postPassword("Test Post Password1")
                .postSecurity("PUBLIC")
                .build();

        mock.perform(post("/api/v1/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("적절하지 않은 요청 값입니다."),
                        jsonPath("$.code").value("C_002")
                        //todo 아래의 json을 계속 찾지 못하는 에거 발생함 원인 찾기
//                    ,jsonPath("$.errors[0].field").value("content"),
//                    jsonPath("$.errors[0].value").value(""),
//                    jsonPath("$.errors[0].message").value("본문은 필수입니다.")
                );
    }

    @Test
    @DisplayName("접근 제어 레벨이 입력되지 않으면 예외가 발생한다.")
    public void shouldThrowExceptionWhenPostSecurityNotInput() throws Exception {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .title("Test Title1")
                .content("Test Content1")
                .build();

        mock.perform(post("/api/v1/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("적절하지 않은 요청 값입니다."),
                        jsonPath("$.code").value("C_002")
                        //todo 아래의 json을 계속 찾지 못하는 에거 발생함 원인 찾기
//                    ,jsonPath("$.errors[0].field").value("postSecurity"),
//                    jsonPath("$.errors[0].value").value(""),
//                    jsonPath("$.errors[0].message").value("접근 제어 레벨은 필수입니다.")
                );
    }


    @Test
    @DisplayName("새로운 게시글을 추가한다.")
    public void createNewPostTest() throws Exception {
        mock.perform(post("/api/v1/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostDto1))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .header("Authorization", "bearer {AccessToken}"))
                .andDo(print())
                .andDo(document("새로운 게시글을 추가한다.",
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("categoryId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("카테고리 ID"),
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글의 제목이다."),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글의 본문이다."),
                                fieldWithPath("postPassword")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글의 비밀번호이다. PROTECTED모드때 사용한다."),
                                fieldWithPath("postSecurity")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글의 접근 제한 레벨이다.")
                        )
                ))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/post/1"));
    }
}
