package com.fasttime.domain.resume.controller;

import com.fasttime.domain.resume.dto.LikeResumeRequest;
import com.fasttime.domain.resume.dto.ResumeDeleteServiceRequest;
import com.fasttime.domain.resume.dto.ResumeRequestDto;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.dto.ResumeUpdateRequest;
import com.fasttime.domain.resume.dto.ResumeUpdateServiceRequest;
import com.fasttime.domain.resume.dto.ResumesSearchRequest;
import com.fasttime.domain.resume.repository.ResumeOrderBy;
import com.fasttime.domain.resume.service.ResumeService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/resumes")
public class ResumeController {

    private final SecurityUtil securityUtil;
    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<ResponseDTO<ResumeResponseDto>> createResume(
        @RequestBody @Valid ResumeRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, "자기소개서가 등록되었습니다.",
                resumeService.createResume(requestDto, securityUtil.getCurrentMemberId())));
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<ResponseDTO<Void>> deleteResume(@PathVariable Long resumeId) {
        resumeService.delete(
            new ResumeDeleteServiceRequest(resumeId, securityUtil.getCurrentMemberId()));
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "정상적으로 삭제되었습니다.", null));
    }

    @PutMapping("/{resumeId}")
    public ResponseEntity<ResponseDTO<ResumeResponseDto>> updateResume(@PathVariable Long resumeId,
        @RequestBody @Valid ResumeUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "자기소개서 업데이트 완료되었습니다.",
                resumeService.updateResume(new ResumeUpdateServiceRequest(resumeId,
                    securityUtil.getCurrentMemberId(), request.title(),
                    request.content()))));
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResponseDTO<ResumeResponseDto>> getResume(@PathVariable Long resumeId,
        HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                resumeService.getResume(resumeId, request.getRemoteAddr())));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ResumeResponseDto>>> getResumes(
        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "orderBy", defaultValue = "date") String orderBy) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                resumeService.search(
                    new ResumesSearchRequest(ResumeOrderBy.of(orderBy), page,
                        pageSize))));
    }

    @PostMapping("/{resumeId}/likes")
    public ResponseEntity<ResponseDTO<Object>> likeResume(@PathVariable Long resumeId) {
        resumeService.likeResume(
            new LikeResumeRequest(resumeId, securityUtil.getCurrentMemberId()));
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "정상적으로 처리되었습니다."));
    }

    @DeleteMapping("/{resumeId}/likes")
    public ResponseEntity<ResponseDTO<Object>> cancelLike(@PathVariable Long resumeId) {
        resumeService.cancelLike(
            new LikeResumeRequest(resumeId, securityUtil.getCurrentMemberId()));
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "정상적으로 처리되었습니다."));
    }
}
