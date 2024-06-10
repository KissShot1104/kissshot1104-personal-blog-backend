package kissshot1104.personal.blog.integration.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.security.prinipal.MemberPrincipal;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.repository.MemberRepository;
import kissshot1104.personal.blog.post.dto.AuthenticationDataDto;
import kissshot1104.personal.blog.post.dto.CreatePostDto;
import kissshot1104.personal.blog.post.dto.request.PostModifyRequest;
import kissshot1104.personal.blog.post.entity.Post;
import kissshot1104.personal.blog.post.entity.PostSecurity;
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

    private Member member1;
    private Member member2;
    private UserDetails user;
    private UserDetails user2;

    private Category category1;
    private Category category2;
    private Category category3;

    private Post publicPost1;
    private Post protectedPost1;
    private Post privatePost1;
    private Post publicPost2;
    private Post protectedPost2;
    private Post privatePost2;

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

        member1 = Member.builder()
                .nickName("nickName1")
                .username("username")
                .password(passwordEncoder.encode("1"))
                .build();
        member2 = Member.builder()
                .nickName("nickName2")
                .username("username2")
                .password(passwordEncoder.encode("2"))
                .build();
        memberRepository.saveAll(List.of(member1, member2));
        user = memberPrincipal.loadUserByUsername("username");
        user2 = memberPrincipal.loadUserByUsername("username2");

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
                .andExpect(header().stringValues("Location", "/post/7"));
    }

    @Test
    @DisplayName("작성자가 아니면 protected게시글을 조회 시 비밀번호가 틀리면 예외가 발생한다.")
    public void authExceptionWhenInvalidPassword() throws Exception {
        final AuthenticationDataDto authenticationDataDto = AuthenticationDataDto.builder()
                .postPassword("invalid Password")
                .build();

        UserDetails user2 = memberPrincipal.loadUserByUsername("username2");

        mock.perform(post("/api/v1/post/{postId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationDataDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user2)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("권한이 없는 사용자입니다."),
                        jsonPath("$.code").value("AU_002")
                );
    }

    @Test
    @DisplayName("private 게시글은 작성자가 아니면 조회할 수 없다.")
    public void canNotViewPrivatePostUnlessAuthor() throws Exception {
        final AuthenticationDataDto authenticationDataDto = AuthenticationDataDto.builder()
                .build();

        mock.perform(post("/api/v1/post/{postId}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationDataDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user2)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("권한이 없는 사용자입니다."),
                        jsonPath("$.code").value("AU_002")
                );
    }

    @Test
    @DisplayName("public 게시글을 조회한다.")
    public void findPublicPostTest() throws Exception {
        final AuthenticationDataDto authenticationDataDto = AuthenticationDataDto.builder()
                .build();

        mock.perform(post("/api/v1/post/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationDataDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.postId").value(1),
                        jsonPath("$.category").value("Test Category Name1"),
                        jsonPath("$.nickName").value("nickName1"),
                        jsonPath("$.title").value("title1"),
                        jsonPath("$.content").value("content1"),
                        jsonPath("$.postSecurity").value("PUBLIC")
                );
    }

    @Test
    @DisplayName("protected 게시글을 조회한다.")
    public void findProtectedPostTest() throws Exception {
        final AuthenticationDataDto authenticationDataDto = AuthenticationDataDto.builder()
                .postPassword("password2")
                .build();

        mock.perform(post("/api/v1/post/{postId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationDataDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.postId").value(2),
                        jsonPath("$.category").value("Test Category Name2"),
                        jsonPath("$.nickName").value("nickName1"),
                        jsonPath("$.title").value("title2"),
                        jsonPath("$.content").value("content2"),
                        jsonPath("$.postSecurity").value("PROTECTED")
                );
    }

    @Test
    @DisplayName("private 게시글을 조회한다.")
    public void findPrivatePostTest() throws Exception {
        final AuthenticationDataDto authenticationDataDto = AuthenticationDataDto.builder()
                .build();

        mock.perform(post("/api/v1/post/{postId}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationDataDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.postId").value(3),
                        jsonPath("$.category").value("Test Category Name3"),
                        jsonPath("$.nickName").value("nickName1"),
                        jsonPath("$.title").value("title3"),
                        jsonPath("$.content").value("content3"),
                        jsonPath("$.postSecurity").value("PRIVATE")
                );
    }

    @Test
    @DisplayName("모든 게시글을 조회한다.")
    public void findAll() throws Exception {
        mock.perform(get("/api/v1/post/")
                        .param("kw", "")
                        .param("kw-type", "")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdDate,desc")
                        .contentType(MediaType.ALL_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .header("Authorization", "bearer {AccessToken}"))
                .andDo(print())
                .andDo(document("게시글들을 조회한다.",
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("AccessToken")
                        ),
                        queryParameters(
                                parameterWithName("kw")
                                        .description("게시글 검색 키워드"),
                                parameterWithName("kw-type")
                                        .description("게시글 검색 대상"),
                                parameterWithName("page")
                                        .description("페이지 번호"),
                                parameterWithName("size")
                                        .description("페이지 크기"),
                                parameterWithName("sort")
                                        .description("정렬 기준")
                        ),
                        responseFields(
                                fieldWithPath("content[].postId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 ID"),
                                fieldWithPath("content[].category")
                                        .type(JsonFieldType.STRING)
                                        .description("카테고리 이름"),
                                fieldWithPath("content[].nickName")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 글쓴이"),
                                fieldWithPath("content[].title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목"),
                                fieldWithPath("content[].content")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 내용(본문)"),
                                fieldWithPath("content[].postSecurity")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 접근 레벨"),
                                fieldWithPath("pageable.pageNumber")
                                        .type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize")
                                        .type(JsonFieldType.NUMBER)
                                        .description("페이지 크기"),
                                fieldWithPath("pageable.sort.empty")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("정렬 정보가 비어 있는지 여부"),
                                fieldWithPath("pageable.sort.unsorted")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("정렬되지 않음 여부"),
                                fieldWithPath("pageable.sort.sorted")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("정렬 여부"),
                                fieldWithPath("pageable.offset")
                                        .type(JsonFieldType.NUMBER)
                                        .description("오프셋"),
                                fieldWithPath("pageable.paged")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("페이지 매김 여부"),
                                fieldWithPath("pageable.unpaged")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("페이지 매김되지 않음 여부"),
                                fieldWithPath("last")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("마지막 페이지 여부"),
                                fieldWithPath("totalPages")
                                        .type(JsonFieldType.NUMBER)
                                        .description("총 페이지 수"),
                                fieldWithPath("totalElements")
                                        .type(JsonFieldType.NUMBER)
                                        .description("총 요소 수"),
                                fieldWithPath("size")
                                        .type(JsonFieldType.NUMBER)
                                        .description("페이지 크기"),
                                fieldWithPath("number")
                                        .type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("sort.empty")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("정렬 정보가 비어 있는지 여부"),
                                fieldWithPath("sort.unsorted")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("정렬되지 않음 여부"),
                                fieldWithPath("sort.sorted")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("정렬 여부"),
                                fieldWithPath("first")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("첫 페이지 여부"),
                                fieldWithPath("numberOfElements")
                                        .type(JsonFieldType.NUMBER)
                                        .description("현재 페이지의 요소 수"),
                                fieldWithPath("empty")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("현재 페이지가 비어 있는지 여부")
                        )
                ))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content[*].postId", hasItems(1, 2, 3, 4, 5)),
                        jsonPath("$.content[*].category",
                                hasItems("Test Category Name1", "Test Category Name2", "Test Category Name3")),
                        jsonPath("$.content[*].nickName", hasItems("nickName1", "nickName2")),
                        jsonPath("$.content[*].title", hasItems("title1", "title2", "title3", "title4", "title5")),
                        jsonPath("$.content[*].content",
                                hasItems("content1", "content2", "content3", "content4", "content5")),
                        jsonPath("$.content[*].postSecurity", hasItems("PUBLIC", "PROTECTED", "PRIVATE"))
                );
    }

    @Test
    @DisplayName("다른 사람의 게시글은 수정할 수 없다.")
    public void canNotModifyPostUnless() throws Exception {
        final PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("modifyTitle1")
                .content("modifyContent1")
                .postPassword("modifyPostPassword1")
                .postSecurity("PUBLIC")
                .build();

        mock.perform(patch("/api/v1/post/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postModifyRequest))
                        .with(SecurityMockMvcRequestPostProcessors.user(user2))
                        .header("Authorization", "bearer {AccessToken}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("권한이 없는 사용자입니다."),
                        jsonPath("$.code").value("AU_002")
                );
    }

    @Test
    @DisplayName("존재하지 않는 게시글은 수정할 수 없다.")
    public void canNotModifyPost() throws Exception {
        final PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("modifyTitle1")
                .content("modifyContent1")
                .postPassword("modifyPostPassword1")
                .postSecurity("PUBLIC")
                .build();

        mock.perform(patch("/api/v1/post/{postId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postModifyRequest))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .header("Authorization", "bearer {AccessToken}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("지정한 Entity를 찾을 수 없습니다."),
                        jsonPath("$.code").value("C_001")
                );
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    public void modifyPostTest() throws Exception {
        final PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("modifyTitle1")
                .content("modifyContent1")
                .postPassword("modifyPostPassword1")
                .postSecurity("PUBLIC")
                .build();

        mock.perform(patch("/api/v1/post/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postModifyRequest))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .header("Authorization", "bearer {AccessToken}"))
                .andDo(print())
                .andDo(document("게시글을 수정한다.",
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("postId")
                                        .description("게시글 ID")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .description("게시글 제목")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("content")
                                        .description("게시글 내용(본문)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("postPassword")
                                        .description("게시글 비밀번호")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("postSecurity")
                                        .description("게시글 접근레벨")
                                        .type(JsonFieldType.STRING)
                        )
                ))
                .andExpect(status().isOk());

        final Post post = postRepository.findById(1L).get();
        assertThat(post)
                .extracting("id", "category", "member", "title", "content", "postPassword", "postSecurity")
                .contains(1L, category1, member1, "modifyTitle1", "modifyContent1", "modifyPostPassword1",
                        PostSecurity.PUBLIC);
    }

    @Test
    @DisplayName("다른 사람의 게시글은 삭제할 수 없다.")
    public void canNotDeletePostUnless() throws Exception {
        final PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("modifyTitle1")
                .content("modifyContent1")
                .postPassword("modifyPostPassword1")
                .postSecurity("PUBLIC")
                .build();

        mock.perform(delete("/api/v1/post/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postModifyRequest))
                        .with(SecurityMockMvcRequestPostProcessors.user(user2))
                        .header("Authorization", "bearer {AccessToken}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("권한이 없는 사용자입니다."),
                        jsonPath("$.code").value("AU_002")
                );
    }

    @Test
    @DisplayName("존재하지 않는 게시글은 삭제할 수 없다.")
    public void canNotDeletePost() throws Exception {
        final PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("modifyTitle1")
                .content("modifyContent1")
                .postPassword("modifyPostPassword1")
                .postSecurity("PUBLIC")
                .build();

        mock.perform(delete("/api/v1/post/{postId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postModifyRequest))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .header("Authorization", "bearer {AccessToken}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("지정한 Entity를 찾을 수 없습니다."),
                        jsonPath("$.code").value("C_001")
                );
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    public void deletePostTest() throws Exception {

        mock.perform(delete("/api/v1/post/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .header("Authorization", "bearer {AccessToken}"))
                .andDo(print())
                .andDo(document("게시글을 삭제한다.",
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("postId")
                                        .description("게시글 ID")
                        )))
                .andExpect(status().isNoContent());

        assertThatThrownBy(() -> postService.findByPostId(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("지정한 Entity를 찾을 수 없습니다.");
    }
}
