package kissshot1104.personal.blog.integration.category.controller;


import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import kissshot1104.personal.blog.category.dto.ModifyCategoryDto;
import kissshot1104.personal.blog.category.dto.ModifyCategoryDtos;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.repository.CategoryRepository;
import kissshot1104.personal.blog.global.security.prinipal.MemberPrincipal;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.repository.MemberRepository;
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
class CategoryControllerTest {
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

    private ModifyCategoryDto modifyCategoryDto1;
    private ModifyCategoryDto modifyCategoryDto2;
    private ModifyCategoryDto modifyCategoryDto3;
    private ModifyCategoryDto modifyCategoryDto4;
    private ModifyCategoryDto modifyCategoryDto5;
    private ModifyCategoryDto modifyCategoryDto6;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAllInBatch();
        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1;").executeUpdate();
        categoryRepository.deleteAllInBatch();
        em.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1;").executeUpdate();

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
                .username("username")
                .password(passwordEncoder.encode("1"))
                .build();
        memberRepository.save(member);
        user = memberPrincipal.loadUserByUsername("username");

        modifyCategoryDto1 = ModifyCategoryDto.builder()
                .categoryId(1L)
                .categoryName("Test Category Name1")
                .parentCategoryId(null)
                .build();

        modifyCategoryDto2 = ModifyCategoryDto.builder()
                .categoryId(2L)
                .categoryName("Test Category Name2")
                .parentCategoryId(1L)
                .build();

        modifyCategoryDto3 = ModifyCategoryDto.builder()
                .categoryId(3L)
                .categoryName("Test Category Name3")
                .parentCategoryId(null)
                .build();
        modifyCategoryDto4 = ModifyCategoryDto.builder()
                .categoryName("Test Category Name4")
                .subCategoryId(1L)
                .build();

        modifyCategoryDto5 = ModifyCategoryDto.builder()
                .categoryName("Test Category Name5")
                .subCategoryId(2L)
                .parentCategoryId(1L)
                .build();

        modifyCategoryDto6 = ModifyCategoryDto.builder()
                .categoryName("Test Category Name6")
                .subCategoryId(3L)
                .parentCategoryId(2L)
                .build();
    }

    @Test
    @DisplayName("카테고리 이름을 넣지 않으면 예외가 발생한다.")
    public void saveCategoryInvalidCategoryNameException() throws Exception {
        final ModifyCategoryDto modifyCategoryDto = ModifyCategoryDto.builder()
                .categoryId(null)
                .categoryName(null)
                .parentCategoryId(null)
                .build();

        ModifyCategoryDtos modifyCategoryDtos = ModifyCategoryDtos.builder()
                .modifyCategoryDtos(List.of(modifyCategoryDto))
                .build();

        mock.perform(post("/api/v1/save-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCategoryDtos))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("적절하지 않은 요청 값입니다."),
                        jsonPath("$.code").value("C_002")
                );
    }

    @Test
    @DisplayName("카테고리를 추가한다.")
    public void addCategory() throws Exception {
        //given
        final ModifyCategoryDtos modifyCategoryDtos = ModifyCategoryDtos.builder()
                .modifyCategoryDtos(List.of(modifyCategoryDto1,
                        modifyCategoryDto2,
                        modifyCategoryDto3,
                        modifyCategoryDto4,
                        modifyCategoryDto5,
                        modifyCategoryDto6))
                .build();

        //when
        mock.perform(post("/api/v1/save-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCategoryDtos))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories)
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(
                        tuple(1L, null, "Test Category Name1", 0L),
                        tuple(2L, category1, "Test Category Name2", 1L),
                        tuple(3L, null, "Test Category Name3", 0L),
                        tuple(4L, null, "Test Category Name4", 0L),
                        tuple(5L, category1, "Test Category Name5", 1L),
                        tuple(6L, category2, "Test Category Name6", 2L)
                );
    }


    @Test
    @DisplayName("카테고리이름을 수정한다.")
    public void modifyCategoryName() throws Exception {
        //given
        final ModifyCategoryDto modify = ModifyCategoryDto.builder()
                .categoryId(1L)
                .categoryName("Test Modify Category Name")
                .build();
        final ModifyCategoryDtos modifyCategoryDtos = ModifyCategoryDtos.builder()
                .modifyCategoryDtos(List.of(
                        modifyCategoryDto2,
                        modifyCategoryDto3,
                        modifyCategoryDto4,
                        modifyCategoryDto5,
                        modifyCategoryDto6,
                        modify))
                .build();

        //when
        mock.perform(post("/api/v1/save-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCategoryDtos))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        final List<Category> categories = categoryRepository.findAll();
        assertThat(categories)
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(
                        tuple(1L, null, "Test Modify Category Name", 0L),
                        tuple(2L, category1, "Test Category Name2", 1L),
                        tuple(3L, null, "Test Category Name3", 0L),
                        tuple(4L, null, "Test Category Name4", 0L),
                        tuple(5L, category1, "Test Category Name5", 1L),
                        tuple(6L, category2, "Test Category Name6", 2L)
                );
    }

    @Test
    @DisplayName("연결된 카테고리(부모)를 변경한다.")
    public void modifyCategoryParent() throws Exception {
        //given
        final ModifyCategoryDto modify1 = ModifyCategoryDto.builder()
                .categoryId(1L)
                .categoryName("Test Category Name1")
                .parentCategoryId(2L)
                .build();

        final ModifyCategoryDto modify2 = ModifyCategoryDto.builder()
                .categoryId(2L)
                .categoryName("Test Category Name2")
                .parentCategoryId(null)
                .build();

        final ModifyCategoryDtos modifyCategoryDtos = ModifyCategoryDtos.builder()
                .modifyCategoryDtos(List.of(modify1,
                        modify2,
                        modifyCategoryDto3,
                        modifyCategoryDto4,
                        modifyCategoryDto5,
                        modifyCategoryDto6))
                .build();

        //when
        mock.perform(post("/api/v1/save-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCategoryDtos))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        final List<Category> categories = categoryRepository.findAll();
        assertThat(categories)
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(
                        tuple(1L, category2, "Test Category Name1", 1L),
                        tuple(2L, null, "Test Category Name2", 0L),
                        tuple(3L, null, "Test Category Name3", 0L),
                        tuple(4L, null, "Test Category Name4", 0L),
                        tuple(5L, category1, "Test Category Name5", 2L),
                        tuple(6L, category2, "Test Category Name6", 1L)
                );
    }

    @Test
    @DisplayName("카테고리를 삭제한다.")
    public void deleteCategoryParent() throws Exception {
        //given
        final ModifyCategoryDto modify1 = ModifyCategoryDto.builder()
                .categoryId(1L)
                .categoryName("Test Category Name1")
                .parentCategoryId(4L)
                .build();

        final ModifyCategoryDto modify2 = ModifyCategoryDto.builder()
                .categoryName("Test Category Name2")
                .parentCategoryId(null)
                .subCategoryId(1L)
                .build();

        final ModifyCategoryDtos modifyCategoryDtos = ModifyCategoryDtos.builder()
                .modifyCategoryDtos(List.of(modify1,
                        modify2))
                .build();

        //when
        mock.perform(post("/api/v1/save-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCategoryDtos))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        final List<Category> categories = categoryRepository.findAll();
        final Category category4 = categoryRepository.findById(4L).get();
        assertThat(categories)
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(
                        tuple(1L, category4, "Test Category Name1", 1L),
                        tuple(4L, null, "Test Category Name2", 0L)
                );
    }

    @Test
    @DisplayName("변경된 카테고리 설정을 모두 저장한다.")
    public void categorySettingsUpdate() throws Exception {
        //given
        final ModifyCategoryDto createCategory = ModifyCategoryDto.builder()
                .subCategoryId(1L)
                .categoryName("new Category Name")
                .build();

        final ModifyCategoryDto modify1 = ModifyCategoryDto.builder()
                .categoryId(1L)
                .categoryName("modified Category Name")
                .parentCategoryId(4L)
                .build();

        final ModifyCategoryDtos modifyCategoryDtos = ModifyCategoryDtos.builder()
                .modifyCategoryDtos(List.of(createCategory, modify1))
                .build();
        //when
        mock.perform(post("/api/v1/save-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCategoryDtos))
                        .header("Authorization", "Bearer {AccessToken}")
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andDo(document("변경된 모든 카테고리 설정을 저장한다.",
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("modifyCategoryDtos")
                                        .type(JsonFieldType.ARRAY)
                                        .description("@Valid를 사용하기 위해 일급컬렉션을 사용하게 되었다"),
                                fieldWithPath("modifyCategoryDtos[].categoryId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("카테고리 ID").optional(),
                                fieldWithPath("modifyCategoryDtos[].categoryName")
                                        .type(JsonFieldType.STRING)
                                        .description("카테고리 이름"),
                                fieldWithPath("modifyCategoryDtos[].parentCategoryId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("부모 카테고리 ID").optional(),
                                fieldWithPath("modifyCategoryDtos[].subCategoryId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("임시로 만들어진 카테고리 ID").optional(),
                                fieldWithPath("modifyCategoryDtos[].subParentCategoryId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("임시로 만들어진 부모 카테고리 ID").optional()
                        )
                ))
                .andExpect(status().isOk());
        //then
        final List<Category> categories = categoryRepository.findAll();
        final Category category4 = categoryRepository.findById(4L).get();
        assertThat(categories)
                .extracting("id", "category", "categoryName", "categoryDepth")
                .contains(
                        tuple(1L, category4, "modified Category Name", 1L),
                        tuple(4L, null, "new Category Name", 0L)
                );
    }

    //todo 순회하는 최종 검증로직 만들어야함
    @Test
    @DisplayName("카테고리가 순환을 한다면 예외가 발생한다.")
    public void circularReferenceValidateTest() throws Exception {
        //given
        final ModifyCategoryDto modify1 = ModifyCategoryDto.builder()
                .categoryId(1L)
                .categoryName("Test Category Name1")
                .parentCategoryId(3L)
                .build();

        final ModifyCategoryDto modify2 = ModifyCategoryDto.builder()
                .categoryId(2L)
                .categoryName("Test Category Name2")
                .parentCategoryId(1L)
                .build();

        final ModifyCategoryDto modify3 = ModifyCategoryDto.builder()
                .categoryId(3L)
                .categoryName("Test Category Name2")
                .parentCategoryId(2L)
                .build();

        final ModifyCategoryDtos modifyCategoryDtos = ModifyCategoryDtos.builder()
                .modifyCategoryDtos(List.of(modify1,
                        modify2,
                        modify3))
                .build();

        //when
        mock.perform(post("/api/v1/save-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCategoryDtos))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("순환참조를 감지했습니다."),
                        jsonPath("$.code").value("C_004")
                );
    }
}
