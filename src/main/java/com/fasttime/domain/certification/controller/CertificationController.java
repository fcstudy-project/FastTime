package com.fasttime.domain.certification.controller;

import com.fasttime.domain.certification.dto.request.CertificationRequestDTO;
import com.fasttime.domain.certification.dto.request.RejectionRequestDTO;
import com.fasttime.domain.certification.dto.request.WithdrawalRequestDTO;
import com.fasttime.domain.certification.dto.response.AllCertificationResponseDTO;
import com.fasttime.domain.certification.dto.response.ApproveResponseDTO;
import com.fasttime.domain.certification.dto.response.CertificationResponseDTO;
import com.fasttime.domain.certification.dto.response.MyCertificationResponseDTO;
import com.fasttime.domain.certification.entity.Certification;
import com.fasttime.domain.certification.entity.CertificationStatus;
import com.fasttime.domain.certification.service.CertificationService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/certification")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ResponseDTO<CertificationResponseDTO>> createCertification(
        @RequestBody CertificationRequestDTO requestDTO) {
        Long currentMemberId = securityUtil.getCurrentMemberId();
        Certification certification = certificationService.createCertification(requestDTO,
            currentMemberId);

        CertificationResponseDTO responseDto = CertificationResponseDTO.from(certification);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.OK, "인증 요청이 완료되었습니다.", responseDto));
    }

    @PostMapping("/withdraw/{certificationId}")
    public ResponseEntity<ResponseDTO<CertificationResponseDTO>> withdrawCertification(
        @PathVariable Long certificationId,
        @Valid @RequestBody WithdrawalRequestDTO withdrawalRequestDTO) {
        Long currentMemberId = securityUtil.getCurrentMemberId();
        Certification certification = certificationService.withdrawCertification(
            certificationId, currentMemberId, withdrawalRequestDTO.withdrawalReason());

        CertificationResponseDTO responseDto = CertificationResponseDTO.from(certification);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "철회 요청이 완료되었습니다.", responseDto));
    }

    @GetMapping("/my-certifications")
    public ResponseEntity<ResponseDTO<List<MyCertificationResponseDTO>>> getMyCertifications() {
        Long currentMemberId = securityUtil.getCurrentMemberId();
        List<Certification> certifications = certificationService.getCertificationsByMember(
            currentMemberId);

        List<MyCertificationResponseDTO> responseDTO = certifications.stream()
            .map(MyCertificationResponseDTO::from)
            .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "내 인증요청 조회가 완료되었습니다.", responseDTO));
    }

    @PatchMapping("/cancel-withdrawal/{certificationId}")
    public ResponseEntity<ResponseDTO<CertificationResponseDTO>> cancelWithdrawal(
        @PathVariable Long certificationId) {
        Long currentMemberId = securityUtil.getCurrentMemberId();
        Certification certification = certificationService.cancelWithdrawal(certificationId,
            currentMemberId);

        CertificationResponseDTO responseDto = CertificationResponseDTO.from(certification);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "철회 취소 요청이 완료되었습니다.", responseDto));
    }

    @PatchMapping("/approve/{certificationId}/{bootCampId}")
    public ResponseEntity<ResponseDTO<Object>> approveCertification(
        @PathVariable Long certificationId,
        @PathVariable Long bootCampId) {

        Long currentMemberId = securityUtil.getCurrentMemberId();
        Certification certification = certificationService.approveCertification(
            certificationId, bootCampId, currentMemberId);

        ApproveResponseDTO responseDto = ApproveResponseDTO.from(certification);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "인증 요청이 승인되었습니다.", responseDto));
    }

    @PatchMapping("/reject/{certificationId}")
    public ResponseEntity<ResponseDTO<MyCertificationResponseDTO>> rejectCertification(
        @PathVariable Long certificationId,
        @Valid @RequestBody RejectionRequestDTO rejectionRequestDTO) {

        Long currentAdminId = securityUtil.getCurrentMemberId();
        Certification certification = certificationService.rejectCertification(
            certificationId, currentAdminId, rejectionRequestDTO.rejectionReason());

        MyCertificationResponseDTO responseDto = MyCertificationResponseDTO.from(certification);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "인증 요청이 거절되었습니다.", responseDto));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getAllCertifications(
        @RequestParam(required = false) CertificationStatus status,
        @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<AllCertificationResponseDTO> certifications = certificationService.getAllCertificationsByStatus(
            status, pageable);
        Map<String, Object> responseMap = createPaginationResponse(certifications);
        return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, "인증서 조회 성공", responseMap));
    }

    private Map<String, Object> createPaginationResponse(Page<?> page) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("currentPage", page.getNumber() + 1);
        responseMap.put("totalPages", page.getTotalPages());
        responseMap.put("currentElements", page.getNumberOfElements());
        responseMap.put("totalElements", page.getTotalElements());
        responseMap.put("certification", page.getContent());
        return responseMap;
    }
}
