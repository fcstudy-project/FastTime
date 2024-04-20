package com.fasttime.domain.study.docs;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.study.controller.CategoryController;
import com.fasttime.domain.study.dto.CategoryResponse;
import com.fasttime.domain.study.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryControllerTest extends RestDocsSupport {
    private final CategoryService categoryService = mock(CategoryService.class);

    @Override
    public Object initController() {
        return new CategoryController(categoryService) ;
    }
    @DisplayName("카테고리 목록 조회 API")
    @Test
    void getCategories() throws Exception {
        //given
        CategoryResponse categoryResponse1 = new CategoryResponse(1L, "백엔드");
        CategoryResponse categoryResponse2 = new CategoryResponse(2L, "면접 준비");

        List<CategoryResponse> categoryResponses = List.of(categoryResponse1, categoryResponse2);

        when(categoryService.getAllCategories())
                .thenReturn(categoryResponses);

        //when then
        mockMvc.perform(get("/api/v2/categories"))
                .andExpect(status().isOk())
                .andDo(document("category-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                                fieldWithPath("message").type(JsonFieldType.NULL).description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("카테고리 리스트"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("카테고리 이름")
                        )
                ));


    }
}


