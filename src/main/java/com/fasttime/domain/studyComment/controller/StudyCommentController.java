package com.fasttime.domain.studyComment.controller;

import com.fasttime.domain.studyComment.dto.request.CreateStudyCommentRequestDTO;
import com.fasttime.domain.studyComment.dto.request.GetStudyCommentsRequestDTO;
import com.fasttime.domain.studyComment.dto.request.StudyCommentPageRequestDTO;
import com.fasttime.domain.studyComment.dto.request.UpdateStudyCommentRequestDTO;
import com.fasttime.domain.studyComment.dto.response.StudyCommentListResponseDTO;
import com.fasttime.domain.studyComment.dto.response.StudyCommentResponseDTO;
import com.fasttime.domain.studyComment.service.StudyCommentService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/studyComments")
public class StudyCommentController {

    private final StudyCommentService studyCommentService;
    private final SecurityUtil securityUtil;

    @PostMapping("/{studyId}")
    public ResponseEntity<ResponseDTO<Object>> createComment(
        @PathVariable(name = "studyId") long studyId,
        @Valid @RequestBody CreateStudyCommentRequestDTO createStudyCommentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseDTO.res(HttpStatus.CREATED, "스터디 댓글을 성공적으로 등록했습니다.",
                    studyCommentService.createComment(studyId, securityUtil.getCurrentMemberId(),
                            createStudyCommentRequestDTO)));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<StudyCommentListResponseDTO>> getComments(
        @RequestParam(required = false) Long studyId,
        @RequestParam(required = false) Long memberId,
        @RequestParam(required = false) Long parentStudyCommentId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "스터디 댓글을 성공적으로 조회했습니다.", studyCommentService.getComments(
                GetStudyCommentsRequestDTO.builder()
                    .studyId(studyId)
                    .memberId(memberId)
                    .parentStudyCommentId(parentStudyCommentId)
                    .build(),
                StudyCommentPageRequestDTO.builder()
                    .page(page)
                    .size(pageSize)
                    .build().of()
            )));
    }

    @PatchMapping("/{studyCommentId}")
    public ResponseEntity<ResponseDTO<StudyCommentResponseDTO>> updateComment(
        @PathVariable(name = "studyCommentId") long studyCommentId,
        @Valid @RequestBody UpdateStudyCommentRequestDTO updateStudyCommentRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "스터디 댓글 내용을 성공적으로 수정했습니다.",
                    studyCommentService.updateComment(studyCommentId, securityUtil.getCurrentMemberId(),
                            updateStudyCommentRequestDTO)));
    }

    @DeleteMapping("/{studyCommentId}")
    public ResponseEntity<ResponseDTO<StudyCommentResponseDTO>> deleteComment(
        @PathVariable(name = "studyCommentId") long studyCommentId) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "스터디 댓글을 성공적으로 삭제했습니다.",
                    studyCommentService.deleteComment(studyCommentId, securityUtil.getCurrentMemberId())));
    }
}
