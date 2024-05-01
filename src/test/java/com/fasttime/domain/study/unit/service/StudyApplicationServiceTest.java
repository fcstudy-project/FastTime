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
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.entity.StudyApplication;
import com.fasttime.domain.study.entity.StudyRequestStatus;
import com.fasttime.domain.study.exception.NotStudyWriterException;
import com.fasttime.domain.study.exception.StudyApplicationNotFoundException;
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
            StudyApplicationResponseDto studyApplicationResponseDto = studyApplicationService.apply(
                1L,
                1L,
                applyToStudyRequestDto
            );

            // then
            assertThat(studyApplicationResponseDto).extracting("studyApplicationId")
                .isEqualTo(1L);

            verify(studyRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(studyApplicationRepository, times(1))
                .save(any(StudyApplication.class));
        }

        @Test
        @DisplayName("스터디를 찾을 수 없으면 스터디 참여 지원을 할 수 없다.")
        void _study_not_found_willFail() {
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
        void _study_deleted_willFail() {
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

    @Nested
    @DisplayName("approve()는 ")
    class Context_approve {

        @Test
        @DisplayName("스터디 참여 신청을 승인할 수 있다.")
        void _willSuccess() {
            // given
            given(studyApplicationRepository.findById(any(long.class))).willReturn(
                Optional.of(newStudyApplication()));

            // when
            StudyApplicationResponseDto studyApplicationResponseDto = studyApplicationService.approve(
                1L,
                1L
            );

            // then
            assertThat(studyApplicationResponseDto).extracting("studyApplicationId")
                .isEqualTo(1L);

            verify(studyApplicationRepository, times(1))
                .findById(any(long.class));
        }

        @Test
        @DisplayName("스터디 참여 신청을 찾을 수 없으면 스터디 참여 신청을 승인할 수 없다.")
        void _study_application_not_found_willFail() {
            // given
            given(studyApplicationRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when then
            Throwable exception = assertThrows(StudyApplicationNotFoundException.class, () -> {
                studyApplicationService.approve(1L, 1L);
            });

            assertEquals("존재하지 않는 스터디 신청입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("삭제된 스터디면 스터디 참여 신청을 승인할 수 없다.")
        void _study_deleted_willFail() {
            // given
            StudyApplication studyApplication = newStudyApplication();
            studyApplication.getStudy().delete(LocalDateTime.now());

            given(studyApplicationRepository.findById(any(Long.class))).willReturn(
                Optional.of(studyApplication));

            // when then
            Throwable exception = assertThrows(StudyDeleteException.class, () -> {
                studyApplicationService.approve(1L, 1L);
            });

            assertEquals("삭제된 스터디 모집글입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("스터디 게시글 작성자가 아니면 스터디 참여 신청을 승인할 수 없다.")
        void _not_study_writer_willFail() {
            // given
            given(studyApplicationRepository.findById(any(Long.class)))
                .willReturn(Optional.of(newStudyApplication()));

            // when then
            Throwable exception = assertThrows(NotStudyWriterException.class, () -> {
                studyApplicationService.approve(2L, 1L);
            });

            assertEquals("해당 스터디 게시글에 대한 권한이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("reject()는 ")
    class Context_reject {

        @Test
        @DisplayName("스터디 참여 신청을 거부할 수 있다.")
        void _willSuccess() {
            // given
            given(studyApplicationRepository.findById(any(long.class))).willReturn(
                Optional.of(newStudyApplication()));

            // when
            StudyApplicationResponseDto studyApplicationResponseDto = studyApplicationService.reject(
                1L,
                1L
            );

            // then
            assertThat(studyApplicationResponseDto).extracting("studyApplicationId")
                .isEqualTo(1L);

            verify(studyApplicationRepository, times(1))
                .findById(any(long.class));
        }

        @Test
        @DisplayName("스터디 참여 신청을 찾을 수 없으면 스터디 참여 신청을 거부할 수 없다.")
        void _study_application_not_found_willFail() {
            // given
            given(studyApplicationRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when then
            Throwable exception = assertThrows(StudyApplicationNotFoundException.class, () -> {
                studyApplicationService.reject(1L, 1L);
            });

            assertEquals("존재하지 않는 스터디 신청입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("삭제된 스터디면 스터디 참여 신청을 거부할 수 없다.")
        void _study_deleted_willFail() {
            // given
            StudyApplication studyApplication = newStudyApplication();
            studyApplication.getStudy().delete(LocalDateTime.now());

            given(studyApplicationRepository.findById(any(Long.class))).willReturn(
                Optional.of(studyApplication));

            // when then
            Throwable exception = assertThrows(StudyDeleteException.class, () -> {
                studyApplicationService.reject(1L, 1L);
            });

            assertEquals("삭제된 스터디 모집글입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("스터디 게시글 작성자가 아니면 스터디 참여 신청을 거부할 수 없다.")
        void _not_study_writer_willFail() {
            // given
            given(studyApplicationRepository.findById(any(Long.class)))
                .willReturn(Optional.of(newStudyApplication()));

            // when then
            Throwable exception = assertThrows(NotStudyWriterException.class, () -> {
                studyApplicationService.reject(2L, 1L);
            });

            assertEquals("해당 스터디 게시글에 대한 권한이 없습니다.", exception.getMessage());
        }
    }

    private Member newMember() {
        return Member.builder()
            .id(2L)
            .email("email2")
            .password("password2")
            .nickname("nickname2")
            .image("imageUrl2")
            .build();
    }

    private Study newStudy() {
        return Study.builder()
            .id(1L)
            .member(Member.builder()
                .id(1L)
                .email("email1")
                .password("password1")
                .nickname("nickname1")
                .image("imageUrl1")
                .build()
            )
            .build();
    }

    private StudyApplication newStudyApplication() {
        return StudyApplication.builder()
            .id(1L)
            .status(StudyRequestStatus.CONSIDERING)
            .applicant(newMember())
            .study(newStudy())
            .message("스터디 같이 해요")
            .build();
    }
}
