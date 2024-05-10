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
import com.fasttime.domain.study.dto.request.GetStudySuggestionsRequestDto;
import com.fasttime.domain.study.dto.request.StudySuggestionPageRequestDto;
import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudySuggestionResponseDto;
import com.fasttime.domain.study.dto.response.StudySuggestionsResponseDto;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.entity.StudyRequestStatus;
import com.fasttime.domain.study.entity.StudySuggestion;
import com.fasttime.domain.study.exception.NotStudySuggestionReceiverException;
import com.fasttime.domain.study.exception.NotStudyWriterException;
import com.fasttime.domain.study.exception.StudyDeleteException;
import com.fasttime.domain.study.exception.StudyNotFoundException;
import com.fasttime.domain.study.exception.StudySuggestionNotFoundException;
import com.fasttime.domain.study.repository.StudyRepository;
import com.fasttime.domain.study.repository.StudySuggestionRepository;
import com.fasttime.domain.study.service.StudySuggestionServiceImpl;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Transactional
@ExtendWith(MockitoExtension.class)
public class StudySuggestionServiceTest {

    @InjectMocks
    private StudySuggestionServiceImpl studySuggestionService;

    @Mock
    private MemberService memberService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudySuggestionRepository studySuggestionRepository;

    @Nested
    @DisplayName("suggest()는 ")
    class Context_suggest {

        @Test
        @DisplayName("스터디 참여를 제안할 수 있다.")
        void _willSuccess() {
            // given
            SuggestStudyRequestDto suggestStudyRequestDto = new SuggestStudyRequestDto(
                "스터디 같이 해요!");

            given(studyRepository.findById(any(Long.class))).willReturn(Optional.of(newStudy()));
            given(memberService.getMember(any(Long.class))).willReturn(newMember());
            given(studySuggestionRepository.save(any(StudySuggestion.class)))
                .willReturn(newStudySuggestion());

            // when
            StudySuggestionResponseDto studySuggestionResponseDto = studySuggestionService.suggest(
                1L,
                2L,
                1L,
                suggestStudyRequestDto
            );

            // then
            assertThat(studySuggestionResponseDto).extracting("studySuggestionId")
                .isEqualTo(1L);

            verify(studyRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(studySuggestionRepository, times(1))
                .save(any(StudySuggestion.class));
        }

        @Test
        @DisplayName("스터디를 찾을 수 없으면 스터디 참여 제안을 할 수 없다.")
        void _study_not_found_willFail() {
            // given
            SuggestStudyRequestDto suggestStudyRequestDto = new SuggestStudyRequestDto(
                "스터디 같이 해요!");

            given(studyRepository.findById(any(Long.class))).willReturn(Optional.empty());
            given(memberService.getMember(any(Long.class))).willReturn(newMember());

            // when then
            Throwable exception = assertThrows(StudyNotFoundException.class, () -> {
                studySuggestionService.suggest(1L, 2L, 1L, suggestStudyRequestDto);
            });

            assertEquals("존재하지 않는 스터디게시판입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("삭제된 스터디면 스터디 참여 제안을 할 수 없다.")
        void _study_deleted_willFail() {
            // given
            SuggestStudyRequestDto suggestStudyRequestDto = new SuggestStudyRequestDto(
                "스터디 같이 해요!");

            Study deletedStudy = newStudy();
            deletedStudy.delete(LocalDateTime.now());

            given(studyRepository.findById(any(Long.class))).willReturn(Optional.of(deletedStudy));
            given(memberService.getMember(any(Long.class))).willReturn(newMember());

            // when then
            Throwable exception = assertThrows(StudyDeleteException.class, () -> {
                studySuggestionService.suggest(1L, 2L, 1L, suggestStudyRequestDto);
            });

            assertEquals("삭제된 스터디 모집글입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("스터디 게시글 작성자가 아니면 스터디 참여 제안을 할 수 없다.")
        void _not_study_writer_willFail() {
            // given
            SuggestStudyRequestDto suggestStudyRequestDto = new SuggestStudyRequestDto(
                "스터디 같이 해요!");

            given(studyRepository.findById(any(Long.class)))
                .willReturn(Optional.of(newStudy()));
            given(memberService.getMember(any(Long.class))).willReturn(newMember());

            // when then
            Throwable exception = assertThrows(NotStudyWriterException.class, () -> {
                studySuggestionService.suggest(3L, 2L, 1L, suggestStudyRequestDto);
            });

            assertEquals("해당 스터디 게시글에 대한 권한이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getStudySuggestions()는 ")
    class Context_getStudySuggestions {

        @Test
        @DisplayName("스터디 참여 제안 목록을 조회할 수 있다.")
        void _willSuccess() {
            // given
            GetStudySuggestionsRequestDto getStudySuggestionsRequestDto = GetStudySuggestionsRequestDto.builder()
                .receiverId(1L)
                .studyId(1L)
                .build();
            PageRequest pageRequest = StudySuggestionPageRequestDto.builder()
                .page(0)
                .size(10)
                .build().of();
            Study study = newStudy();
            Member member1 = Member.builder()
                .id(2L)
                .email("email2")
                .password("password2")
                .nickname("nickname2")
                .image("imageUrl2")
                .build();
            Member member2 = Member.builder()
                .id(3L)
                .email("email3")
                .password("password3")
                .nickname("nickname3")
                .image("imageUrl3")
                .build();
            List<StudySuggestion> studySuggestions = List.of(
                StudySuggestion.builder()
                    .id(1L)
                    .status(StudyRequestStatus.CONSIDERING)
                    .receiver(member1)
                    .study(study)
                    .message("스터디 같이 하고 싶어요!")
                    .build(),
                StudySuggestion.builder()
                    .id(1L)
                    .status(StudyRequestStatus.CONSIDERING)
                    .receiver(member2)
                    .study(study)
                    .message("스터디 참여 하고 싶어요!")
                    .build()
            );

            given(studySuggestionRepository.findAllByConditions(
                any(GetStudySuggestionsRequestDto.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(studySuggestions));

            // when
            StudySuggestionsResponseDto studySuggestionsResponseDto = studySuggestionService
                .getStudySuggestions(getStudySuggestionsRequestDto, pageRequest);

            // then
            assertEquals(studySuggestionsResponseDto.studySuggestions().size(), 2);
        }
    }

    @Nested
    @DisplayName("approve()는 ")
    class Context_approve {

        @Test
        @DisplayName("스터디 참여 제안을 승인할 수 있다.")
        void _willSuccess() {
            // given
            given(studySuggestionRepository.findById(any(long.class))).willReturn(
                Optional.of(newStudySuggestion()));

            // when
            StudySuggestionResponseDto studySuggestionResponseDto = studySuggestionService.approve(
                1L,
                1L
            );

            // then
            assertThat(studySuggestionResponseDto).extracting("studySuggestionId")
                .isEqualTo(1L);

            verify(studySuggestionRepository, times(1))
                .findById(any(long.class));
        }

        @Test
        @DisplayName("스터디 참여 제안을 찾을 수 없으면 스터디 참여 제안을 승인할 수 없다.")
        void _study_application_not_found_willFail() {
            // given
            given(studySuggestionRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when then
            Throwable exception = assertThrows(StudySuggestionNotFoundException.class, () -> {
                studySuggestionService.approve(1L, 1L);
            });

            assertEquals("존재하지 않는 스터디 제안입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("삭제된 스터디면 스터디 참여 제안을 승인할 수 없다.")
        void _study_deleted_willFail() {
            // given
            StudySuggestion studySuggestion = newStudySuggestion();
            studySuggestion.getStudy().delete(LocalDateTime.now());

            given(studySuggestionRepository.findById(any(Long.class))).willReturn(
                Optional.of(studySuggestion));

            // when then
            Throwable exception = assertThrows(StudyDeleteException.class, () -> {
                studySuggestionService.approve(1L, 1L);
            });

            assertEquals("삭제된 스터디 모집글입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("스터디 참여 제안 수신자가 아니면 스터디 참여 제안을 승인할 수 없다.")
        void _not_study_suggestion_receiver_willFail() {
            // given
            given(studySuggestionRepository.findById(any(Long.class)))
                .willReturn(Optional.of(newStudySuggestion()));

            // when then
            Throwable exception = assertThrows(NotStudySuggestionReceiverException.class, () -> {
                studySuggestionService.approve(2L, 1L);
            });

            assertEquals("해당 제안을 승인하거나 거부할 권한이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("reject()는 ")
    class Context_reject {

        @Test
        @DisplayName("스터디 참여 제안을 거부할 수 있다.")
        void _willSuccess() {
            // given
            given(studySuggestionRepository.findById(any(long.class))).willReturn(
                Optional.of(newStudySuggestion()));

            // when
            StudySuggestionResponseDto studySuggestionResponseDto = studySuggestionService.reject(
                1L,
                1L
            );

            // then
            assertThat(studySuggestionResponseDto).extracting("studySuggestionId")
                .isEqualTo(1L);

            verify(studySuggestionRepository, times(1))
                .findById(any(long.class));
        }

        @Test
        @DisplayName("스터디 참여 제안을 찾을 수 없으면 스터디 참여 제안을 거부할 수 없다.")
        void _study_application_not_found_willFail() {
            // given
            given(studySuggestionRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when then
            Throwable exception = assertThrows(StudySuggestionNotFoundException.class, () -> {
                studySuggestionService.reject(1L, 1L);
            });

            assertEquals("존재하지 않는 스터디 제안입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("삭제된 스터디면 스터디 참여 제안을 거부할 수 없다.")
        void _study_deleted_willFail() {
            // given
            StudySuggestion studySuggestion = newStudySuggestion();
            studySuggestion.getStudy().delete(LocalDateTime.now());

            given(studySuggestionRepository.findById(any(Long.class))).willReturn(
                Optional.of(studySuggestion));

            // when then
            Throwable exception = assertThrows(StudyDeleteException.class, () -> {
                studySuggestionService.reject(1L, 1L);
            });

            assertEquals("삭제된 스터디 모집글입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("스터디 참여 제안 수신자가 아니면 스터디 참여 제안을 거부할 수 없다.")
        void _not_study_suggestion_receiver_willFail() {
            // given
            given(studySuggestionRepository.findById(any(Long.class)))
                .willReturn(Optional.of(newStudySuggestion()));

            // when then
            Throwable exception = assertThrows(NotStudySuggestionReceiverException.class, () -> {
                studySuggestionService.reject(2L, 1L);
            });

            assertEquals("해당 제안을 승인하거나 거부할 권한이 없습니다.", exception.getMessage());
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

    private StudySuggestion newStudySuggestion() {
        return StudySuggestion.builder()
            .id(1L)
            .status(StudyRequestStatus.CONSIDERING)
            .receiver(newMember())
            .study(newStudy())
            .message("스터디 같이 해요")
            .build();
    }
}
