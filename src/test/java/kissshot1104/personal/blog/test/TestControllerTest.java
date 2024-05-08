package kissshot1104.personal.blog.test;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class TestControllerTest {
    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestController testController;

    @Autowired
    private TestService testService;

    @Test
    @DisplayName("Test를 하기 위해 만든것이다.")
    public void test() throws Exception {

        TestDto testDto = TestDto.builder().data("testData입니다.").build();

        mock.perform(post("/test/{number}", 1L)
                        .param("param", "3")
                        .header("Authorization", "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andDo(print())
                .andDo(document("test func",
                        //todo pathParameters와 queryParameter에서는 type을 지정해주지 못한다. 이걸 해결하는 방법을 찾아보자
                        pathParameters(
                                parameterWithName("number")
                                        .description("PathVariable이다")
                                        .attributes(key("example").value(1L))
                        ),
                        queryParameters(
                                parameterWithName("param")
                                        .description("RequestParam이다")
                                        .attributes(key("example").value(3L))
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("data")
                                        .type(JsonFieldType.STRING)
                                        .description("requestDto의 data이다")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("test Data이다")
                        )))
                .andExpect(status().isOk())
                .andExpectAll(jsonPath("$.data").value("test"));
    }

}
