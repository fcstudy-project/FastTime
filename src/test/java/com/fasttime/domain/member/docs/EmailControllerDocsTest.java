package com.fasttime.domain.member.docs;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.member.controller.EmailController;
import com.fasttime.domain.member.dto.request.EmailRequest;
import com.fasttime.domain.member.service.EmailUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class EmailControllerDocsTest extends RestDocsSupport {

    private EmailUseCase emailUseCase;

    @Override
    public Object initController() {
        emailUseCase = mock(EmailUseCase.class);
        return new EmailController(emailUseCase);
    }

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
            .standaloneSetup(initController())
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @DisplayName("이메일 인증 코드 전송 API 문서화")
    @Test
    void testSendVerificationEmail() throws Exception {
        // given
        EmailRequest emailRequest = new EmailRequest("test@example.com");
        when(emailUseCase.sendVerificationEmail(anyString())).thenReturn("Verification Code");

        // when & then
        mockMvc.perform(post("/api/v1/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
            .andExpect(status().isOk())
            .andDo(document("email-send-verification",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").description("인증 코드를 보낼 이메일")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data").description("응답 데이터")
                )
            ));
    }

    @DisplayName("이메일 인증 코드 검증 API 문서화")
    @Test
    void testVerifyEmailCode() throws Exception {
        // given
        String validEmail = "test@example.com";
        String validCode = "12345678";

        when(emailUseCase.verifyEmailCode(validEmail, validCode)).thenReturn(true);

        // when & then
        mockMvc.perform(get("/api/v1/verify/{code}", validCode)
                .param("email", validEmail))
            .andExpect(status().isOk())
            .andDo(document("email-verify-code",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("code").description("검증하려는 이메일 인증 코드")
                ),
                queryParameters(
                    parameterWithName("email").description("인증을 요청한 사용자의 이메일 주소")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data").description("인증 성공 여부 (true/false)")
                )
            ));
    }
}
