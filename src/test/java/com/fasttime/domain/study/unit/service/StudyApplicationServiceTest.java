package com.fasttime.domain.study.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.response.ApplyToStudyResponseDto;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.entity.StudyApplication;
import com.fasttime.domain.study.entity.StudyRequestStatus;
import com.fasttime.domain.study.exception.StudyDeleteException;
import com.fasttime.domain.study.exception.StudyNotFoundException;
import com.fasttime.domain.study.repository.StudyApplicationRepository;
import com.fasttime.domain.study.repository.StudyRepository;
import com.fasttime.domain.study.service.StudyApplicationServiceImpl;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
public class StudyApplicationServiceTest {

    @InjectMocks
    private StudyApplicationServiceImpl studyApplicationService;

    @Mock
    private MemberService memberService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyApplicationRepository studyApplicationRepository;

    @Nested
    @DisplayName("apply()는 ")
    class Context_apply {

        @Test
        @DisplayName("스터디 참여를 지원할 수 있다.")
        void _willSuccess() {
            // given
            ApplyToStudyRequestDto applyToStudyRequestDto = new ApplyToStudyRequestDto(
                "스터디 같이 해요!");

            given(studyRepository.findById(any(Long.class))).willReturn(Optional.of(newStudy()));
            given(memberService.getMember(any(Long.class))).willReturn(newMember());
            given(studyApplicationRepository.save(any(StudyApplication.class))).willReturn(
                StudyApplication.builder()
                    .id(1L)
                    .status(StudyRequestStatus.CONSIDERING)
                    .applicant(newMember())
                    .study(newStudy())
                    .message("스터디 같이 해요")
                    .build());

            // when
            ApplyToStudyResponseDto applyToStudyResponseDto = studyApplicationService.apply(
                1L,
                1L,
                applyToStudyRequestDto
            );

            // then
            assertThat(applyToStudyResponseDto).extracting("studyApplicationId")
                .isEqualTo(1L);
            verify(studyRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(studyApplicationRepository, times(1))
                .save(any(StudyApplication.class));
        }

        @Test
        @DisplayName("스터디를 찾을 수 없으면 스터디 참여 지원을 할 수 없다.")
        void study_not_found_willFail() {
            // given
            ApplyToStudyRequestDto applyToStudyRequestDto = new ApplyToStudyRequestDto(
                "스터디 같이 해요!");

            given(studyRepository.findById(any(Long.class))).willReturn(Optional.empty());
            given(memberService.getMember(any(Long.class))).willReturn(newMember());

            // when then
            Throwable exception = assertThrows(StudyNotFoundException.class, () -> {
                studyApplicationService.apply(1L, 1L, applyToStudyRequestDto);
            });
            assertEquals("존재하지 않는 스터디게시판입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("삭제된 스터디면 스터디 참여 지원을 할 수 없다.")
        void study_deleted_willFail() {
            // given
            ApplyToStudyRequestDto applyToStudyRequestDto = new ApplyToStudyRequestDto(
                "스터디 같이 해요!");

            Study deletedStudy = newStudy();
            deletedStudy.delete(LocalDateTime.now());

            given(studyRepository.findById(any(Long.class))).willReturn(Optional.of(deletedStudy));
            given(memberService.getMember(any(Long.class))).willReturn(newMember());

            // when then
            Throwable exception = assertThrows(StudyDeleteException.class, () -> {
                studyApplicationService.apply(1L, 1L, applyToStudyRequestDto);
            });
            assertEquals("삭제된 스터디 모집글입니다.", exception.getMessage());
        }
    }

    private Member newMember() {
        return Member.builder()
            .id(1L)
            .email("email")
            .password("password")
            .nickname("nickname")
            .image("imageUrl")
            .build();
    }

    private Study newStudy() {
        return Study.builder()
            .id(1L)
            .build();
    }
}
