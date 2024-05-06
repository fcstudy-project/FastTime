package com.fasttime.domain.study.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.study.dto.StudyCreateRequest;
import com.fasttime.domain.study.dto.StudyPageResponse;
import com.fasttime.domain.study.dto.StudyResponse;
import com.fasttime.domain.study.dto.StudyUpdateRequest;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.exception.NotStudyWriterException;
import com.fasttime.domain.study.exception.StudyDeleteException;
import com.fasttime.domain.study.exception.StudyNotFoundException;
import com.fasttime.domain.study.repository.StudyRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyCategoryService studyCategoryService;
    private final MemberService memberService;

    @Transactional
    public StudyResponse createStudy(Long memberId, StudyCreateRequest studyCreateRequest) {
        Member member = memberService.getMember(memberId);

        Study savedStudy = studyRepository.save(
                Study.createNewStudy(
                        studyCreateRequest.title(), studyCreateRequest.content(),
                        studyCreateRequest.skill(), studyCreateRequest.total(),
                        studyCreateRequest.recruitmentEnd(), studyCreateRequest.progressStart(),
                        studyCreateRequest.progressEnd(), studyCreateRequest.contact(), member
                ));

        studyCategoryService.createCategory(savedStudy, studyCreateRequest.categoryIds());

        return getStudyResponse(savedStudy);
    }

    @Transactional
    public StudyResponse updateStudy(Long studyId, Long memberId, StudyUpdateRequest request) {
        Study study = findStudyById(studyId);
        Member member = memberService.getMember(memberId);
        isWriter(member, study);

        studyCategoryService.deleteCategoryByUpdate(study);
        studyCategoryService.createCategory(study, request.categoryIds());

        study.updateStudy(
            request.title(), request.content(), request.skill(), request.total(),
            request.recruitmentEnd(), request.progressStart(), request.progressEnd(),
            request.contact()
        );

        return getStudyResponse(study);

    }

    @Transactional
    public void deleteStudy(Long studyId, Long memberId) {
        Study study = findStudyById(studyId);
        Member member = memberService.getMember(memberId);
        isWriter(member, study);
        study.delete(LocalDateTime.now());
    }

    public StudyResponse getStudy(Long studyId) {
        return getStudyResponse(findStudyById(studyId));
    }

    public StudyPageResponse searchStudies(Pageable pageable) {
        Page<Study> studyPage = studyRepository.search(pageable);
        List<StudyResponse> studyResponses = studyPage.getContent().stream()
            .map(this::getStudyResponse)
            .toList();
        return StudyPageResponse.of(studyPage, studyResponses);
    }

    private void isWriter(Member requestMember, Study study) {
        if (!requestMember.getId().equals(study.getMember().getId())) {
            throw new NotStudyWriterException(String.format(
                "This member has no auth to control this study / targetStudyId = %d, requestMemberId = %d",
                study.getId(), requestMember.getId()));
        }
    }

    private Study findStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (study.getDeletedAt() == null) {
            throw new StudyDeleteException();
        }
        return study;
    }

    private StudyResponse getStudyResponse(Study study) {
        return StudyResponse.builder()
            .id(study.getId())
            .title(study.getTitle())
            .content(study.getContent())
            .skill(study.getSkill())
            .total(study.getTotal())
            .current(study.getCurrent())
            .applicant(study.getApplicant())
            .recruitmentStart(study.getRecruitmentStart())
            .recruitmentEnd(study.getRecruitmentEnd())
            .progressStart(study.getProgressStart())
            .progressEnd(study.getProgressEnd())
            .contact(study.getContact())
            .nickname(study.getMember().getNickname())
            .categories(studyCategoryService.findCategoryNameByStudy(study)).build();
    }

}
