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
import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.SuggestStudyResponseDto;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.entity.StudyRequestStatus;
import com.fasttime.domain.study.entity.StudySuggestion;
import com.fasttime.domain.study.exception.NotStudyWriterException;
import com.fasttime.domain.study.exception.StudyDeleteException;
import com.fasttime.domain.study.exception.StudyNotFoundException;
import com.fasttime.domain.study.repository.StudyRepository;
import com.fasttime.domain.study.repository.StudySuggestionRepository;
import com.fasttime.domain.study.service.StudySuggestionServiceImpl;
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
            SuggestStudyResponseDto suggestStudyResponseDto = studySuggestionService.suggest(
                1L,
                2L,
                1L,
                suggestStudyRequestDto
            );

            // then
            assertThat(suggestStudyResponseDto).extracting("studySuggestionId")
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
